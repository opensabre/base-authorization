package io.github.opensabre.authorization.rest;

import cn.hutool.core.util.StrUtil;
import io.github.opensabre.authorization.entity.ScopeWithDescription;
import io.github.opensabre.authorization.entity.User;
import io.github.opensabre.authorization.oauth2.login.LoginSecurityService;
import io.github.opensabre.authorization.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AuthorizationController {

    @Resource
    private RegisteredClientRepository registeredClientRepository;

    @Resource
    private OAuth2AuthorizationConsentService authorizationConsentService;

    @Resource
    private IUserService userService;
    @Resource
    private LoginSecurityService loginSecurityService;

    /**
     * 登陆页面
     *
     * @return 登陆页面
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "error", required = false) String error,
                        Model model) {
        model.addAttribute("username", username);
        model.addAttribute("captchaRequired", loginSecurityService.isCaptchaRequired(username));
        model.addAttribute("loginErrorMessage", resolveLoginErrorMessage(error));
        return "login";
    }

    /**
     * 用户个人主页
     *
     * @param principal 认证信息
     * @param model     页面model
     * @return 用户个人主页
     */
    @GetMapping({"/", "/profile"})
    public String profile(Authentication principal, Model model) {
        User user = userService.getByUniqueId(principal.getName());
        String displayName = user != null && StrUtil.isNotBlank(user.getName()) ? user.getName() : principal.getName();
        Set<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        model.addAttribute("user", user);
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("displayName", displayName);
        model.addAttribute("avatarText", StrUtil.subPre(displayName, 1).toUpperCase());
        model.addAttribute("authorities", authorities);
        return "profile";
    }

    /**
     * 授权确认页面
     *
     * @param principal 认证信息
     * @param model     页面model
     * @param clientId  客户端ID
     * @param scope     授权范围
     * @param state     状态信息
     * @param userCode  设备码
     * @return 授权确认页面
     */
    @GetMapping(value = "/oauth2/consent")
    public String consent(Principal principal,
                          Model model,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state,
                          @RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {
        // Remove scopes that were already approved，需要授权的scope
        Set<String> scopesToApprove = new HashSet<>();
        // 已授权的scope
        Set<String> previouslyApprovedScopes = new HashSet<>();
        // 查询client信息
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        Assert.notNull(registeredClient, clientId + ":客户端不存在");
        // 查询client授权的scope信息
        OAuth2AuthorizationConsent currentAuthorizationConsent = this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        // 已授予的scope
        Set<String> authorizedScopes = Objects.isNull(currentAuthorizationConsent) ? Collections.emptySet() : currentAuthorizationConsent.getScopes();
        //
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", withDescription(scopesToApprove));
        model.addAttribute("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("userCode", userCode);
        model.addAttribute("requestURI", StringUtils.hasText(userCode) ? "/oauth2/device_verification" : "/oauth2/authorize");
        return "consent";
    }

    /**
     * 设备验证页面
     *
     * @param userCode 用户码
     * @return 设备验证页面
     */
    @GetMapping("/oauth2/activate")
    public String activate(@RequestParam(value = "user_code", required = false) String userCode) {
        return StrUtil.isNotBlank(userCode) ? "redirect:/oauth2/device_verification?user_code=" + userCode : "device-activate";
    }

    /**
     * 设备验证成功页面
     *
     * @return 验证成功页面
     */
    @GetMapping(value = "/oauth2/activated", params = "success")
    public String activated() {
        return "device-activated";
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        return scopes.stream().map(ScopeWithDescription::new).collect(Collectors.toSet());
    }

    private static String resolveLoginErrorMessage(String error) {
        if (!StringUtils.hasText(error)) {
            return null;
        }
        if ("captcha".equals(error)) {
            return "图形验证码错误，请重新输入。";
        }
        if ("LockedException".equals(error)) {
            return "账户已被锁定，请联系管理员。";
        }
        if ("DisabledException".equals(error)) {
            return "账户已被禁用，请联系管理员。";
        }
        if ("AccountExpiredException".equals(error)) {
            return "账户已过期，请联系管理员。";
        }
        return "账号或密码错误，请重新输入。";
    }
}

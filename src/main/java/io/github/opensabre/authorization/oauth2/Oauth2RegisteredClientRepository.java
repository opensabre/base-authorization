package io.github.opensabre.authorization.oauth2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.opensabre.authorization.dao.RegisteredClientMapper;
import io.github.opensabre.authorization.entity.RegisteredClientConvert;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.annotation.Resource;

@Configuration
public class Oauth2RegisteredClientRepository implements RegisteredClientRepository {

    @Resource
    RegisteredClientMapper registeredClientMapper;

    @Resource
    RegisteredClientConvert registeredClientConvert;

    @Override
    public void save(RegisteredClient registeredClient) {
        RegisteredClientPo registeredClientPo = registeredClientConvert.convertToRegisteredClientPo(registeredClient);
        registeredClientMapper.insert(registeredClientPo);
    }

    @Override
    public RegisteredClient findById(String id) {
        RegisteredClientPo registeredClientPo = registeredClientMapper.selectById(id);
        return registeredClientConvert.convertToRegisteredClient(registeredClientPo);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        QueryWrapper<RegisteredClientPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("client_id", clientId);
        RegisteredClientPo registeredClientPo = registeredClientMapper.selectOne(queryWrapper);
        return registeredClientConvert.convertToRegisteredClient(registeredClientPo);
    }

}

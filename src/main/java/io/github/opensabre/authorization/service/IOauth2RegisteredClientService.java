package io.github.opensabre.authorization.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.RegisteredClientQueryParam;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.authorization.entity.vo.RegisteredClientVo;

public interface IOauth2RegisteredClientService {
    /**
     * 新增
     *
     * @param registeredClientPo Client表单对象
     */
    boolean add(RegisteredClientPo registeredClientPo);

    /**
     * 修改客户端信息
     *
     * @param registeredClientPo Client表单对象，须有id字段
     */
    boolean update(RegisteredClientPo registeredClientPo);

    /**
     * 查询所有Client列表
     *
     * @param registeredClientQueryParam 查询参数
     * @return IPage<RegisteredClientVo> Client列表
     */
    IPage<RegisteredClientVo> query(Page page, RegisteredClientQueryParam registeredClientQueryParam);

    /**
     * 根据clientId获取对象
     *
     * @param clientId clientId
     * @return RegisteredClientPo
     */
    RegisteredClientPo getByClientId(String clientId);

    /**
     * 根据id获取对象
     *
     * @param id 唯一id
     * @return RegisteredClientPo
     */
    RegisteredClientPo get(String id);

    /**
     * 失效client
     *
     * @param id 唯一id
     */
    boolean disable(String id);
}

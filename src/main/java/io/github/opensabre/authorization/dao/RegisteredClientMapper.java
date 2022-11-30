package io.github.opensabre.authorization.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RegisteredClientMapper extends BaseMapper<RegisteredClientPo> {
}
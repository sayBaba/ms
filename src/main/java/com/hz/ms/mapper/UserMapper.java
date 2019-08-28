package com.hz.ms.mapper;

import com.hz.ms.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
@Repository
public interface UserMapper {

    /**
     * 根据手机号查询用户信息
     * @param map
     * @return
     */
    User checkPhone(@Param("map") Map<String,String> map);
}

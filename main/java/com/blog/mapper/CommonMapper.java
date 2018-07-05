package com.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2018/5/10.
 */
@Mapper
public interface CommonMapper {
    public List<LinkedHashMap<String, Object>> findList(@Param("sqlStr") String sqlStr);
    public void executeSql(@Param("sqlStr") String sqlStr);
    public int queryInt(@Param("sqlStr") String sqlStr);
    public String queryString(@Param("sqlStr") String sqlStr);
    public int executeTransition_update(@Param("sqlStr") String sqlStr);
    public int executeTransition_insert(@Param("sqlStr") String sqlStr);
    public int test( @Param("password") String password, @Param("loginid") String  loginid);
    public void insertBatch(@Param("params") Map params);
}

package com.lagou.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * @Author ascetic
     * @Description 查询所有
     * @Date 12:00 2020-04-28
     * @Param [statementid , params]
     * @return java.util.List<E>
     **/
    public <E> List<E> selectList(String statementid,Object... params) throws Exception;

    /**
     * @Author ascetic
     * @Description 根据条件查询单个
     * @Date 12:01 2020-04-28
     * @Param [statementid, params]
     * @return T
     **/
    public <T> T selectOne(String statementid,Object... params) throws Exception;


    /**
     * @Author ascetic
     * @Description 无参数插入
     * @Date 12:01 2020-04-28
     * @Param [statement]
     * @return int 返回受影响的行数
     **/
    Integer insert(String statement) throws Exception;

    /**
     * @Author ascetic
     * @Description 带参数插入
     * @Date 12:02 2020-04-28
     * @Param [statement, parameter]
     * @return int 返回受影响的行数
     **/
    Integer insert(String statement, Object... params) throws Exception;

    /**
     * @Author ascetic
     * @Description 无参数修改
     * @Date 12:03 2020-04-28
     * @Param [statement]
     * @return int 返回受影响的行数
     **/
    Integer update(String statement) throws Exception;

    /**
     * @Author ascetic
     * @Description 带参数修改
     * @Date 12:03 2020-04-28
     * @Param [statement, parameter]
     * @return int 返回受影响的行数
     **/
    Integer update(String statement, Object... params) throws Exception;

    /**
     * @Author ascetic
     * @Description 无参数删除
     * @Date 12:03 2020-04-28
     * @Param [statement]
     * @return int 返回受影响的行数
     **/
    Integer delete(String statement) throws Exception;

    /**
     * @Author ascetic
     * @Description 带参数删除
     * @Date 12:04 2020-04-28
     * @Param [statement, parameter]
     * @return int 返回受影响的行数
     **/
    Integer delete(String statement, Object... params) throws Exception;


    /**
     * @Author ascetic
     * @Description 为Dao接口生成代理实现类
     * @Date 12:04 2020-04-28
     * @Param [mapperClass]
     * @return T 返回Mapper 代理对象
     **/
    public <T> T getMapper(Class<?> mapperClass);


}

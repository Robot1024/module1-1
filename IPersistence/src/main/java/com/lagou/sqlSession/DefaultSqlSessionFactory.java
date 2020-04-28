package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public SqlSession openSession() {
        //先默认使用 simpleExecutor ，这个以后也可以通过配置文件选择初始不同的Exector
        SimpleExecutor simpleExecutor = new SimpleExecutor();

        return new DefaultSqlSession(configuration,simpleExecutor);
    }
}

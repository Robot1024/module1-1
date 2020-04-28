package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration,Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <E> List<E> selectList(String statementid, Object... params) throws Exception {

        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        List<Object> list = executor.query(configuration, mappedStatement, params);

        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementid, Object... params) throws Exception {
        List<Object> objects = selectList(statementid, params);

        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else if (objects.size() > 1) {
            // TODO 6 objects.size() == 0 的时候，这时候应该返回  null
            throw new RuntimeException("期望返回一个结果 （或者 null）被返回,但是发现："+objects.size());
        } else {
            return null;
        }


    }

    @Override
    public Integer insert(String statementid) throws Exception {
        return  insert(statementid,null);
    }


    @Override
    public Integer insert(String statementid, Object... params) throws Exception{


        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        int row = executor.update(configuration,mappedStatement,params);
        return row > 0 ? 1 :0 ;
    }

    @Override
    public Integer update(String statementid) throws Exception{
        return update(statementid,null);
    }

    @Override
    public Integer update(String statementid, Object... params) throws Exception{

        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        int row = executor.update(configuration,mappedStatement,params);
        return row > 0 ? 1 :0 ;
    }

    @Override
    public Integer delete(String statementid) throws Exception{
        return delete(statementid,null);
    }

    @Override
    public Integer delete(String statementid, Object... params) throws Exception{
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        int row = executor.update(configuration,mappedStatement,params);
        return row > 0 ? 1 :0 ;
    }


    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象，并返回

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码 //根据不同情况，来调用selctList或者selectOne
                // 准备参数 1：statmentid :sql语句的唯一标识：namespace.id= 接口全限定名.方法名
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statementId = className+"."+methodName;

                // 准备参数2：params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                String node = configuration.getMappedStatementMap().get(statementId).getNode();
                if("select".equals(node)){
                    // 判断是否进行了 泛型类型参数化
                    if(genericReturnType instanceof ParameterizedType){
                        List<Object> objects = selectList(statementId, args);
                        return objects;
                    }else {
                        //TODO  6 如果返回值是 void 类型，或者是  int 类型
                        return selectOne(statementId,args);
                    }
                }else if("insert".equals(node)){
                    if(args.length > 0 ){
                        return insert(statementId,args);
                    }else {
                        return insert(statementId);
                    }

                }else if("update".equals(node)){
                    if(args.length > 0){
                        return update(statementId,args);
                    }else {
                        return update(statementId);
                    }

                }else if("delete".equals(node)){
                    if(args.length > 0){
                        return delete(statementId,args) ;
                    }else {
                        return delete(statementId);
                    }

                }else {
                    throw new RuntimeException("期望返回一个结果 （或者 null）被返回,但是发现：");
                }

            }
        });

        return (T) proxyInstance;
    }


}

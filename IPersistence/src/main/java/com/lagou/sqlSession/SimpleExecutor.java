package com.lagou.sqlSession;


import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements  Executor {

    /**
     * @Author ascetic
     * @Description 获取BoundSql
     * @Date 13:17 2020-04-28
     * @Param [mappedStatement]
     * @return com.lagou.config.BoundSql
     **/
    private BoundSql getBoundSql(MappedStatement mappedStatement){

        // 2. 获取sql语句 : select * from user where id = #{id} and username = #{username}
        //转换sql语句： select * from user where id = ? and username = ? ，转换的过程中，还需要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        return boundSql;

    }
    
    /**
     * @Author ascetic
     * @Description 负责处理JDBC的 Statement的交互，负责动态Sql语句的生产 和 查询缓存的维护
     * @Date 12:51 2020-04-28
     * @Param [configuration, mappedStatement, params]
     * @return void
     **/
    private  Object doExcutor(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        // TODO 暂时没有动态SqL功能，没有缓存功能

        // 1. 注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(getBoundSql(mappedStatement).getSqlText());

        return statementHandler(mappedStatement,preparedStatement,params);
    }

    private  Object statementHandler(MappedStatement mappedStatement,PreparedStatement preparedStatement, Object... params) throws Exception {

        // 设置参数
        this.parameterHandler(mappedStatement,preparedStatement,params);
        if(mappedStatement.getIsQuery()){
            return resultHandler(mappedStatement,preparedStatement.executeQuery());
        }else {
            return preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement  parameterHandler(MappedStatement mappedStatement,PreparedStatement preparedStatement,Object... params) throws Exception {
        // 4. 设置参数
        //获取到了参数的全路径
        String paramterType = mappedStatement.getParamterType();
        Class<?> paramtertypeClass = getClassType(paramterType);

        List<ParameterMapping> parameterMappingList = getBoundSql(mappedStatement).getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();


            if(isWrapClass(paramtertypeClass) || paramtertypeClass.getName().equals("java.lang.String")){

                preparedStatement.setObject(i+1,params[0]);

            }else {
                //反射
                Field declaredField = paramtertypeClass.getDeclaredField(content);
                //暴力访问
                declaredField.setAccessible(true);
                Object o = declaredField.get(params[0]);

                preparedStatement.setObject(i+1,o);
            }


        }
        return preparedStatement;
    }

    public  <E> List<E> resultHandler(MappedStatement mappedStatement,ResultSet resultSet) throws Exception {

        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        ArrayList<Object> objects = new ArrayList<>();

        // 6. 封装返回结果集
        while (resultSet.next()){
            Object o =resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {

                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);


            }
            objects.add(o);

        }
        return (List<E>) objects;

    }

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        return (List<E>) doExcutor(configuration,mappedStatement,params);

    }

    @Override
    public Integer update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        return (Integer) doExcutor(configuration,mappedStatement,params);
    }

    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if(paramterType!=null){
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }
         return null;

    }





    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
         return boundSql;

    }

    /**
     * @Author ascetic
     * @Description 判断是不是java 基本包装数据类型，此处不考虑是基本类型情况
     * @Date 16:56 2020-04-28
     * @Param [clz]
     * @return boolean
     **/
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }




}

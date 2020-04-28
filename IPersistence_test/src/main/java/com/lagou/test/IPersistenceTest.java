package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {


    public SqlSession sqlSession;
    public IUserDao userDao;


    @Before
    public void befor() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        sqlSession = sqlSessionFactory.openSession();
        userDao = sqlSession.getMapper(IUserDao.class);
    }

    @Test
    public void test() throws Exception {

        //调用 非mapper 代理方式
        User user = new User();
        user.setId(1);
        user.setUsername("lucc");

        User user2 = sqlSession.selectOne("com.lagou.dao.IUserDao.findByCondition", user);
        System.out.println(user2);


    }

    @Test
    public void test1() throws Exception{

        // mapper 代理方式 findAll

        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1);
        }

    }

    @Test
    public void test2() throws Exception {

        // mapper 代理方式 insert
        User user = new User();
        user.setId(16);
        user.setUsername("insert");
        user.setBirthday("1990-01-01");
        user.setPassword("123456");
        userDao.userInsert(user);

        System.out.println(userDao.findByCondition(user));


    }


    @Test
    public void test3() throws Exception {

        // mapper 代理方式 update
        User user = new User();
        user.setId(16);
        user.setUsername("update");
        user.setBirthday("1990-01-01");
        user.setPassword("654321");
        userDao.userUpdate(user);

        System.out.println(userDao.findByCondition(user));


    }

    @Test
    public void test4() throws Exception {


        // mapper 代理方式 update
        User user = new User();
        user.setId(16);
        user.setUsername("update");
        System.out.println(userDao.findByCondition(user));
        userDao.userDeleteById(16);
        System.out.println(userDao.findByCondition(user));


    }



}

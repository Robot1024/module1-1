package com.lagou.config;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration =configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {

        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        String namespace = rootElement.attributeValue("namespace");

        List<Element> querylist = rootElement.selectNodes("//select");

        statementMapParse(querylist,namespace,true,"select");



        //TODO 1 List<Element> list = rootElement.selectNodes("//insert");
        List<Element> insertList = rootElement.selectNodes("//insert");
        statementMapParse(insertList,namespace,false,"insert");


        //TODO 2 List<Element> list = rootElement.selectNodes("//update");
        List<Element> updateList = rootElement.selectNodes("//update");
        statementMapParse(updateList,namespace,false,"update");

        //TODO 3 List<Element> list = rootElement.selectNodes("//delete");
        List<Element> deleteList = rootElement.selectNodes("//delete");
        statementMapParse(deleteList,namespace,false,"delete");

    }

    public void statementMapParse(List<Element> list,String namespace,Boolean isQuery,String node){

        for (Element element : list) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String sqlText = element.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSql(sqlText);
            mappedStatement.setIsQuery(isQuery);
            mappedStatement.setNode(node);
            String key = namespace+"."+id;
            configuration.getMappedStatementMap().put(key,mappedStatement);

        }

    }


}

package com.blog.util;

import com.blog.entity.Sys_Config;
import com.blog.mapper.CommonMapper;
import com.github.pagehelper.StringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by paul on 2018/5/10.
 */
//如果使用工具类中的方法报nullpointexception，说明方法类中的dao接口层未注入到spring，需要构造init方法初始化dao接口内部调用
@Component
public class CommonDao {
    @Autowired
    public CommonMapper icommonMapper;
    public static CommonDao commonDao;

    @PostConstruct
    public void init() {
        commonDao = this;
    }

    //select
    public List findList(String sql, Class entityClass) throws Exception{
        List<LinkedHashMap<String, Object>> list= commonDao.icommonMapper.findList(sql);
        List returnlist = new ArrayList();
        for(LinkedHashMap<String, Object> map:list){
            Object obj = entityClass.newInstance();
            Field[] fs = entityClass.getDeclaredFields();
            for(Field f :fs){
                f.setAccessible(true);
                f.set(obj,map.get(f.getName()));
            }
            returnlist.add(obj);
        }
        return returnlist;
    }
    //select page
    public PageBean<Object> findListWithPage(String sql, Class entityClass, int pageNum, int pageSize) throws Exception{
        int count = icommonMapper.queryInt(MessageFormat.format("select count(1) from {0}",getTableNameWithClassName(entityClass)));
        List<Object> list = findList(sql,entityClass);
        PageBean<Object> pageData = new PageBean<Object>(pageNum, pageSize,count);
        pageData.setItems(list);
        return pageData;
    }
    public Object FindEntity(String sql, Class entityClass) throws Exception{
        List returnlist = findList(sql,entityClass);
        if (returnlist.size()>0){
            return returnlist.get(0);
        }else{
            return null;
        }
    }
    public Object FindEntityWithRowGuid(String tableName,String rowGuid, Class entityClass) throws Exception{
        String sql = MessageFormat.format("select * from {0} where rowguid={1}",tableName,"'"+rowGuid+"'");
        List returnlist = findList(sql,entityClass);
        if (returnlist.size()>0){
            return returnlist.get(0);
        }else{
            return null;
        }
    }


    //insert
    public void insert(Object entity){
        List<Field> fs = Arrays.asList(entity.getClass().getDeclaredFields());
        StringBuilder sqlbuilder = new StringBuilder();
        StringBuilder fieldbuilder = new StringBuilder();
        StringBuilder valuebuilder = new StringBuilder();
        //去除反射的类的包名
        String tablename = entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
        sqlbuilder.append(MessageFormat.format("insert into {0}",tablename));
        fieldbuilder.append("(");
        valuebuilder.append("(");
        try {
            for(Field f :fs){

                if (fs.indexOf(f)==fs.size()-1){
                    fieldbuilder.append(f.getName().toString());
                    valuebuilder.append(MessageFormat.format("{0}{1}{2}","'",f.get(entity).toString(),"'"));
                }else{
                    fieldbuilder.append(f.getName().toString()+",");
                    valuebuilder.append(MessageFormat.format("{0}{1}{2},","'",f.get(entity).toString(),"'"));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        fieldbuilder.append(")");
        valuebuilder.append(")");
        sqlbuilder.append(" "+fieldbuilder.toString());
        sqlbuilder.append(" values "+valuebuilder.toString());
        //System.out.println(sqlbuilder.toString());
        commonDao.icommonMapper.executeSql(sqlbuilder.toString());
    }
    public String getInsertSql(Object entity){
        List<Field> fs = Arrays.asList(entity.getClass().getDeclaredFields());
        StringBuilder sqlbuilder = new StringBuilder();
        StringBuilder fieldbuilder = new StringBuilder();
        StringBuilder valuebuilder = new StringBuilder();
        //去除反射的类的包名
        String tablename = entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
        sqlbuilder.append(MessageFormat.format("insert into {0}",tablename));
        fieldbuilder.append("(");
        valuebuilder.append("(");
        try {
            for(Field f :fs){

                if (fs.indexOf(f)==fs.size()-1){
                    fieldbuilder.append(f.getName().toString());
                    valuebuilder.append(MessageFormat.format("{0}{1}{2}","'",f.get(entity).toString(),"'"));
                }else{
                    fieldbuilder.append(f.getName().toString()+",");
                    valuebuilder.append(MessageFormat.format("{0}{1}{2},","'",f.get(entity).toString(),"'"));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        fieldbuilder.append(")");
        valuebuilder.append(")");
        sqlbuilder.append(" "+fieldbuilder.toString());
        sqlbuilder.append(" values "+valuebuilder.toString());
        //System.out.println(sqlbuilder.toString());
        return sqlbuilder.toString();
    }
    public void insertBatch(List entityList) throws Exception{
        Map<String,Object> params = new HashedMap();
        String tablename =getTableNameWithEntity(entityList.get(0));
        List<Field> fields = getFileListWithEntity(entityList.get(0));
        List<Map<String,Object>> batchlist = GetBatchList(entityList);
        int count = batchlist.size() / 1000;
        int remainder = batchlist.size() % 1000;
        for (int i = 0; i <= count; i++) {
            List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>();
            if (i == count) {
                if(remainder != 0){
                    subList = batchlist.subList(i * 1000, 1000 * i + remainder);
                }else {
                    continue;
                }
            } else {
                subList = batchlist.subList(i * 1000, 1000 * (i + 1));
            }
            params.put("table_name", tablename);
            params.put("fields", subList.get(0));
            params.put("list", subList);
            commonDao.icommonMapper.insertBatch(params);
        }
    }
    //批量插入

    //update
    public void update(Object entity,String Key) throws Exception{
        List<Field> fs = Arrays.asList(entity.getClass().getDeclaredFields());
        StringBuilder sqlbuilder = new StringBuilder();
        StringBuilder fieldbuilder = new StringBuilder();
        //去除反射的类的包名
        String tablename = entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
        sqlbuilder.append(MessageFormat.format("update {0} set ",tablename));
        for(Field f :fs){
            if (fs.indexOf(f)==fs.size()-1){
                fieldbuilder.append(MessageFormat.format("{0}={1}{2}{3}",f.getName(),"'",f.get(entity).toString(),"'"));
            }else{
                fieldbuilder.append(MessageFormat.format("{0}={1}{2}{3},",f.getName(),"'",f.get(entity).toString(),"'"));
            }
        }
        for(Field f :fs){
            if (f.getName().equals(Key)){
                fieldbuilder.append(MessageFormat.format(" where {0}={1}",Key,"'"+f.get(entity).toString()+"'"));
            }
        }
        sqlbuilder.append(" "+fieldbuilder.toString());
        //System.out.println(sqlbuilder.toString());
        commonDao.icommonMapper.executeSql(sqlbuilder.toString());
    }
    public String getUpdateSql(Object entity,String Key) throws Exception{
        List<Field> fs = Arrays.asList(entity.getClass().getDeclaredFields());
        StringBuilder sqlbuilder = new StringBuilder();
        StringBuilder fieldbuilder = new StringBuilder();
        //去除反射的类的包名
        String tablename = entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
        sqlbuilder.append(MessageFormat.format("update {0} set ",tablename));
        for(Field f :fs){
            if (fs.indexOf(f)==fs.size()-1){
                fieldbuilder.append(MessageFormat.format("{0}={1}{2}{3}",f.getName(),"'",f.get(entity).toString(),"'"));
            }else{
                fieldbuilder.append(MessageFormat.format("{0}={1}{2}{3},",f.getName(),"'",f.get(entity).toString(),"'"));
            }
        }
        for(Field f :fs){
            if (f.getName().equals(Key)){
                fieldbuilder.append(MessageFormat.format(" where {0}={1}",Key,"'"+f.get(entity).toString()+"'"));
            }
        }
        sqlbuilder.append(" "+fieldbuilder.toString());
        //System.out.println(sqlbuilder.toString());
        return sqlbuilder.toString();
    }

    //delete
    public void delete(Object entity,String Key){
        String tablename = entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
        String sql = MessageFormat.format("delete from {0} where rowguid={1}",tablename,"'"+Key+"'");
        commonDao.icommonMapper.executeSql(sql);
    }

    public void executemutilsql(List<String> sqllist){
        StringBuilder allsql = new StringBuilder();
        for (int i=0;i<sqllist.size();i++){
            allsql.append(sqllist.get(i));
            if (i<sqllist.size()-1){
                allsql.append(";");
            }
        }
        commonDao.icommonMapper.executeSql(allsql.toString());
    }

    public String typeMapper(String databasetype){
        String returntype = "";
        switch (databasetype.toLowerCase()){
            case "nvarchar":
            case "longtext":
            case "varchar":
                returntype = "String";
                break;
            case "int":
                returntype = "int";
                break;
            case "datetime":
                returntype="Date";
                break;
            case "blob":
                returntype = "byte[]";
            case "numeric":
                returntype = "BigDecimal";
                break;

        }
        return returntype;
    }

    public String getTableNameWithClassName(Class entityClass) throws Exception{
        Object entity = entityClass.newInstance();
        return entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
    }

    public String getTableNameWithEntity(Object entity) throws Exception{
        return entity.getClass().getName().replace(entity.getClass().getPackage().getName()+".","");
    }

    public List<Field> getFileListWithEntity(Object entity) throws Exception{
        return  Arrays.asList(entity.getClass().getDeclaredFields());
    }

    public Map<String,Object> convertEntityToMap(Object entity) throws Exception{
        Map returnMap = new HashMap();
        List<Field> fs = Arrays.asList(entity.getClass().getDeclaredFields());
        for (Field f:fs){
            String filedname = f.getName().toString();
            Object value = f.get(entity);
            returnMap.put(filedname,value);
        }
        return returnMap;
    }

    public List<Map<String,Object>> GetBatchList(List<Object> entityList) throws Exception{
        List<Map<String,Object>> insertItems = new ArrayList<Map<String,Object>>();
        for (Object entity : entityList) {
            Map<String,Object> insertItem = convertEntityToMap(entity);
            insertItems.add(insertItem);
        }
        return insertItems;
    }
}

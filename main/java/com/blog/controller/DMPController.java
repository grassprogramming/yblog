package com.blog.controller;

import com.blog.entity.Sys_Table;
import com.blog.entity.Sys_TableStruct;
import com.blog.mapper.CommonMapper;
import com.blog.service.CommonDaoService;
import com.blog.util.CommonDao;
import com.blog.util.FileUtil;
import com.blog.util.PageBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by paul on 2018/6/5.
 */
@RestController
@MapperScan("com.blog.mapper")
public class DMPController {
    public CommonDao commonDao = new CommonDao();
    @Autowired
    public CommonDaoService commonDaoService;
    @Autowired
    private CommonMapper icommonMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public final String tablename = "sys_table";
    public final  String subtablename = "sys_tablestruct";
    @RequestMapping(value="/dmp/tablelist",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablelist(@RequestBody Map<String,Object> reqMap) throws  Exception{
        int pageNum =Integer.parseInt(reqMap.get("pageNum").toString());
        int pageSize =Integer.parseInt(reqMap.get("pageSize").toString());
        String searchConfigName = reqMap.get("searchConfigName").toString();
        int count = icommonMapper.queryInt("select count(1) from "+tablename);
        PageHelper.startPage(pageNum, pageSize);
        //数据库操作要紧随分页事件后，有且只有后面的一个个sql会被分页
        String sql = MessageFormat.format("select * from {0} where 1=1 ",tablename);
        if (!StringUtil.isEmpty(searchConfigName)){
            sql += MessageFormat.format(" and tablename like {0}","'%"+searchConfigName+"%'");
        }
        sql +=" order by ordernum desc";
        List<Sys_Table> list = commonDao.findList(sql,Sys_Table.class);
        PageBean<Sys_Table> pageData = new PageBean<Sys_Table>(pageNum, pageSize,count);
        pageData.setItems(list);
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",list);
        map.put("currentPage",pageNum);
        map.put("pagesize",pageSize);
        map.put("totalnum",pageData.getTotalNum());
        return map;
    }

    @RequestMapping(value="/dmp/tableadd",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tableadd(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String tablecode = reqMap.get("tablecode").toString();
            String tablename = reqMap.get("tablename").toString();
            String sqltablename = reqMap.get("sqltablename").toString();
            int ordernum = Integer.parseInt(reqMap.get("ordernum").toString());
            Sys_Table sys_table = new Sys_Table();
            sys_table.setTablecode(tablecode);
            sys_table.setTablename(tablename);
            sys_table.setSqltablename(sqltablename);
            sys_table.setTabletype("");
            sys_table.setOrdernum(ordernum);
            //建立表
            String createtablesql = MessageFormat.format("CREATE TABLE {0} (rowguid VARCHAR(50))",sqltablename);
            //建立表结构，增加一个rowguid字段
            Sys_TableStruct sys_tableStruct = new Sys_TableStruct();
            sys_tableStruct.setFieldname("唯一标识");
            sys_tableStruct.setFieldsqlname("rowguid");
            sys_tableStruct.setFieldlength(50);
            sys_tableStruct.setFieldtype("VARCHAR");
            sys_tableStruct.setOrdernum(0);
            sys_tableStruct.setTablecode(sys_table.getRowguid());
            icommonMapper.executeSql(createtablesql);
            // transition
            List<String> sqllist = new ArrayList<>();
            sqllist.add(commonDao.getInsertSql(sys_table));
            sqllist.add(commonDao.getInsertSql(sys_tableStruct));
            commonDaoService.executeSqlWithTransition(sqllist);
            map.put("executestatus","1");
        }catch (Exception e){
            e.printStackTrace();
            map.put("executestatus","0");
        }
        return map;
    }

    @RequestMapping(value="/dmp/tabledelete",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tabledelete(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            Sys_Table sys_table = (Sys_Table)commonDao.FindEntityWithRowGuid(tablename,rowguid,Sys_Table.class);
            String sql = MessageFormat.format("delete from {0} where rowguid={1}",tablename,"'"+rowguid+"'");
            String fieldsql = MessageFormat.format("delete from {0} where tablecode={1}",subtablename,"'"+rowguid+"'");
            String structsql = MessageFormat.format( "drop table {0}",sys_table.getSqltablename());
            icommonMapper.executeSql(structsql);

            // transition
            List<String> sqllist = new ArrayList<>();
            sqllist.add(sql);
            sqllist.add(fieldsql);
            commonDaoService.executeSqlWithTransition(sqllist);
            map.put("executestatus","1");
        }catch (Exception e){
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/dmp/tablefindone",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablefindone(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            Sys_Table sys_table = (Sys_Table)commonDao.FindEntityWithRowGuid(tablename,rowguid,Sys_Table.class);
            map.put("data",sys_table);
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/dmp/tablesave",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablesave(@RequestBody Sys_Table sys_table){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            commonDao.update(sys_table,"rowguid");
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/dmp/tablestructlist",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablestructlist(@RequestBody Map<String,Object> reqMap,HttpServletRequest request) throws  Exception{
        int pageNum =Integer.parseInt(reqMap.get("pageNum").toString());
        int pageSize =Integer.parseInt(reqMap.get("pageSize").toString());
        String searchConfigName = reqMap.get("searchConfigName").toString();
        String tablecode =  request.getParameter("tablecode");
        String countsql = MessageFormat.format("select count(1) from {0} where 1=1 ",subtablename);
        if (!StringUtil.isEmpty(tablecode)){
            countsql += MessageFormat.format(" and tablecode = {0}",tablecode);
        }
        int count = icommonMapper.queryInt("select count(1) from "+subtablename);
        PageHelper.startPage(pageNum, pageSize);
        //数据库操作要紧随分页事件后，有且只有后面的一个个sql会被分页
        String sql = MessageFormat.format("select * from {0} where 1=1 ",subtablename);
        if (!StringUtil.isEmpty(searchConfigName)){
            sql += MessageFormat.format(" and fieldsqlname like {0}","'%"+searchConfigName+"%'");
        }
        if (!StringUtil.isEmpty(tablecode)){
            sql += MessageFormat.format(" and tablecode = {0}","'"+tablecode+"'");
        }
        sql +=" order by ordernum desc";
        List<Sys_TableStruct> list = commonDao.findList(sql,Sys_TableStruct.class);
        PageBean<Sys_TableStruct> pageData = new PageBean<Sys_TableStruct>(pageNum, pageSize,count);
        pageData.setItems(list);
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",list);
        map.put("currentPage",pageNum);
        map.put("pagesize",pageSize);
        map.put("totalnum",pageData.getTotalNum());
        return map;
    }

    @RequestMapping(value="/dmp/tablestructadd",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablestructadd(@RequestBody Map<String,Object> reqMap,HttpServletRequest request){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String fieldname = reqMap.get("fieldname").toString();
            String fieldsqlname = reqMap.get("fieldsqlname").toString();
            String fieldtype = reqMap.get("fieldtype").toString();
            String tablecode =  request.getParameter("tablecode");
            int ordernum = Integer.parseInt(reqMap.get("ordernum").toString());
            int fieldlength = Integer.parseInt(reqMap.get("fieldlength").toString());
            Sys_TableStruct sys_tableStruct = new Sys_TableStruct();
            sys_tableStruct.setFieldname(fieldname);
            sys_tableStruct.setFieldsqlname(fieldsqlname);
            sys_tableStruct.setFieldtype(fieldtype);
            sys_tableStruct.setOrdernum(ordernum);
            sys_tableStruct.setFieldlength(fieldlength);
            sys_tableStruct.setTablecode(tablecode);
            //数据库加字段
            Sys_Table sys_table = (Sys_Table)commonDao.FindEntityWithRowGuid(tablename,tablecode,Sys_Table.class);
            String fielddefnitionsql = "";
            switch (fieldtype){
                case "nvarchar":
                    fielddefnitionsql = MessageFormat.format("{0}({1})",fieldtype,fieldlength);
                    break;
                case "numeric":
                    fielddefnitionsql = MessageFormat.format("{0}(10,{1})",fieldtype,fieldlength);
                    break;
                case "blob":
                case "longtext":
                case "int":
                case "datetime":
                    fielddefnitionsql = MessageFormat.format("{0}",fieldtype);
                    break;
            }
            String fieldsql =MessageFormat.format( "alter table {0} add {1} {2}",
                    sys_table.getSqltablename(),fieldsqlname,fielddefnitionsql);
            System.out.println(fieldsql);
            // transition
            List<String> sqllist = new ArrayList<>();
            sqllist.add(commonDao.getInsertSql(sys_tableStruct));
            sqllist.add(fieldsql);
            commonDaoService.executeSqlWithTransition(sqllist);

            map.put("executestatus","1");
        }catch (Exception e){
            e.printStackTrace();
            map.put("executestatus","0");
        }
        return map;
    }

    @RequestMapping(value="/dmp/tablestructdelete",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablestructdelete(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            //删除数据库字段
            Sys_TableStruct sys_tableStruct = (Sys_TableStruct)commonDao.FindEntityWithRowGuid(subtablename,rowguid,Sys_TableStruct.class);
            Sys_Table sys_table = (Sys_Table)commonDao.FindEntityWithRowGuid(tablename,sys_tableStruct.getTablecode(),Sys_Table.class);
            String fieldsql =MessageFormat.format( "alter table {0} drop column {1}",
                    sys_table.getSqltablename(),sys_tableStruct.getFieldsqlname());
            icommonMapper.executeSql(fieldsql);
            //删除tablestruct表
            String sql = MessageFormat.format("delete from {0} where rowguid={1}",subtablename,"'"+rowguid+"'");
            icommonMapper.executeSql(sql);
            map.put("executestatus","1");
        }catch (Exception e){
            e.printStackTrace();
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/dmp/tablestructfindone",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablestructfindone(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            Sys_TableStruct sys_tableStruct = (Sys_TableStruct)commonDao.FindEntityWithRowGuid(subtablename,rowguid,Sys_TableStruct.class);
            map.put("data",sys_tableStruct);
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/dmp/tablstructesave",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> tablstructesave(@RequestBody Sys_TableStruct sys_tableStruct){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            List<String> sqllist = new ArrayList<>();
            sqllist.add(commonDao.getUpdateSql(sys_tableStruct,"rowguid"));
            commonDaoService.executeSqlWithTransition(sqllist);
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/dmp/createcod",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> createcod(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            Sys_Table sys_table = (Sys_Table)commonDao.FindEntityWithRowGuid(tablename,rowguid,Sys_Table.class);
            List<Sys_TableStruct> sys_tableStructList = (List<Sys_TableStruct>)commonDao.findList( MessageFormat.format(
                    "select * from {0} where tablecode={1} ",subtablename,"'"+sys_table.getRowguid()+"'"),Sys_TableStruct.class);
            //entity
            String entityfilename = ResourceUtils.getURL("src/main/java/com/blog/gxh/entity/").getPath()+sys_table.getSqltablename()+".java";
            System.out.println(entityfilename);
             StringBuilder entityfilecontent = new StringBuilder();
            entityfilecontent.append("package com.blog.gxh.entity;\n" +
                    "\n" +
                    "import java.io.Serializable;\n" +
                    "import java.util.UUID;" +
                    "import java.math.BigDecimal;\n" +
                    "import java.util.Date;\n");
            entityfilecontent.append(MessageFormat.format("public class {0} implements Serializable ",sys_table.getSqltablename()));
            entityfilecontent.append("{");
            entityfilecontent.append("\n");
            //字段声明
            for(Sys_TableStruct tablestruct:sys_tableStructList){
                entityfilecontent.append("    public ");
                entityfilecontent.append(commonDao.typeMapper(tablestruct.getFieldtype())+" ");
                entityfilecontent.append(tablestruct.getFieldsqlname()+";"+"\n");
            }
            entityfilecontent.append("\n");
            //构造函数
            entityfilecontent.append(MessageFormat.format("    public {0}()",sys_table.getSqltablename()));
            entityfilecontent.append("{");
            entityfilecontent.append( "  rowguid = UUID.randomUUID().toString();");
            entityfilecontent.append("}");

            //get,set方法
            for(Sys_TableStruct tablestruct:sys_tableStructList){
                String camelfileldname = tablestruct.getFieldsqlname().substring(0,1).toUpperCase()+tablestruct.getFieldsqlname().substring(1,tablestruct.getFieldsqlname().length());
                entityfilecontent.append(MessageFormat.format("    public {0} get{1}()",commonDao.typeMapper(tablestruct.getFieldtype()),camelfileldname));
                entityfilecontent.append("{");
                entityfilecontent.append(MessageFormat.format(" return {0};" ,tablestruct.getFieldsqlname()));
                entityfilecontent.append("}");

                entityfilecontent.append("\n\n");
                entityfilecontent.append(MessageFormat.format("    public void set{0}({1} {2})",camelfileldname,commonDao.typeMapper(tablestruct.getFieldtype()),tablestruct.getFieldsqlname()));
                entityfilecontent.append("{");
                entityfilecontent.append(MessageFormat.format(
                        "  this.{0} = {0};",tablestruct.getFieldsqlname()));
                entityfilecontent.append("}");
                entityfilecontent.append("\n\n");

            }
            entityfilecontent.append("}");
            FileUtil.createFile(entityfilename,entityfilecontent.toString());
            map.put("executestatus","1");
        }catch (Exception e){
           e.printStackTrace();
            map.put("executestatus","0");
        }

        return map;
    }

}

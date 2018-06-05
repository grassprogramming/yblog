package com.blog.controller;

import com.blog.entity.Sys_Config;
import com.blog.entity.Sys_Module;
import com.blog.mapper.CommonMapper;
import com.blog.util.CommonDao;
import com.blog.util.PageBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by paul on 2018/6/4.
 */
@RestController
public class ModuleController {
    private CommonDao commonDao = new CommonDao();
    @Autowired
    private CommonMapper icommonMapper;

    @RequestMapping(value="/module/list",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> list(@RequestBody Map<String,Object> reqMap) throws  Exception{
        int pageNum =Integer.parseInt(reqMap.get("pageNum").toString());
        int pageSize =Integer.parseInt(reqMap.get("pageSize").toString());
        int count = icommonMapper.queryInt("select count(1) from sys_module");
        String selectnodevalue = reqMap.get("selectnodevalue").toString();
        PageHelper.startPage(pageNum, pageSize);
        //数据库操作要紧随分页事件后，有且只有后面的一个个sql会被分页
        String sql = "select * from sys_module where 1=1 ";
        if (StringUtil.isNotEmpty(selectnodevalue)){
            sql += MessageFormat.format(" and modulecode like {0} and Length(modulecode)={1}",
                    "'"+selectnodevalue+"%'",selectnodevalue.length()+4);
        }
        sql +=" order by ordernum desc";
        List<Sys_Module> list = commonDao.findList(sql,Sys_Module.class);
        PageBean<Sys_Module> pageData = new PageBean<Sys_Module>(pageNum, pageSize,count);
        pageData.setItems(list);
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",list);
        map.put("currentPage",pageNum);
        map.put("pagesize",pageSize);
        map.put("totalnum",pageData.getTotalNum());
        return map;
    }

    @RequestMapping(value="/module/codelist",method= RequestMethod.POST)
    @ResponseBody
    public  List<Map<Object,Object>> codelist(@RequestBody Map<String,Object> reqMap) throws  Exception{
        Map<Object,Object>  map = new HashMap<Object,Object>();
        String sql = "select * from sys_module";
        List<Sys_Module> list = commonDao.findList(sql,Sys_Module.class);
        //选出四位的一级菜单
        List<Sys_Module> fistmenu = new ArrayList<Sys_Module>();
        fistmenu = list.stream()
                .filter((Sys_Module b) ->b.getModulecode().length()==4)
                .collect(Collectors.toList());
        List<Map<Object,Object>> nodemap = new ArrayList<Map<Object,Object>>();
        for(Sys_Module module:fistmenu){
            nodemap.add(getTree(list,module));
        }
        return nodemap;
    }

    public Map<Object,Object> getTree(List<Sys_Module> nodeList,Sys_Module parentModule){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        List<Map<Object,Object>> nodemap = new ArrayList<Map<Object,Object>>();
        List<Sys_Module> childmenu = new ArrayList<Sys_Module>();
        //获取模块下属模块
        childmenu = nodeList.stream()
                .filter((Sys_Module b) -> b.getModulecode().length()==parentModule.getModulecode().length()+4
                        && b.getModulecode().substring(0,parentModule.getModulecode().length()).equals(parentModule.getModulecode()))
                .collect(Collectors.toList());
        if (childmenu.size()>0){
            //下属有子模块，进行获取
            for(Sys_Module module:childmenu){
                Map<Object,Object> nodeinfo = getTree(nodeList,module);
                nodemap.add(nodeinfo);
            }
        }
        map.put("text",parentModule.getModulename());
        map.put("value",parentModule.getModulecode());
        if (nodemap.size()>0){
            map.put("nodes",nodemap);
        }
        return map;
    }

    @RequestMapping(value="/module/add",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> add(@RequestBody Map<String,Object> reqMap,HttpServletRequest request){
            Map<Object,Object>  map = new HashMap<Object,Object>();
        String parentcode = "";
        try {
            parentcode = request.getParameter("parentcode");
        }catch (Exception e){

        }
        try {
            String modulename = reqMap.get("modulename").toString();
            String moduleurl = reqMap.get("moduleurl").toString();
            int ordernum = Integer.parseInt(reqMap.get("ordernum").toString());
            Sys_Module sys_module = new Sys_Module();
            String modulecode = GetModuleCode(parentcode);
            sys_module.setModulename(modulename);
            sys_module.setModulecode(modulecode);
            sys_module.setModuleurl(moduleurl);
            sys_module.setOrdernum(ordernum);
            commonDao.insert(sys_module);
            map.put("executestatus","1");
        }catch (Exception e){
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/module/delete",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> delete(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            String sql = MessageFormat.format("delete from {0} where rowguid={1}","sys_module","'"+rowguid+"'");
            icommonMapper.executeSql(sql);
            map.put("executestatus","1");
        }catch (Exception e){
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/module/findone",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> findone(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            Sys_Module sys_module = (Sys_Module)commonDao.FindEntityWithRowGuid("sys_module",rowguid,Sys_Module.class);
            map.put("data",sys_module);
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/module/save",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> configsave(@RequestBody Sys_Module sys_module){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            commonDao.update(sys_module,"rowguid");
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    public String GetModuleCode(String parentCode){
        String sql = "select IFNULL(MAX(modulecode),'') from sys_module where 1=1 ";
        if (StringUtil.isEmpty(parentCode)){
            sql += MessageFormat.format(" and LENGTH(modulecode)={0}",4);
        }else{
            sql += MessageFormat.format(" and modulecode like {0} and LENGTH(modulecode)={1}","'"+parentCode+"%'",parentCode.length()+4);
        }
        String newcode = "";
        try {
            String maxnewcode = icommonMapper.queryString(sql);
            int newfootcode = 0;
            if (StringUtil.isEmpty(maxnewcode)){
                newfootcode = 1;
                newcode = parentCode + String.format("%04d", newfootcode);
            }else{
                int maxcodefoot = Integer.parseInt(maxnewcode.substring(maxnewcode.length()-4,maxnewcode.length()));
                newfootcode = maxcodefoot+1;
                newcode = maxnewcode.substring(0,maxnewcode.length()-4)+String.format("%04d", newfootcode);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return newcode;
    }
}

package com.blog.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by paul on 2018/6/4.
 */
public class Sys_Module implements Serializable {
    public String rowguid;
    public String modulename;
    public String modulecode;
    public String moduleurl;
    public int ordernum;

    public Sys_Module(){
        rowguid = UUID.randomUUID().toString();
    }
    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

    public void setModulename(String modulename) {
        this.modulename = modulename;
    }

    public void setModulecode(String modulecode) {
        this.modulecode = modulecode;
    }

    public void setModuleurl(String moduleurl) {
        this.moduleurl = moduleurl;
    }

    public void setOrdernum(int ordernum) {
        this.ordernum = ordernum;
    }

    public String getRowguid() {
        return rowguid;
    }

    public String getModulename() {
        return modulename;
    }

    public String getModulecode() {
        return modulecode;
    }

    public String getModuleurl() {
        return moduleurl;
    }

    public int getOrdernum() {
        return ordernum;
    }
}

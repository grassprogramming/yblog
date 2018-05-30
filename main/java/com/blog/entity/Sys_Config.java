package com.blog.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by paul on 2018/5/30.
 */
public class Sys_Config implements Serializable {
    public String rowguid;
    public String configtext;
    public String configvalue;
    public String conifgintroduction;
    public int ordernum;

    public Sys_Config(){
        rowguid = UUID.randomUUID().toString();
    }

    public String getRowguid() {
        return rowguid;
    }

    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

    public String getConfigtext() {
        return configtext;
    }

    public void setConfigtext(String configtext) {
        this.configtext = configtext;
    }

    public String getConfigvalue() {
        return configvalue;
    }

    public void setConfigvalue(String configvalue) {
        this.configvalue = configvalue;
    }

    public String getConifgintroduction() {
        return conifgintroduction;
    }

    public void setConifgintroduction(String conifgintroduction) {
        this.conifgintroduction = conifgintroduction;
    }

    public int getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(int ordernum) {
        this.ordernum = ordernum;
    }
}

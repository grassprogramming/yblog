package com.blog.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by paul on 2018/6/5.
 */
public class Sys_Table implements Serializable {
    public String rowguid;
    public String tablename;
    public String sqltablename;
    public String tabletype;
    public String tablecode;
    public int ordernum;

    public Sys_Table(){
        rowguid = UUID.randomUUID().toString();
    }

    public String getRowguid() {
        return rowguid;
    }

    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getSqltablename() {
        return sqltablename;
    }

    public void setSqltablename(String sqltablename) {
        this.sqltablename = sqltablename;
    }

    public String getTabletype() {
        return tabletype;
    }

    public void setTabletype(String tabletype) {
        this.tabletype = tabletype;
    }

    public String getTablecode() {
        return tablecode;
    }

    public void setTablecode(String tablecode) {
        this.tablecode = tablecode;
    }

    public int getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(int ordernum) {
        this.ordernum = ordernum;
    }
}

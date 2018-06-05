package com.blog.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by paul on 2018/6/5.
 */
public class Sys_TableStruct implements Serializable {
    public String rowguid;
    public String tablecode;
    public String fieldname;
    public String fieldsqlname;
    public String fieldtype;
    public int fieldlength;
    public int ordernum;

    public Sys_TableStruct(){
        rowguid = UUID.randomUUID().toString();
    }

    public String getRowguid() {
        return rowguid;
    }

    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

    public String getTablecode() {
        return tablecode;
    }

    public void setTablecode(String tablecode) {
        this.tablecode = tablecode;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getFieldsqlname() {
        return fieldsqlname;
    }

    public void setFieldsqlname(String fieldsqlname) {
        this.fieldsqlname = fieldsqlname;
    }

    public String getFieldtype() {
        return fieldtype;
    }

    public void setFieldtype(String fieldtype) {
        this.fieldtype = fieldtype;
    }

    public int getFieldlength() {
        return fieldlength;
    }

    public void setFieldlength(int fieldlength) {
        this.fieldlength = fieldlength;
    }

    public int getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(int ordernum) {
        this.ordernum = ordernum;
    }
}

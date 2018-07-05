package com.blog.gxh.entity;

import java.io.Serializable;
import java.util.UUID;import java.math.BigDecimal;
import java.util.Date;
public class MusicSong implements Serializable {
    public String rowguid;
    public String SongName;
    public String SongAddress;

    public MusicSong(){  rowguid = UUID.randomUUID().toString();}    public String getRowguid(){ return rowguid;}

    public void setRowguid(String rowguid){  this.rowguid = rowguid;}

    public String getSongName(){ return SongName;}

    public void setSongName(String SongName){  this.SongName = SongName;}

    public String getSongAddress(){ return SongAddress;}

    public void setSongAddress(String SongAddress){  this.SongAddress = SongAddress;}

}

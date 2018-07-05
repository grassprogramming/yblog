package com.blog.gxh.entity;

import java.io.Serializable;
import java.util.UUID;import java.math.BigDecimal;
import java.util.Date;
public class MusicComment implements Serializable {
    public String rowguid;
    public String SongGuid;
    public String NickName;
    public String CommentContent;
    public String CommentDate;

    public MusicComment(){  rowguid = UUID.randomUUID().toString();}    public String getRowguid(){ return rowguid;}

    public void setRowguid(String rowguid){  this.rowguid = rowguid;}

    public String getSongGuid(){ return SongGuid;}

    public void setSongGuid(String SongGuid){  this.SongGuid = SongGuid;}

    public String getNickName(){ return NickName;}

    public void setNickName(String NickName){  this.NickName = NickName;}

    public String getCommentContent(){ return CommentContent;}

    public void setCommentContent(String CommentContent){  this.CommentContent = CommentContent;}

    public String getCommentDate(){ return CommentDate;}

    public void setCommentDate(String CommentDate){  this.CommentDate = CommentDate;}

}

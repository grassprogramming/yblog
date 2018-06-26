package com.blog.spider.entity;

/**
 * Created by paul on 2018/6/26.
 */
public class SongList {
    public String imageurl;
    public String songlistname;
    public String songlistcount;
    public String createauthorname;
    public String collectcount;
    public String listencount;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getSonglistname() {
        return songlistname;
    }

    public void setSonglistname(String songlistname) {
        this.songlistname = songlistname;
    }

    public String getSonglistcount() {
        return songlistcount;
    }

    public void setSonglistcount(String songlistcount) {
        this.songlistcount = songlistcount;
    }

    public String getCreateauthorname() {
        return createauthorname;
    }

    public void setCreateauthorname(String createauthorname) {
        this.createauthorname = createauthorname;
    }

    public String getCollectcount() {
        return collectcount;
    }

    public void setCollectcount(String collectcount) {
        this.collectcount = collectcount;
    }

    public String getListencount() {
        return listencount;
    }

    public void setListencount(String listencount) {
        this.listencount = listencount;
    }
}

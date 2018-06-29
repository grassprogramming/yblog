package com.blog.spider.entity;

/**
 * Created by paul on 2018/6/25.
 */
public class WYCloudMusic {

    public String songid;
    public String musicname;
    public String author;
    public String album;
    public String time;
    public String Url;

    public String getMusicname() {
        return musicname;
    }

    public void setMusicname(String musicname) {
        this.musicname = musicname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }
}

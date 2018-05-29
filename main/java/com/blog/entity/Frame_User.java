package com.blog.entity;

import java.io.Serializable;

/**
 * Created by paul on 2018/5/10.
 */
public class Frame_User implements Serializable {
    public String loginid;
    public String displayname;
    public String userguid;
    public String password;

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getUserguid() {
        return userguid;
    }

    public void setUserguid(String userguid) {
        this.userguid = userguid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.bokecc.ccsskt.example.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/23.
 */

public class TimingEntity implements Serializable {
    public String userid;
    public String roomid;

    public long classlength;
    public long time;

    public TimingEntity(String userid, String roomid, long classlength, long time) {
        this.userid = userid;
        this.roomid=roomid;
        this.classlength = classlength;
        this.time = time;
    }

    public TimingEntity() {
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getClasslength() {
        return classlength;
    }

    public void setClasslength(long classlength) {
        this.classlength = classlength;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}

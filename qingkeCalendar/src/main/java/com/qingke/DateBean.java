package com.qingke;

/**
 * Created by lvqiu on 2018/10/28.
 */

/**
 *  {'uuid':'2342342342342423424342','title':'客户访谈计划制定及梳理','status':'日常计划','endtime':'16:00','color':'B2B9C8'}
 */
public class DateBean {
    public String uuid;
    public String title;
    public String status;
    public String endtime;
    public String color;

    public DateBean() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

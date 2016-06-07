package com.sharukhhasan.teammanagement.model;

/**
 * Created by sharukhhasan on 6/7/16.
 */
public class Report {
    private String name;
    private String uid;
    private String hours;
    private long time;
    private String date;

    public Report() {}

    public Report(String name, String uid, String hours, long time, String date)
    {
        this.name = name;
        this.uid = uid;
        this.hours = hours;
        this.time = time;
        this.date = date;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getHours()
    {
        return hours;
    }

    public void setHours(String hours)
    {
        this.hours = hours;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}

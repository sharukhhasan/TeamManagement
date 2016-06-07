package com.sharukhhasan.teammanagement.model;

/**
 * Created by sharukhhasan on 5/4/16.
 */
public class User {
    private String email;
    private String name;
    private String prefHours;
    private int level;
    private String manager;

    public User() {}

    public User(String prefHours)
    {
        this.prefHours = prefHours;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPrefHours()
    {
        return prefHours;
    }

    public void setPrefHours(String prefHours)
    {
        this.prefHours = prefHours;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getManager()
    {
        return manager;
    }

    public void setManager(String manager)
    {
        this.manager = manager;
    }
}

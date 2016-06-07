package com.sharukhhasan.teammanagement.model;

/**
 * Created by sharukhhasan on 6/7/16.
 */
public class Manager implements ModelKeyInterface{
    private String key;
    private String name;
    private String email;
    private String prefHours;
    private int level;
    private String manager;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Manager() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPrefHours() {
        return prefHours;
    }

    public void setPrefHours(String prefHours) {
        this.prefHours = prefHours;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}

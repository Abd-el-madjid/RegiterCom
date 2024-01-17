package com.example.registrecom.Models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Canaux {
    private String name;
    private String lastDateMessage;
    private String canauxId;



    private long userId;

    // No-argument constructor required by Firebase
    public Canaux() {
        this.lastDateMessage = null; // Set default value to current time
        this.canauxId = null; // Set default value to null or generate a unique ID
    }

    // Constructor with default values
    public Canaux(String name, String lastDateMessage,long userId) {
        this.name = name;
        this.lastDateMessage = lastDateMessage; // Set default value to current time
        this.canauxId = null; // Set default value to null or generate a unique ID
        this.userId = userId ;
    }

    // Getter and setter methods for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter methods for lastDateMessage
    public String getLastDateMessage() {
        return lastDateMessage;
    }

    public void setLastDateMessage(String lastDateMessage) {
        this.lastDateMessage = lastDateMessage;
    }

    // Getter and setter methods for canauxId
    public String getCanauxId() {
        return canauxId;
    }

    public void setCanauxId(String canauxId) {
        this.canauxId = canauxId;
    }

    // Helper method to get current time as a string
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}

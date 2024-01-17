package com.example.registrecom.classes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class notif {

    private String id; // New field for the ID
    private String title, contenu;
    private Date formattedDate; // Change the type to Date
    private boolean expendable;

    public notif(String id, String title, String contenu, Date date) {
        this.id = id;
        this.title = title;
        this.contenu = contenu;
        this.formattedDate = date;
        this.expendable = false;
    }

    @Override
    public String toString() {
        return "notif{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contenu='" + contenu + '\'' +
                ", formattedDate='" + getFormattedDateString() + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getFormattedDate() {
        return formattedDate;
    }

    public String getFormattedDateString() {
        // Convert Date to the needed format
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd HH:mm");
        return formattedDate != null ? outputFormat.format(formattedDate) : "";
    }

    public boolean isExpendable() {
        return expendable;
    }

    public void setExpendable(boolean expendable) {
        this.expendable = expendable;
    }
}

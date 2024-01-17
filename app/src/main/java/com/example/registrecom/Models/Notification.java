package com.example.registrecom.Models;

import java.util.Date;

public class Notification {
    private String id;
    private  String title ;



    private String contenu;



    private boolean status;
    private long idPersonne;
    private Date dateCreation;

    // Constructors

    public Notification(String title ,String contenu, long idPersonne, Date dateCreation) {
        this.title=title;
        this.contenu = contenu;
        this.status = false;
        this.idPersonne = idPersonne;
        this.dateCreation = dateCreation;
    }

    public Notification(String id ){
        this.id = id;
    }

    // Getters and Setters

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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(long idPersonne) {
        this.idPersonne = idPersonne;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}

package com.example.registrecom.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Messages {
    private String idMessage; // Primary Key
    private String contenu;
    private String idRecepteur;
    private String idEmeteur; // Foreign Key referencing personne.id
    private String timestamp;
    private String idCanaux; // Foreign Key referencing id canaux

    // Constructors

    public Messages() {
        // Default constructor
        this.idRecepteur = null; // Set idRecepteur to null
    }

    public Messages(String idEmeteur, String idCanaux, String contenu) {
        this.idMessage = null;
        this.idRecepteur = null;
        this.contenu = contenu;
        this.idEmeteur = idEmeteur;
        this.timestamp = formatTimestamp(new Date());
        this.idCanaux = idCanaux;
    }

    public Messages(String idEmeteur, String idCanaux, String contenu,String idRecepteur) {
        this.idMessage = null;
        this.idRecepteur = idRecepteur;
        this.contenu = contenu;
        this.idEmeteur = idEmeteur;
        this.timestamp = formatTimestamp(new Date());
        this.idCanaux = idCanaux;
    }
    // Getters and Setters

    public String getIdRecepteur() {
        return idRecepteur;
    }

    public void setIdRecepteur(String idRecepteur) {
        this.idRecepteur = idRecepteur;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getIdEmeteur() {
        return idEmeteur;
    }

    public void setIdEmeteur(String idEmeteur) {
        this.idEmeteur = idEmeteur;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdCanaux() {
        return idCanaux;
    }

    public void setIdCanaux(String idCanaux) {
        this.idCanaux = idCanaux;
    }

    private String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(" - dd MMM yyyy - HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}

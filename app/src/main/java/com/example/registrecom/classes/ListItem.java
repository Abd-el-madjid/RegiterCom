package com.example.registrecom.classes;
// ListItem.java

public class ListItem {
    private String idDemande;  // Add this line
    private String name;
    private String time;
    private int etatImageResource;

    // Modify the constructor to include idDemande
    public ListItem(String idDemande, String name, String time, int etatImageResource) {
        this.idDemande = idDemande;
        this.name = name;
        this.time = time;
        this.etatImageResource = etatImageResource;
    }

    // Add the getter method for idDemande
    public String getIdDemande() {
        return idDemande;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getEtatImageResource() {
        return etatImageResource;
    }
}

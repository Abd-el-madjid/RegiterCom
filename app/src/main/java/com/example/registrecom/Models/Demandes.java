package com.example.registrecom.Models;

import com.example.registrecom.bdmanupulation.Etat;

import java.util.Date;

// Demande class
public class Demandes {
    private String ID_demande;
    private int ID_demandeur;
    private Etat etat;
    private Date dateSoumission;
    private String nomEnterprise;
    private String adresseEnterprise;
    private String numeroTelephoneEnterprise;
    private String emailEnterprise;
    private String typeActivite;

    // Default constructor with "En attente" as the default état
    public Demandes() {
        this.etat = Etat.EN_ATTENTE;
    }

    // Constructor with parameters
    public Demandes(String ID_demande, int ID_demandeur, Date dateSoumission,
                    String nomEnterprise, String adresseEnterprise, String numeroTelephoneEnterprise,
                    String emailEnterprise, String typeActivite) {
        this.ID_demande = ID_demande;
        this.ID_demandeur = ID_demandeur;
        this.etat = Etat.EN_ATTENTE;
        this.dateSoumission = dateSoumission;
        this.nomEnterprise = nomEnterprise;
        this.adresseEnterprise = adresseEnterprise;
        this.numeroTelephoneEnterprise = numeroTelephoneEnterprise;
        this.emailEnterprise = emailEnterprise;
        this.typeActivite = typeActivite;
    }
    // Getters and Setters

    public String getID_demande() {
        return ID_demande;
    }

    public void setID_demande(String ID_demande) {
        this.ID_demande = ID_demande;
    }

    public int getID_demandeur() {
        return ID_demandeur;
    }

    public void setID_demandeur(int ID_demandeur) {
        this.ID_demandeur = ID_demandeur;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public Date getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(Date dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public String getNomEnterprise() {
        return nomEnterprise;
    }

    public void setNomEnterprise(String nomEnterprise) {
        this.nomEnterprise = nomEnterprise;
    }

    public String getAdresseEnterprise() {
        return adresseEnterprise;
    }

    public void setAdresseEnterprise(String adresseEnterprise) {
        this.adresseEnterprise = adresseEnterprise;
    }

    public String getNumeroTelephoneEnterprise() {
        return numeroTelephoneEnterprise;
    }

    public void setNumeroTelephoneEnterprise(String numeroTelephoneEnterprise) {
        this.numeroTelephoneEnterprise = numeroTelephoneEnterprise;
    }

    public String getEmailEnterprise() {
        return emailEnterprise;
    }

    public void setEmailEnterprise(String emailEnterprise) {
        this.emailEnterprise = emailEnterprise;
    }

    public String getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }

    // Additional methods or behaviors can be added as needed
}


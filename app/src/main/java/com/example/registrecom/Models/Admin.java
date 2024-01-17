package com.example.registrecom.Models;

import java.util.Date;
public class Admin {
    private String id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String email;
    private String nomUtilisateur;
    private String motPasse;

    private Date lastLogin;
    private Date dateCreation;


    public Admin() {
        // Set default values
        this.lastLogin = null; // or you can set a specific default value if needed
        this.dateCreation = null;
        this.id = null;


    }

    public Admin( String nom, String prenom, Date dateNaissance,
                     String email, String nomUtilisateur, String motPasse,
                    Date lastLogin, Date dateCreation) {

        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;


        this.email = email;
        this.nomUtilisateur = nomUtilisateur;
        this.motPasse = motPasse;


        this.lastLogin = lastLogin;
        this.dateCreation = dateCreation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMotPasse() {
        return motPasse;
    }

    public void setMotPasse(String motPasse) {
        this.motPasse = motPasse;
    }


    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}

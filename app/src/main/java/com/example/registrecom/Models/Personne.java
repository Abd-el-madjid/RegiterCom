package com.example.registrecom.Models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Personne  implements Serializable {
    private long id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String lieuNaissance;
    private boolean sex;
    private String email;
    private String nationalite;
    private long numTelephone;
    private String nomUtilisateur;
    private String motPasse;

    private Date lastLogin;
    private Date dateCreation;



    private String token ;
    private boolean isactive ;


    public Personne() {
        // Set default values
        this.lastLogin = null; // or you can set a specific default value if needed
        this.dateCreation = null;
        this.token = null;
        this.isactive=false;

    }



    public Personne(long id, String nom, String prenom, Date dateNaissance,
                    String lieuNaissance, boolean sex, String email, String nationalite,
                    long numTelephone, String nomUtilisateur, String motPasse,
                    Date lastLogin, Date dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;
        this.sex = sex;
        this.isactive=false;
        this.email = email;
        this.nationalite = nationalite;
        this.numTelephone = numTelephone;
        this.nomUtilisateur = nomUtilisateur;
        this.motPasse = motPasse;


        this.lastLogin = lastLogin;
        this.dateCreation = dateCreation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public long getNumTelephone() {
        return numTelephone;
    }

    public void setNumTelephone(long numTelephone) {
        this.numTelephone = numTelephone;
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
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }
}

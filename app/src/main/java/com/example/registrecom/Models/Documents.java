package com.example.registrecom.Models;

import java.util.Date;

public class Documents {
    private String idDocument;
    private String idDemande;
    private String documentName;  // Added semicolon
    private String cheminFichier;
    private String typeDocument;
    private Date dateTelechargement;

    public Documents() {
        // Default constructor required for calls to DataSnapshot.getValue(Documents.class)
    }
    // Constructor
    public Documents(String idDocument, String idDemande, String documentName, String cheminFichier, String typeDocument, Date dateTelechargement) {
        this.idDocument = idDocument;
        this.idDemande = idDemande;
        this.documentName = documentName;
        this.cheminFichier = cheminFichier;
        this.typeDocument = typeDocument;
        this.dateTelechargement = dateTelechargement;
    }

    // Getters and Setters
    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    public String getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(String idDemande) {
        this.idDemande = idDemande;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public Date getDateTelechargement() {
        return dateTelechargement;
    }

    public void setDateTelechargement(Date dateTelechargement) {
        this.dateTelechargement = dateTelechargement;
    }
}

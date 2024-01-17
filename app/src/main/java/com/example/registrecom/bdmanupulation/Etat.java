package com.example.registrecom.bdmanupulation;

// Enumeration for état (state)
public enum Etat {
    EN_ATTENTE("En attente"),
    Accepte("accepte"),
    Refuse("refuse"),
    APPROBATION_FINALE("Approbation finale");

    private final String libelle;

    Etat(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

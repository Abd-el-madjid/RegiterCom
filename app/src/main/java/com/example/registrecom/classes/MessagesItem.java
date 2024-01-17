package com.example.registrecom.classes;

public class MessagesItem {
    private String Idsendeur;



    private String sendeur;
    private String time;
    private String contenu; // You can change this to the type you're using for your image

    public MessagesItem(String sendeur, String time, String contenu) {

        this.sendeur = sendeur;
        this.time = time;
        this.contenu = contenu;
    }

    public String getsendeur() {
        return sendeur;
    }

    public String getTime() {
        return time;
    }

    public String getcontenu() {
        return contenu;
    }
    public String getIdsendeur() {
        return Idsendeur;
    }

    public void setIdsendeur(String idsendeur) {
        Idsendeur = idsendeur;
    }

}

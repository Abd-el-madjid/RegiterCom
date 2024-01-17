package com.example.registrecom.classes;



public class Profile{
    private String id;
    private String username;


    private String lettername;
    private String email;
// String to store the image URL or any identifier
    private boolean state;


    public Profile(String id ,String lettername, String username, String email,  boolean state) {
        this.id=id;
        this.lettername =lettername;
        this.username = username;
        this.email = email;
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }


    public boolean getState() {
        return state;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLettername() {
        return lettername;
    }

    public void setLettername(String lettername) {
        this.lettername = lettername;
    }
}

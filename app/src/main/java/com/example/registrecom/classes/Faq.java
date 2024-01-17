package com.example.registrecom.classes;

public class Faq {

    private  String  question , reponse ;
    private boolean expendable ;



    public Faq(String question, String reponse) {
        this.question = question;
        this.reponse = reponse;
        this.expendable=false;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public boolean isExpendable() {
        return expendable;
    }

    public void setExpendable(boolean expendable) {
        this.expendable = expendable;
    }

    @Override
    public String toString() {
        return "Faq{" +
                "question='" + question + '\'' +
                ", reponse='" + reponse + '\'' +
                '}';
    }

}

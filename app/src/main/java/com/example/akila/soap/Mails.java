package com.example.akila.soap;

public class Mails {
    private String title, genre, year, uid, body;

    public Mails() {
    }

    public Mails(String title, String genre, String year, String uid, String body) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.uid = uid;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getUID() {
        return uid;
    }

    public String getBody() {
        return body;
    }
}

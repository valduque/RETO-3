package org.example.model;

public class Article {
    private String title;
    private String authors;
    private String publication;
    private String year;
    private int citedBy;
    private String link;

    // Getters y setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getPublication() { return publication; }
    public void setPublication(String publication) { this.publication = publication; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public int getCitedBy() { return citedBy; }
    public void setCitedBy(int citedBy) { this.citedBy = citedBy; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}

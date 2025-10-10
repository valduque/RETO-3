package org.example.model;

import java.util.List;

public class Author {
    private String name;
    private String affiliation;
    private String email;
    private List<Article> articles;

    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
}

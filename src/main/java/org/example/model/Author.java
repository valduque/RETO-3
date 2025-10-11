package org.example.model;

import java.util.List;

public class Author {
    private String id;
    private List<Article> articles;

    public Author() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
}





package org.example.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.example.model.Article;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ScholarController {

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String serpApiBase = "https://serpapi.com/search";

    public ScholarController(String apiKey) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("apiKey required");
        this.apiKey = apiKey;
    }

    private String httpGet(String url) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.addHeader("Accept", "application/json");
            try (CloseableHttpResponse response = client.execute(get)) {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    if (response.getEntity() == null) return "";
                    return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                } else {
                    throw new IOException("HTTP " + status + " from " + url);
                }
            }
        }
    }

    // --- Buscar por author_id (exacto) ---
    public List<Article> searchByAuthorId(String authorId) throws IOException {
        if (authorId == null || authorId.isBlank())
            throw new IllegalArgumentException("author_id required");

        String url = String.format("%s?engine=google_scholar_author&author_id=%s&api_key=%s",
                serpApiBase,
                URLEncoder.encode(authorId, StandardCharsets.UTF_8),
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
        );

        String body = httpGet(url);
        JsonNode root = mapper.readTree(body);
        JsonNode articlesNode = root.path("articles");

        List<Article> articles = new ArrayList<>();
        if (articlesNode.isArray()) {
            for (JsonNode art : articlesNode) {
                Article a = new Article();
                a.setTitle(art.path("title").asText(null));
                a.setAuthors(art.path("authors").asText(null));
                a.setPublication(art.path("publication").asText(null));
                a.setYear(art.path("year").asText(null));
                JsonNode cited = art.path("cited_by").path("value");
                a.setCitedBy(cited.isMissingNode() ? -1 : cited.asInt(-1));
                a.setLink(art.path("link").asText(null));
                articles.add(a);
            }
        }
        return articles;
    }

    // --- Buscar por nombre (aproximado) ---
    public List<Article> searchByAuthorName(String name) throws IOException {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Author name required");

        String url = String.format("%s?engine=google_scholar&q=%s&api_key=%s",
                serpApiBase,
                URLEncoder.encode(name, StandardCharsets.UTF_8),
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
        );

        String body = httpGet(url);
        JsonNode root = mapper.readTree(body);
        JsonNode articlesNode = root.path("articles");

        List<Article> articles = new ArrayList<>();
        if (articlesNode.isArray()) {
            for (JsonNode art : articlesNode) {
                String authors = art.path("authors").asText("").toLowerCase();
                if (authors.contains(name.toLowerCase())) { // filtrar por coincidencia exacta en texto
                    Article a = new Article();
                    a.setTitle(art.path("title").asText(null));
                    a.setAuthors(authors);
                    a.setPublication(art.path("publication").asText(null));
                    a.setYear(art.path("year").asText(null));
                    JsonNode cited = art.path("cited_by").path("value");
                    a.setCitedBy(cited.isMissingNode() ? -1 : cited.asInt(-1));
                    a.setLink(art.path("link").asText(null));
                    articles.add(a);
                }
            }
        }
        return articles;
    }

    // --- Híbrido: si hay author_id usa exacto, sino nombre ---
    public List<Article> searchHybrid(String authorId, String name) throws IOException {
        if (authorId != null && !authorId.isBlank()) {
            return searchByAuthorId(authorId);
        } else if (name != null && !name.isBlank()) {
            return searchByAuthorName(name);
        } else {
            throw new IllegalArgumentException("Either authorId or name must be provided");
        }
    }
}



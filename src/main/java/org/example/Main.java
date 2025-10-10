package org.example;

import org.example.control.ScholarController;
import org.example.view.ScholarView;
import org.example.model.Article;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("SERPAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Set SERPAPI_KEY environment variable with your SerpApi api_key.");
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            ScholarView view = new ScholarView();
            ScholarController controller = new ScholarController(apiKey);

            view.setSearchAction(() -> {
                String authorId = view.getAuthorId(); // nuevo campo opcional
                String authorName = view.getQuery();
                view.showStatus("Searching...");

                new Thread(() -> {
                    try {
                        List<Article> articles = controller.searchHybrid(authorId, authorName);
                        view.displayArticles(articles);
                        view.showStatus("Search complete");
                    } catch (Exception e) {
                        view.showError("Search failed: " + e.getMessage());
                        view.showStatus("Error");
                    }
                }).start();
            });

            view.setVisible(true);
        });
    }
}


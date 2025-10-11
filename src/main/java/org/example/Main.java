package org.example;

import org.example.control.ScholarController;
import org.example.database.DatabaseManager;
import org.example.model.Article;
import org.example.view.DatabaseViewerDialog;
import org.example.view.ScholarView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Get API key from environment variable
        String apiKey = System.getenv("SERPAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("ERROR: Set SERPAPI_KEY environment variable with your SerpApi api_key.");
            System.err.println("Example: export SERPAPI_KEY=your_api_key_here");
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            ScholarView view = new ScholarView();
            ScholarController controller = new ScholarController(apiKey);
            DatabaseManager dbManager;

            // Initialize database connection
            try {
                dbManager = new DatabaseManager();
                System.out.println("✓ Database connection established successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to database.\n" +
                                "Error: " + e.getMessage() + "\n\n" +
                                "Please ensure:\n" +
                                "1. PostgreSQL is running\n" +
                                "2. Database 'scholar_db' exists\n" +
                                "3. Credentials in DatabaseManager are correct",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("Database connection failed: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
                return;
            }

            // Search action
            view.setSearchAction(() -> {
                String authorId = view.getAuthorId();
                String authorName = view.getQuery();

                if (authorId.isBlank() && authorName.isBlank()) {
                    view.showError("Please enter either Author Name or Author ID");
                    return;
                }

                view.showStatus("Searching...");

                new Thread(() -> {
                    try {
                        List<Article> articles = controller.searchHybrid(authorId, authorName);
                        view.displayArticles(articles);
                        view.showStatus("Search complete - " + articles.size() + " articles found");
                    } catch (Exception e) {
                        view.showError("Search failed: " + e.getMessage());
                        view.showStatus("Error");
                        e.printStackTrace();
                    }
                }).start();
            });

            // Save to database action
            view.setSaveToDbAction(() -> {
                List<Article> articles = view.getCurrentArticles();
                if (articles == null || articles.isEmpty()) {
                    view.showError("No articles to save. Please search first.");
                    return;
                }

                // Limit to 3 articles as per requirements
                List<Article> articlesToSave = articles.size() > 3
                        ? articles.subList(0, 3)
                        : articles;

                // Prompt for researcher information
                String researcherName = view.promptForResearcherName();
                if (researcherName == null || researcherName.isBlank()) {
                    view.showError("Researcher name is required");
                    return;
                }

                String affiliation = view.promptForAffiliation();
                String email = view.promptForEmail();
                String authorId = view.getAuthorId();

                view.showStatus("Saving to database...");

                new Thread(() -> {
                    try {
                        // Check if researcher already exists
                        if (authorId != null && !authorId.isBlank() && dbManager.researcherExists(authorId)) {
                            view.showError("Researcher with this Author ID already exists in database");
                            view.showStatus("Ready");
                            return;
                        }

                        // Insert researcher
                        int researcherId = dbManager.insertResearcher(
                                researcherName,
                                authorId.isBlank() ? null : authorId,
                                affiliation,
                                email
                        );

                        // Insert articles (max 3)
                        for (Article article : articlesToSave) {
                            dbManager.insertArticle(researcherId, article);
                        }

                        view.showInfo(String.format(
                                "Successfully saved:\n" +
                                        "Researcher: %s\n" +
                                        "Articles: %d",
                                researcherName, articlesToSave.size()
                        ));
                        view.showStatus("Saved to database successfully");

                    } catch (SQLException e) {
                        view.showError("Failed to save to database: " + e.getMessage());
                        view.showStatus("Error saving");
                        e.printStackTrace();
                    }
                }).start();
            });

            // View database action
            view.setViewDbAction(() -> {
                try {
                    DatabaseViewerDialog dialog = new DatabaseViewerDialog(view, dbManager);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    view.showError("Failed to open database viewer: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // Add window closing listener to close database connection
            view.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    dbManager.close();
                    System.out.println("Database connection closed");
                }
            });

            view.setVisible(true);
        });
    }
}




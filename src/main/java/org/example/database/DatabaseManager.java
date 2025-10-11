package org.example.database;

import org.example.model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/scholar_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Duquechita26!";

    private Connection connection;

    public DatabaseManager() throws SQLException {
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        createTables();
    }

    private void createTables() throws SQLException {
        String createResearchersTable = """
        CREATE TABLE IF NOT EXISTS researchers (
            id SERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            author_id VARCHAR(100),
            affiliation VARCHAR(500),
            email VARCHAR(255),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """;

        String createArticlesTable = """
        CREATE TABLE IF NOT EXISTS articles (
            id SERIAL PRIMARY KEY,
            researcher_id INTEGER NOT NULL,
            title TEXT NOT NULL,
            authors TEXT,
            publication VARCHAR(500),
            publication_date VARCHAR(50),
            abstract TEXT,
            link TEXT,
            keywords TEXT,
            cited_by INTEGER DEFAULT 0,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (researcher_id) REFERENCES researchers(id) ON DELETE CASCADE
        )
    """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createResearchersTable);
            stmt.execute(createArticlesTable);
        }
    }

    // Insert a researcher and return the generated ID
    public int insertResearcher(String name, String authorId, String affiliation, String email) throws SQLException {
        String sql = "INSERT INTO researchers (name, author_id, affiliation, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, authorId);
            pstmt.setString(3, affiliation);
            pstmt.setString(4, email);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert researcher");
    }

    // Insert an article linked to a researcher
    public void insertArticle(int researcherId, Article article) throws SQLException {
        String sql = """
            INSERT INTO articles (researcher_id, title, authors, publication, publication_date, 
                                 abstract, link, keywords, cited_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, researcherId);
            pstmt.setString(2, article.getTitle());
            pstmt.setString(3, article.getAuthors());
            pstmt.setString(4, article.getPublication());
            pstmt.setString(5, article.getYear());
            pstmt.setString(6, article.getAbstract());
            pstmt.setString(7, article.getLink());
            pstmt.setString(8, article.getKeywords());
            pstmt.setInt(9, article.getCitedBy());
            pstmt.executeUpdate();
        }
    }

    // Get all researchers
    public List<ResearcherRecord> getAllResearchers() throws SQLException {
        String sql = "SELECT * FROM researchers ORDER BY name";
        List<ResearcherRecord> researchers = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ResearcherRecord r = new ResearcherRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("author_id"),
                        rs.getString("affiliation"),
                        rs.getString("email")
                );
                researchers.add(r);
            }
        }
        return researchers;
    }

    // Get articles for a specific researcher
    public List<Article> getArticlesByResearcher(int researcherId) throws SQLException {
        String sql = "SELECT * FROM articles WHERE researcher_id = ? ORDER BY cited_by DESC";
        List<Article> articles = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, researcherId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article a = new Article();
                    a.setTitle(rs.getString("title"));
                    a.setAuthors(rs.getString("authors"));
                    a.setPublication(rs.getString("publication"));
                    a.setYear(rs.getString("publication_date"));
                    a.setAbstract(rs.getString("abstract"));
                    a.setLink(rs.getString("link"));
                    a.setKeywords(rs.getString("keywords"));
                    a.setCitedBy(rs.getInt("cited_by"));
                    articles.add(a);
                }
            }
        }
        return articles;
    }

    // Get total count of articles
    public int getArticleCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    // Get total count of researchers
    public int getResearcherCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM researchers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    // Check if researcher exists by author_id
    public boolean researcherExists(String authorId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM researchers WHERE author_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inner record class for researcher data
    public record ResearcherRecord(int id, String name, String authorId,
                                   String affiliation, String email) {}
}
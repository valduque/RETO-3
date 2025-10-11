package org.example.view;

import org.example.model.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class ScholarView extends JFrame {

    private final JTextField queryField;
    private final JTextField authorIdField;
    private final JButton searchButton;
    private final JButton saveToDbButton;
    private final JButton viewDbButton;
    private final JTable articlesTable;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    private List<Article> currentArticles;

    public ScholarView() {
        super("Google Scholar Author Search with Database");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLayout(new BorderLayout());

        // Top panel with search fields
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout());

        top.add(new JLabel("Author Name:"));
        queryField = new JTextField(15);
        top.add(queryField);

        top.add(new JLabel("Author ID (optional):"));
        authorIdField = new JTextField(10);
        top.add(authorIdField);

        searchButton = new JButton("Search");
        top.add(searchButton);

        saveToDbButton = new JButton("Save to DB");
        saveToDbButton.setEnabled(false);
        top.add(saveToDbButton);

        viewDbButton = new JButton("View Database");
        top.add(viewDbButton);

        add(top, BorderLayout.NORTH);

        // Articles table
        String[] columns = {"Title", "Authors", "Publication", "Year", "Cited By", "Link"};
        tableModel = new DefaultTableModel(columns, 0);
        articlesTable = new JTable(tableModel);
        articlesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        articlesTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        articlesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        articlesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        articlesTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        articlesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        articlesTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(articlesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel("Ready");
        add(statusLabel, BorderLayout.SOUTH);
    }

    public String getQuery() {
        return queryField.getText().trim();
    }

    public String getAuthorId() {
        return authorIdField.getText().trim();
    }

    public void setSearchAction(Runnable action) {
        searchButton.addActionListener(e -> action.run());
    }

    public void setSaveToDbAction(Runnable action) {
        saveToDbButton.addActionListener(e -> action.run());
    }

    public void setViewDbAction(Runnable action) {
        viewDbButton.addActionListener(e -> action.run());
    }

    public void showStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    public void showInfo(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }

    public void displayArticles(List<Article> articles) {
        SwingUtilities.invokeLater(() -> {
            currentArticles = articles;
            tableModel.setRowCount(0);

            if (articles == null || articles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No articles found for this author.", "Info", JOptionPane.INFORMATION_MESSAGE);
                saveToDbButton.setEnabled(false);
                return;
            }

            for (Article a : articles) {
                Vector<Object> row = new Vector<>();
                row.add(a.getTitle());
                row.add(a.getAuthors());
                row.add(a.getPublication());
                row.add(a.getYear());
                row.add(a.getCitedBy());
                row.add(a.getLink());
                tableModel.addRow(row);
            }

            saveToDbButton.setEnabled(true);
        });
    }

    public List<Article> getCurrentArticles() {
        return currentArticles;
    }

    public String promptForResearcherName() {
        return JOptionPane.showInputDialog(this,
                "Enter researcher name:",
                "Save to Database",
                JOptionPane.QUESTION_MESSAGE);
    }

    public String promptForAffiliation() {
        return JOptionPane.showInputDialog(this,
                "Enter affiliation (optional):",
                "Save to Database",
                JOptionPane.QUESTION_MESSAGE);
    }

    public String promptForEmail() {
        return JOptionPane.showInputDialog(this,
                "Enter email (optional):",
                "Save to Database",
                JOptionPane.QUESTION_MESSAGE);
    }
}



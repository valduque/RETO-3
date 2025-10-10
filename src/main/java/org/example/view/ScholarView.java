package org.example.view;

import org.example.model.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class ScholarView extends JFrame {

    private final JTextField queryField;
    private final JTextField authorIdField; // campo opcional para author_id
    private final JButton searchButton;
    private final JTable articlesTable;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;

    public ScholarView() {
        super("Google Scholar Author Search");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Panel superior con campos de búsqueda
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
        add(top, BorderLayout.NORTH);

        // Tabla de artículos
        String[] columns = {"Title", "Authors", "Publication", "Year", "Cited By", "Link"};
        tableModel = new DefaultTableModel(columns, 0);
        articlesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(articlesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Estado inferior
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

    public void showStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    public void displayArticles(List<Article> articles) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);

            if (articles == null || articles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No articles found for this author.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
        });
    }
}



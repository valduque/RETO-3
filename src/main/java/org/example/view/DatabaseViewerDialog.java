package org.example.view;

import org.example.database.DatabaseManager;
import org.example.model.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DatabaseViewerDialog extends JDialog {

    private final DatabaseManager dbManager;
    private final JTable researchersTable;
    private final DefaultTableModel researchersTableModel;
    private final JTable articlesTable;
    private final DefaultTableModel articlesTableModel;
    private final JLabel statsLabel;

    public DatabaseViewerDialog(JFrame parent, DatabaseManager dbManager) {
        super(parent, "Database Viewer", true);
        this.dbManager = dbManager;

        setSize(900, 600);
        setLayout(new BorderLayout());

        // Top panel with statistics
        JPanel topPanel = new JPanel();
        statsLabel = new JLabel();
        topPanel.add(statsLabel);
        add(topPanel, BorderLayout.NORTH);

        // Split pane for researchers and articles
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Researchers panel
        JPanel researchersPanel = new JPanel(new BorderLayout());
        researchersPanel.setBorder(BorderFactory.createTitledBorder("Researchers"));

        String[] researcherColumns = {"ID", "Name", "Author ID", "Affiliation", "Email"};
        researchersTableModel = new DefaultTableModel(researcherColumns, 0);
        researchersTable = new JTable(researchersTableModel);
        researchersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        researchersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadArticlesForSelectedResearcher();
            }
        });

        JScrollPane researchersScrollPane = new JScrollPane(researchersTable);
        researchersPanel.add(researchersScrollPane, BorderLayout.CENTER);

        // Articles panel
        JPanel articlesPanel = new JPanel(new BorderLayout());
        articlesPanel.setBorder(BorderFactory.createTitledBorder("Articles"));

        String[] articleColumns = {"Title", "Authors", "Publication", "Year", "Cited By"};
        articlesTableModel = new DefaultTableModel(articleColumns, 0);
        articlesTable = new JTable(articlesTableModel);
        articlesTable.getColumnModel().getColumn(0).setPreferredWidth(300);

        JScrollPane articlesScrollPane = new JScrollPane(articlesTable);
        articlesPanel.add(articlesScrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(researchersPanel);
        splitPane.setBottomComponent(articlesPanel);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        // Bottom panel with close button
        JPanel bottomPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadData();
        setLocationRelativeTo(parent);
    }

    private void loadData() {
        try {
            // Load statistics
            int researcherCount = dbManager.getResearcherCount();
            int articleCount = dbManager.getArticleCount();
            statsLabel.setText(String.format("Database: %d researchers, %d articles",
                    researcherCount, articleCount));

            // Load researchers
            researchersTableModel.setRowCount(0);
            List<DatabaseManager.ResearcherRecord> researchers = dbManager.getAllResearchers();

            for (DatabaseManager.ResearcherRecord r : researchers) {
                Object[] row = {
                        r.id(),
                        r.name(),
                        r.authorId(),
                        r.affiliation(),
                        r.email()
                };
                researchersTableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading database: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadArticlesForSelectedResearcher() {
        int selectedRow = researchersTable.getSelectedRow();
        if (selectedRow == -1) {
            articlesTableModel.setRowCount(0);
            return;
        }

        int researcherId = (int) researchersTableModel.getValueAt(selectedRow, 0);

        try {
            articlesTableModel.setRowCount(0);
            List<Article> articles = dbManager.getArticlesByResearcher(researcherId);

            for (Article a : articles) {
                Object[] row = {
                        a.getTitle(),
                        a.getAuthors(),
                        a.getPublication(),
                        a.getYear(),
                        a.getCitedBy()
                };
                articlesTableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading articles: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
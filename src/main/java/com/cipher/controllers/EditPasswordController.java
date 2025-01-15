package com.cipher.controllers;

import java.io.IOException;

import com.cipher.Outils.Chiffrement;
import com.cipher.Outils.DatabaseManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditPasswordController {

    @FXML
    private TextField siteField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private Chiffrement chiffrement;
    private DatabaseManager databaseManager;
    private String site;

    @FXML
    private void initialize() {
        // Initialize any necessary data here
    }

    public void setChiffrement(Chiffrement chiffrement) {
        this.chiffrement = chiffrement;
        this.databaseManager = new DatabaseManager(chiffrement);
    }

    public void setSite(String site) {
        this.site = site;
        siteField.setText(site);
        loadPasswordDetails();
    }

    private void loadPasswordDetails() {
        try {
            String decryptedPassword = databaseManager.getDecryptedPasswordBySite(site);
            if (decryptedPassword != null) {
                passwordField.setText(decryptedPassword);
                // Load the username if needed
                // usernameField.setText(...);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des détails du mot de passe : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSavePassword() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (password.isEmpty()) {
            System.out.println("⚠️ Tous les champs doivent être remplis !");
            return;
        }

        if (username.isEmpty()) {
            username = "Pas de nom";
        }
        // Encrypt the password
        String encryptedPassword = chiffrement.chiffrer(password);

        // Update the password in the database
        databaseManager.updatePassword(site, username, encryptedPassword);

        System.out.println("✅ Mot de passe mis à jour dans la base de données !");

        // Return to the dashboard
        goToDashboard();
    }

    @FXML
    private void handleCancel() {
        // Return to the dashboard without saving
        goToDashboard();
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cipher/views/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Pass the Chiffrement instance to the Dashboard
            DashboardController dashboardController = loader.getController();
            dashboardController.setChiffrement(chiffrement);

            Stage stage = (Stage) siteField.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(dashboardRoot));
            stage.show();

            System.out.println("📂 Retour au Dashboard !");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour au Dashboard : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

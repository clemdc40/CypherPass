package com.cipher.controllers;

import com.cipher.Outils.Chiffrement;
import com.cipher.Outils.DatabaseManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private FlowPane passwordContainer;

    private DatabaseManager databaseManager;
    private Chiffrement chiffrement;

    /**
     * Injection de l'instance de chiffrement.
     */
    public void setChiffrement(Chiffrement chiffrement) {
        this.chiffrement = chiffrement;
        this.databaseManager = new DatabaseManager(chiffrement);
        loadPasswordsFromDatabase();  // ⚠️ Charger les données immédiatement
    }
    

    @FXML
    private void initialize() {
        if (chiffrement != null) {
            databaseManager = new DatabaseManager(chiffrement);
            loadPasswordsFromDatabase();
        } else {
            System.err.println("❌ L'instance de Chiffrement n'a pas été initialisée.");
        }
    }


    /**
     * Charge les mots de passe depuis la base de données.
     */
    private void loadPasswordsFromDatabase() {
        passwordContainer.getChildren().clear();
    
        try (ResultSet resultSet = databaseManager.getSiteAndUsername()) {
            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                String site = resultSet.getString("site");
                String username = resultSet.getString("username");
    
                System.out.println("📦 Donnée récupérée - Site: " + site + " | Username: " + username);
    
                addPasswordCard(site, username);
            }
    
            if (!hasData) {
                System.out.println("⚠️ Aucune donnée trouvée dans la base de données.");
            }
    
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des sites et noms d'utilisateur.");
            e.printStackTrace();
        }
    }
    

    /**
     * Ajoute une carte de mot de passe au tableau de bord.
     */
    public void addPasswordCard(String site, String usernameOrEmail) {
        VBox card = new VBox(10);
        card.setPadding(new javafx.geometry.Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10;");
    
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        card.setEffect(shadow);
    
        Label serviceLabel = new Label("🌐 " + site);
        serviceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    
        Label userLabel = new Label("👤 " + usernameOrEmail);
        userLabel.setStyle("-fx-text-fill: #666666;");
    
        Button copyButton = new Button("📋 Copier");
        copyButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-background-radius: 10;");
        copyButton.setOnAction(e -> handleCopyPassword(site));
    
        Button deleteButton = new Button("🗑️ Supprimer");
        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;");
        deleteButton.setOnAction(e -> handleDeletePassword(site, card));
    
        HBox buttonBox = new HBox(10, copyButton, deleteButton);
        card.getChildren().addAll(serviceLabel, userLabel, buttonBox);
    
        passwordContainer.getChildren().add(card);
    
        System.out.println("📄 Carte ajoutée pour le site : " + site);
    }
    

    /**
     * Copie le mot de passe déchiffré dans le presse-papier.
     */
    private void handleCopyPassword(String site) {
        try {
            String decryptedPassword = databaseManager.getDecryptedPasswordBySite(site);

            if (decryptedPassword != null) {
                ClipboardContent content = new ClipboardContent();
                content.putString(decryptedPassword);
                Clipboard.getSystemClipboard().setContent(content);
                System.out.println("🔒 Mot de passe copié : " + decryptedPassword);
            } else {
                System.err.println("❌ Mot de passe introuvable pour le site : " + site);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la copie du mot de passe : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime un mot de passe.
     */
    private void handleDeletePassword(String site, VBox card) {
        try {
            databaseManager.deleteSite(site);
            passwordContainer.getChildren().remove(card);
            System.out.println("🗑️ Mot de passe supprimé pour le site : " + site);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Redirection vers la page d'ajout.
     */
    @FXML
    private void handleAddPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cipher/views/add_password.fxml"));
            Parent addPasswordPage = loader.load();
    
            // ✅ Transfert de l'instance Chiffrement
            AddPasswordController addPasswordController = loader.getController();
            addPasswordController.setChiffrement(chiffrement);  // ⚠️ Important !
    
            Stage stage = (Stage) passwordContainer.getScene().getWindow();
            stage.setScene(new Scene(addPasswordPage));
            stage.show();
    
            System.out.println("📂 Passage à la page d'ajout de mot de passe.");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la redirection vers la page d'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}

package com.cipher.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

import com.cipher.Outils.Chiffrement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView logoImageView;

    private static final String MASTERPWD_FILE = "keystore/passwd.txt";

    @FXML
    private void initialize() {
        checkAndCreateMasterPassword();
    
        // 📥 Chargement du logo
        Image logo = new Image(getClass().getResource("/com/cipher/images/logo.png").toExternalForm());
        logoImageView.setImage(logo);
    }

    /**
     * Vérifie si le mot de passe maître existe, sinon demande à l'utilisateur d'en créer un.
     */
    private void checkAndCreateMasterPassword() {
        try {
            File file = new File(MASTERPWD_FILE);

            // Si le fichier n'existe pas ou est vide
            if (!file.exists() || file.length() == 0) {
                System.out.println("🔒 Aucun mot de passe maître trouvé. Veuillez en créer un.");
                errorLabel.setText("🔒 Aucun mot de passe maître. Veuillez en créer un.");

                passwordField.setOnAction(event -> {
                    String newPassword = passwordField.getText();
                    if (newPassword.isEmpty()) {
                        errorLabel.setText("⚠️ Le mot de passe ne peut pas être vide.");
                    } else {
                        saveMasterPasswordHash(newPassword);
                        errorLabel.setText("✅ Mot de passe maître créé !");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde le mot de passe maître haché.
     */
    private void saveMasterPasswordHash(String masterPassword) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(masterPassword.getBytes(StandardCharsets.UTF_8));

            Files.createDirectories(Paths.get("keystore"));
            try (FileOutputStream fos = new FileOutputStream(MASTERPWD_FILE)) {
                fos.write(hash);
            }

            System.out.println("🔐 Mot de passe maître sauvegardé !");
        } catch (Exception e) {
            throw new RuntimeException("❌ Erreur lors de la sauvegarde du mot de passe maître : " + e.getMessage());
        }
    }

    /**
     * Vérifie si le mot de passe entré correspond au mot de passe maître.
     */
    private boolean verifyMasterPasswordHash(String inputPassword) {
        File passwordFile = new File("keystore/passwd.txt");

        try {
            // Si le fichier n'existe pas ou est vide, demander de créer un mot de passe
            if (!passwordFile.exists() || passwordFile.length() == 0) {
                System.out.println("🔑 Aucun mot de passe maître trouvé. Veuillez en créer un.");
                saveMasterPassword(inputPassword);  // Sauvegarde du mot de passe
                return true;
            }

            // Lire le mot de passe chiffré stocké
            BufferedReader reader = new BufferedReader(new FileReader(passwordFile));
            String savedPasswordHash = reader.readLine();
            reader.close();

            // Hash du mot de passe entré
            String inputPasswordHash = hashPassword(inputPassword);

            // Comparaison des hash
            return inputPasswordHash.equals(savedPasswordHash);

        } catch (IOException e) {
            throw new RuntimeException("❌ Erreur lors de la vérification du mot de passe maître : " + e.getMessage(), e);
        }
    }

    /**
     * Sauvegarde le mot de passe maître sous forme de hash.
     */
    private void saveMasterPassword(String password) {
        try {
            String passwordHash = hashPassword(password);
            FileWriter writer = new FileWriter("keystore/passwd.txt");
            writer.write(passwordHash);
            writer.close();
            System.out.println("🔐 Mot de passe maître sauvegardé avec succès !");
        } catch (IOException e) {
            throw new RuntimeException("❌ Erreur lors de la sauvegarde du mot de passe maître : " + e.getMessage(), e);
        }
    }

    /**
     * Hash le mot de passe avec SHA-256.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("❌ Erreur lors du hashage du mot de passe : " + e.getMessage(), e);
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String masterPassword = passwordField.getText();
    
        if (verifyMasterPasswordHash(masterPassword)) {
            System.out.println("✅ Authentification réussie !");
            
            // ✅ Appel de la méthode avec chiffrement
            loadDashboard(masterPassword);
        } else {
            errorLabel.setText("❌ Mot de passe incorrect !");
        }
    }
    
    private void loadDashboard(String masterPassword) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cipher/views/dashboard.fxml"));
            Parent dashboardRoot = loader.load();
    
            // ✅ Passer l'instance de chiffrement
            DashboardController dashboardController = loader.getController();
            dashboardController.setChiffrement(new Chiffrement(masterPassword));  // ⚠️ Important !
    
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("📂 Gestionnaire de mots de passe");
            stage.show();
    
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("❌ Impossible de charger le Dashboard.");
        }
    }
}

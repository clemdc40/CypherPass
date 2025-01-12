package com.cipher.controllers;

import java.io.IOException;

import com.cipher.Outils.Chiffrement;
import com.cipher.Outils.DatabaseManager;
import com.cipher.Outils.PasswordGenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class AddPasswordController {

    @FXML
    private TextField websiteField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private CheckBox includeUppercase;

    @FXML
    private CheckBox includeNumbers;

    @FXML
    private CheckBox includeSymbols;

    @FXML
    private Slider passwordLengthSlider;

    @FXML
    private Label passwordLengthLabel;

    private Chiffrement chiffrement;
    private DatabaseManager databaseManager;  // ✅ Déclaration ajoutée

    @FXML
    private void initialize() {
        // Désélectionner les cases à cocher par défaut
        includeUppercase.setSelected(false);
        includeNumbers.setSelected(false);
        includeSymbols.setSelected(false);

        // Mettre à jour l'étiquette de longueur de mot de passe lorsque le curseur change
        passwordLengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            passwordLengthLabel.setText(String.valueOf(newVal.intValue()))
        );
    }

    @FXML
    private void handleAddPassword() {
        String site = websiteField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (site.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("⚠️ Tous les champs doivent être remplis !");
            return;
        }

        // 🔒 Chiffrement avec l'instance de chiffrement
        String encryptedPassword = chiffrement.chiffrer(password);

        // 📝 Sauvegarde dans la base de données
        databaseManager.addPassword(site, username, encryptedPassword);

        System.out.println("✅ Mot de passe ajouté à la base de données !");

        // 🔙 Retour au Dashboard
        goToDashboard();
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cipher/views/dashboard.fxml"));
            Parent dashboardRoot = loader.load();
    
            // ✅ Passer l'instance de chiffrement au Dashboard
            DashboardController dashboardController = loader.getController();
            dashboardController.setChiffrement(chiffrement);  // ⚠️ Important !
    
            passwordField.getScene().setRoot(dashboardRoot);
    
            System.out.println("📂 Retour au Dashboard !");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour au Dashboard : " + e.getMessage());
            e.printStackTrace();
        }
    }
        

    
    

    @FXML
    private void handleGeneratePassword() {
        boolean useUppercase = includeUppercase.isSelected();
        boolean useNumbers = includeNumbers.isSelected();
        boolean useSymbols = includeSymbols.isSelected();
        int passwordLength = (int) passwordLengthSlider.getValue();

        String generatedPassword = PasswordGenerator.generatePassword(passwordLength, useUppercase, useNumbers, useSymbols);
        passwordField.setText(generatedPassword);
    }

    public void setChiffrement(Chiffrement chiffrement) {
        this.chiffrement = chiffrement;
        this.databaseManager = new DatabaseManager(chiffrement);
    }

    
}

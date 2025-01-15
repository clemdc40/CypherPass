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
    private DatabaseManager databaseManager;

    @FXML
    private javafx.scene.control.Button generateButton;

    @FXML
    private javafx.scene.control.Button addPasswordButton;


    @FXML
    private void initialize() {
        includeUppercase.setSelected(false);
        includeNumbers.setSelected(false);
        includeSymbols.setSelected(false);
    
        // Met à jour la valeur du label selon le slider
        passwordLengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            passwordLengthLabel.setText(String.valueOf(newVal.intValue()))
        );
    
        // Effets pour le bouton "Générer"
        generateButton.setOnMouseEntered(event ->
            generateButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        generateButton.setOnMouseExited(event ->
            generateButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        generateButton.setOnMousePressed(event ->
            generateButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        generateButton.setOnMouseReleased(event ->
            generateButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 10;")
        );
    
        // Effets pour le bouton "Add Password"
        addPasswordButton.setOnMouseEntered(event ->
            addPasswordButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        addPasswordButton.setOnMouseExited(event ->
            addPasswordButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        addPasswordButton.setOnMousePressed(event ->
            addPasswordButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        addPasswordButton.setOnMouseReleased(event ->
            addPasswordButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 10;")
        );
    }
    

    @FXML
    private void handleAddPassword() {
        String site = websiteField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (site.isEmpty() || password.isEmpty()) {
            System.out.println("⚠️ Tous les champs doivent être remplis !");
            return;
        }

        if(username.isEmpty()) {
            username = "Pas de nom";
        }

        // Encrypt the password
        String encryptedPassword = chiffrement.chiffrer(password);

        // Save the encrypted password to the database
        databaseManager.addPassword(site, username, encryptedPassword);

        System.out.println("✅ Mot de passe ajouté à la base de données !");

        // Return to the dashboard
        goToDashboard();
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cipher/views/dashboard.fxml"));
            Parent dashboardRoot = loader.load();
    
            // Pass the Chiffrement instance to the Dashboard
            DashboardController dashboardController = loader.getController();
            dashboardController.setChiffrement(chiffrement);
    
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

        // Generate a random password based on the selected options
        String generatedPassword = PasswordGenerator.generatePassword(passwordLength, useUppercase, useNumbers, useSymbols);
        passwordField.setText(generatedPassword);
    }

    public void setChiffrement(Chiffrement chiffrement) {
        this.chiffrement = chiffrement;
        this.databaseManager = new DatabaseManager(chiffrement);
    }
}

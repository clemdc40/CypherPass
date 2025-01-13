package com.cipher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/cipher/views/login.fxml"));
        primaryStage.setTitle("CipherPass - Connexion");

        // 🎯 Définir l'icône de la fenêtre
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/cipher/images/logo.png")));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

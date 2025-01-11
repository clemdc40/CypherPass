package com.cipher;

import com.cipher.Outils.Chiffrement;
import com.cipher.Outils.DatabaseManager;
import com.cipher.Outils.PasswordGenerator;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.File;
import java.util.Scanner;

public class App {

    private static final String MASTERPWD_FILE = "keystore/masterpwd.txt";
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Vérifie si le mot de passe maître existe
        File masterFile = new File(MASTERPWD_FILE);
        if (!masterFile.exists() || masterFile.length() == 0) {
            // Définir un nouveau mot de passe maître
            System.out.print("Définissez votre mot de passe maître : ");
            String masterPassword = scanner.nextLine();

            saveMasterPasswordHash(masterPassword);

            System.out.println("🔐 Mot de passe maître sauvegardé avec succès !");
        }

        // Authentification avec le mot de passe maître
        boolean authenticated = false;
        String masterPassword = null;

        while (!authenticated) {
            System.out.print("Entrez votre mot de passe maître : ");
            masterPassword = scanner.nextLine();

            if (verifyMasterPasswordHash(masterPassword)) {
                System.out.println("✅ Authentification réussie !");
                authenticated = true;
            } else {
                System.out.println("❌ Mot de passe maître incorrect. Veuillez réessayer.");
            }
        }

        // Initialisation des gestionnaires
        Chiffrement chiffrement = new Chiffrement(masterPassword);
        DatabaseManager dbManager = new DatabaseManager(chiffrement);

        // Menu des options
        boolean running = true;
        while (running) {
            System.out.println("\n📋 Menu :");
            System.out.println("1️⃣ - Ajouter un mot de passe");
            System.out.println("2️⃣ - Lister tous les mots de passe");
            System.out.println("3️⃣ - Quitter");
            System.out.print("Choisissez une option : ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("🔑 Site : ");
                    String site = scanner.nextLine();
                    System.out.print("👤 Nom d'utilisateur : ");
                    String username = scanner.nextLine();

                    System.out.print("Voulez-vous générer un mot de passe ? (oui/non) : ");
                    String generatePasswordChoice = scanner.nextLine();
                    String password;
                    if (generatePasswordChoice.equalsIgnoreCase("oui")) {
                        System.out.print("Longueur du mot de passe : ");
                        int length = Integer.parseInt(scanner.nextLine());
                        System.out.print("Inclure des minuscules ? (oui/non) : ");
                        boolean useLowercase = scanner.nextLine().equalsIgnoreCase("oui");
                        System.out.print("Inclure des majuscules ? (oui/non) : ");
                        boolean useUppercase = scanner.nextLine().equalsIgnoreCase("oui");
                        System.out.print("Inclure des chiffres ? (oui/non) : ");
                        boolean useDigits = scanner.nextLine().equalsIgnoreCase("oui");
                        System.out.print("Inclure des symboles ? (oui/non) : ");
                        boolean useSymbols = scanner.nextLine().equalsIgnoreCase("oui");

                        password = PasswordGenerator.generatePassword(length, useLowercase, useUppercase, useDigits, useSymbols);
                        System.out.println("🔒 Mot de passe généré : " + password);
                    } else {
                        System.out.print("🔒 Mot de passe : ");
                        password = scanner.nextLine();
                    }

                    dbManager.addPassword(site, username, password);
                    System.out.println("✅ Mot de passe sauvegardé !");
                    break;

                case "2":
                    System.out.println("\n📂 Liste des mots de passe enregistrés :");
                    dbManager.retrievePasswords();
                    break;

                case "3":
                    running = false;
                    System.out.println("👋 Au revoir !");
                    break;

                default:
                    System.out.println("❗ Choix invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }

    /**
     * Stocke le hash du mot de passe maître.
     */
    private static void saveMasterPasswordHash(String masterPassword) {
        try {
            byte[] hash = java.security.MessageDigest.getInstance("SHA-256").digest(masterPassword.getBytes());
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("keystore"));
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(MASTERPWD_FILE)) {
                fos.write(hash);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la sauvegarde du mot de passe maître", e);
            throw new RuntimeException("Erreur lors de la sauvegarde du mot de passe maître : " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie que le hash du mot de passe maître est correct.
     */
    private static boolean verifyMasterPasswordHash(String inputPassword) {
        try {
            File file = new File(MASTERPWD_FILE);
            if (!file.exists() || file.length() == 0) {
                logger.severe("❌ Aucun mot de passe maître sauvegardé !");
                return false;
            }
            byte[] storedHash = java.nio.file.Files.readAllBytes(file.toPath());
            byte[] inputHash = java.security.MessageDigest.getInstance("SHA-256").digest(inputPassword.getBytes());

            // Comparaison des hash
            if (storedHash.length != inputHash.length) return false;
            for (int i = 0; i < storedHash.length; i++) {
                if (storedHash[i] != inputHash[i]) return false;
            }
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification du mot de passe maître", e);
            throw new RuntimeException("Erreur lors de la vérification du mot de passe maître : " + e.getMessage(), e);
        }
    }
}

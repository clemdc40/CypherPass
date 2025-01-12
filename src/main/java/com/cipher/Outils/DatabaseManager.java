package com.cipher.Outils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:keystore/database.db";
    private final Chiffrement chiffrement;

    public DatabaseManager(Chiffrement chiffrement) {
        this.chiffrement = chiffrement;
        createTable();
    }
    

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS passwords ("
               + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
               + "site TEXT,"
               + "username TEXT,"
               + "password TEXT);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la table : " + e.getMessage());
        }
    }

    public void addPassword(String site, String username, String encryptedPassword) {
        String sql = "INSERT INTO passwords (site, username, password) VALUES (?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, site);
            stmt.setString(2, username);
            stmt.setString(3, encryptedPassword);
            stmt.executeUpdate();
    
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du mot de passe : " + e.getMessage());
        }
    }
    

    public void retrievePasswords() {
        String sql = "SELECT site, username, password FROM passwords";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String site = rs.getString("site");
                String username = rs.getString("username");
                String decryptedPassword = chiffrement.dechiffrer(rs.getString("password"));

                System.out.println("🌐 Site : " + site + " | 👤 Utilisateur : " + username + " | 🔑 Mot de passe : " + decryptedPassword);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des mots de passe : " + e.getMessage());
        }
    }

    public ResultSet getSiteAndUsername() {
        String sql = "SELECT site, username FROM passwords";
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des sites et noms d'utilisateur : " + e.getMessage());
        }
    }

    public void deleteSite(String site) {
        String sql = "DELETE FROM passwords WHERE site = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, site);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du site : " + e.getMessage());
        }
    }

    public String getDecryptedPasswordBySite(String site) {
        String sql = "SELECT password FROM passwords WHERE site = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, site);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                return chiffrement.dechiffrer(encryptedPassword);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du mot de passe : " + e.getMessage());
        }
        return null;
    }

    public Chiffrement getChiffrement() {
        return chiffrement;
    }

    public ResultSet getPasswordBySite(String site) {
        String sql = "SELECT password FROM passwords WHERE site = ?";
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, site);
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du mot de passe : " + e.getMessage());
        }
    }
    

}

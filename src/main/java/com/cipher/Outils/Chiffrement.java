package com.cipher.Outils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cette classe gère le chiffrement symétrique avec l'algorithme AES.
 * Elle permet de générer une clé aléatoire ou de charger une clé existante
 * depuis un fichier, puis de chiffrer et déchiffrer des données.
 */
public class Chiffrement {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;                 // Taille de la clé AES (128 bits)
    private static final String DEFAULT_KEY_FILE = "keeper.txt"; // Chemin par défaut vers le fichier de la clé
    private SecretKey secretKey;

    /**
     * Constructeur.
     *
     * @param loadExistingKey Si true, tente de charger la clé existante depuis le fichier DEFAULT_KEY_FILE,
     *                        sinon génère une nouvelle clé aléatoire.
     */
    public Chiffrement(boolean loadExistingKey) {
        if (loadExistingKey) {
            // On tente de charger la clé. Si le fichier n'existe pas ou ne contient pas de clé valide,
            // on génère une clé nouvelle.
            SecretKey keyFromFile = loadKeyFromFile(DEFAULT_KEY_FILE);
            if (keyFromFile != null) {
                this.secretKey = keyFromFile;
            } else {
                this.secretKey = generateSecretKey();
            }
        } else {
            this.secretKey = generateSecretKey();
        }
    }

    /**
     * Génère une clé secrète AES aléatoire de 128 bits.
     *
     * @return La clé secrète générée.
     */
    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la clé AES", e);
        }
    }

    /**
     * Sauvegarde la clé secrète actuelle dans un fichier (encodée en Base64).
     *
     * @param filePath Le chemin du fichier dans lequel sauvegarder la clé (optionnel).
     *                 Si null ou vide, la clé est sauvegardée dans le fichier par défaut (DEFAULT_KEY_FILE).
     * @throws UnsupportedEncodingException En cas de problème d'encodage (très rare avec UTF-8).
     */
    public void saveKey(String filePath) throws UnsupportedEncodingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = DEFAULT_KEY_FILE;
        }
        // Utilisation d’un try-with-resources pour gérer automatiquement la fermeture du PrintWriter
        try (PrintWriter writer = new PrintWriter(filePath)) {
            String encodedKey = Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
            writer.println(encodedKey);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la clé dans le fichier : " + filePath, e);
        }
    }

    /**
     * Lit la clé secrète à partir d’un fichier (encodée en Base64).
     *
     * @param filePath Le chemin du fichier contenant la clé.
     * @return La clé secrète si elle est correctement chargée, null sinon.
     */
    private SecretKey loadKeyFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String encodedKey = reader.readLine();
            if (encodedKey == null || encodedKey.trim().isEmpty()) {
                // Fichier vide ou contenu invalide
                return null;
            }
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        } catch (IOException e) {
            // En cas d’erreur (fichier introuvable ou problème de lecture),
            // on retourne simplement null, ce qui permet de gérer le cas dans le constructeur.
            return null;
        }
    }

    /**
     * Chiffre une chaîne de texte en utilisant la clé secrète actuelle.
     *
     * @param texte Le texte à chiffrer.
     * @return Le texte chiffré, encodé en Base64.
     */
    public String chiffrer(String texte) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            byte[] texteChiffre = cipher.doFinal(texte.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(texteChiffre);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chiffrement", e);
        }
    }

    /**
     * Déchiffre une chaîne chiffrée en Base64 en utilisant la clé secrète actuelle.
     *
     * @param texteChiffre Le texte chiffré, encodé en Base64.
     * @return Le texte en clair.
     */
    public String dechiffrer(String texteChiffre) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            byte[] texteDecode = Base64.getDecoder().decode(texteChiffre);
            byte[] texteDechiffre = cipher.doFinal(texteDecode);
            return new String(texteDechiffre, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du déchiffrement", e);
        }
    }

    /**
     * Retourne la clé secrète sous forme de chaîne encodée en Base64.
     *
     * @return La clé secrète encodée en Base64.
     */
    public String getSecretKeyAsString() {
        return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
    }

    /**
     * Recharge la clé secrète à partir d'une chaîne encodée en Base64.
     *
     * @param keyStr Représentation Base64 de la clé.
     */
    public void setSecretKeyFromString(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    /**
     * Retourne la clé secrète interne (objet SecretKey).
     *
     * @return La clé secrète en cours d'utilisation.
     */
    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public void savePassword(String password) {
        File file = new File("passwd.txt");
        if (!file.exists() || file.length() == 0) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(password);
                System.out.println("Mot de passe sauvegardé avec succès.");
            } catch (IOException e) {
                System.err.println("Erreur lors de la sauvegarde du mot de passe : " + e.getMessage());
            }
        } else {
            System.out.println("Le fichier passwd.txt existe déjà et n'est pas vide.");
        }
    }

    public Boolean connectPassword(String password){
        File file = new File("passwd.txt");
        if (file.exists() && file.length() != 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line.equals(password)) {
                    //System.out.println("Mot de passe correct.");
                    return true;
                } else {
                    //System.out.println("Mot de passe incorrect.");
                    return false;
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture du mot de passe : " + e.getMessage());
            }
        } else {
            System.out.println("Le fichier passwd.txt n'existe pas ou est vide.");
        }
                return null;
    }
}

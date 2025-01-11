package com.cipher.Outils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chiffrement {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_KEY_FILE = "keystore/keeper.key";
    private static final String SALT_FILE = "keystore/salt.dat";
    private static final String PASSWORD_FILE = "keystore/passwd.txt";
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 100_000;
    private static final Logger logger = Logger.getLogger(Chiffrement.class.getName());

    private byte[] derivedKey;

    /**
     * Constructeur : on dérive la clé à partir du (vrai) mot de passe maître.
     */
    public Chiffrement(String masterPassword) {
        try {
            // Charger ou générer le salt
            byte[] salt = loadOrGenerateSalt();

            // Charger ou générer le fichier clé
            byte[] keyFileData = loadOrGenerateKeyFile();

            // Générer la Composite Key (Mot de passe maître + Fichier clé)
            byte[] compositeKey = generateCompositeKey(masterPassword, keyFileData);

            // Dériver la clé AES avec PBKDF2
            this.derivedKey = deriveKey(compositeKey, salt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'initialisation du chiffrement", e);
            throw new RuntimeException("Erreur lors de l'initialisation du chiffrement : " + e.getMessage(), e);
        }
    }

    /**
     * Génère ou charge le salt.
     */
    private byte[] loadOrGenerateSalt() throws IOException {
        File saltFile = new File(SALT_FILE);
        if (!saltFile.exists()) {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            Files.createDirectories(Paths.get("keystore"));
            try (FileOutputStream fos = new FileOutputStream(saltFile)) {
                fos.write(salt);
            }
            return salt;
        }
        return Files.readAllBytes(Paths.get(SALT_FILE));
    }

    /**
     * Génère ou charge le fichier clé.
     */
    private byte[] loadOrGenerateKeyFile() throws IOException {
        File keyFile = new File(DEFAULT_KEY_FILE);
        if (!keyFile.exists()) {
            byte[] keyFileData = new byte[64];
            new SecureRandom().nextBytes(keyFileData);
            Files.createDirectories(Paths.get("keystore"));
            try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                fos.write(keyFileData);
            }
            return keyFileData;
        }
        return Files.readAllBytes(Paths.get(DEFAULT_KEY_FILE));
    }

    /**
     * Génère la Composite Key (Mot de passe maître + Fichier clé).
     */
    private byte[] generateCompositeKey(String password, byte[] keyFileData) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] passwordHash = sha256.digest(password.getBytes(StandardCharsets.UTF_8));
        byte[] compositeKey = new byte[passwordHash.length + keyFileData.length];

        System.arraycopy(passwordHash, 0, compositeKey, 0, passwordHash.length);
        System.arraycopy(keyFileData, 0, compositeKey, passwordHash.length, keyFileData.length);

        return sha256.digest(compositeKey);
    }

    /**
     * Dérive la clé AES avec PBKDF2.
     */
    private byte[] deriveKey(byte[] compositeKey, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                Base64.getEncoder().encodeToString(compositeKey).toCharArray(),
                salt,
                ITERATIONS,
                KEY_SIZE
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Chiffre un texte en utilisant la clé dérivée.
     */
    public String chiffrer(String texte) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(derivedKey, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(texte.getBytes(StandardCharsets.UTF_8));

            // Combinaison IV + données chiffrées
            byte[] ivAndEncrypted = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, ivAndEncrypted, 0, iv.length);
            System.arraycopy(encrypted, 0, ivAndEncrypted, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(ivAndEncrypted);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du chiffrement", e);
            throw new RuntimeException("Erreur lors du chiffrement : " + e.getMessage(), e);
        }
    }

    /**
     * Déchiffre un texte chiffré.
     */
    public String dechiffrer(String texteChiffre) {
        try {
            byte[] ivAndEncrypted = Base64.getDecoder().decode(texteChiffre);
            byte[] iv = new byte[16];
            byte[] encryptedBytes = new byte[ivAndEncrypted.length - 16];

            System.arraycopy(ivAndEncrypted, 0, iv, 0, 16);
            System.arraycopy(ivAndEncrypted, 16, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(derivedKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));

            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du déchiffrement", e);
            throw new RuntimeException("Erreur lors du déchiffrement : " + e.getMessage(), e);
        }
    }

    /**
     * Sauvegarde un mot de passe (ou n'importe quel texte) de manière chiffrée.
     */
    public void savePassword(String password) {
        try {
            String encryptedPassword = chiffrer(password);
            File file = new File(PASSWORD_FILE);
            Files.createDirectories(Paths.get("keystore"));
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(encryptedPassword);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la sauvegarde du mot de passe", e);
            throw new RuntimeException("Erreur lors de la sauvegarde du mot de passe : " + e.getMessage(), e);
        }
    }

    /**
     * Charge et déchiffre le mot de passe stocké dans passwd.txt (ou renvoie une string vide s’il n’y en a pas).
     */
    public String loadSavedPassword() {
        File file = new File(PASSWORD_FILE);
        if (!file.exists() || file.length() == 0) {
            logger.info("Aucun mot de passe sauvegardé.");
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String encryptedPassword = reader.readLine();
            if (encryptedPassword == null || encryptedPassword.isEmpty()) {
                return "";
            }
            return dechiffrer(encryptedPassword);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la lecture du mot de passe sauvegardé", e);
            throw new RuntimeException("Erreur lors de la lecture du mot de passe sauvegardé : " + e.getMessage(), e);
        }
    }
}

package com.cipher.Outils;

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
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Chiffrement {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_KEY_FILE = "keystore/keeper.key";
    private static final String SALT_FILE = "keystore/salt.dat";
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 100_000;
    private static final Logger logger = Logger.getLogger(Chiffrement.class.getName());
    private static final String PASSWORD_FILE = "keystore/passwd.txt";

    private byte[] derivedKey;

    /**
     * Constructeur : on dérive la clé à partir du mot de passe maître.
     */
    public Chiffrement(String masterPassword) {
        try {
            ensureKeystoreDirectoryExists();  // ✅ Vérifier que le dossier existe
            byte[] salt = loadOrGenerateSalt();
            byte[] keyFileData = loadOrGenerateKeyFile();
            byte[] compositeKey = generateCompositeKey(masterPassword, keyFileData);
            this.derivedKey = deriveKey(compositeKey, salt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de l'initialisation du chiffrement", e);
            throw new RuntimeException("❌ Erreur lors de l'initialisation du chiffrement : " + e.getMessage(), e);
        }
    }

    /**
     * ✅ Crée le dossier `keystore` s'il n'existe pas.
     */
    private void ensureKeystoreDirectoryExists() throws IOException {
        File keystoreDir = new File("keystore");
        if (!keystoreDir.exists()) {
            if (keystoreDir.mkdirs()) {
                logger.info("📁 Dossier 'keystore' créé.");
            } else {
                throw new IOException("❌ Impossible de créer le dossier 'keystore'.");
            }
        }
    }

    /**
     * ✅ Génère ou charge le salt.
     */
    private byte[] loadOrGenerateSalt() throws IOException {
        File saltFile = new File(SALT_FILE);
        if (!saltFile.exists()) {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            try (FileOutputStream fos = new FileOutputStream(saltFile)) {
                fos.write(salt);
            }
            logger.info("🔑 Salt généré et sauvegardé.");
            return salt;
        }
        logger.info("📥 Salt chargé depuis le fichier.");
        return Files.readAllBytes(Paths.get(SALT_FILE));
    }

    /**
     * ✅ Génère ou charge le fichier clé.
     */
    private byte[] loadOrGenerateKeyFile() throws IOException {
        File keyFile = new File(DEFAULT_KEY_FILE);
        if (!keyFile.exists()) {
            byte[] keyFileData = new byte[64];
            new SecureRandom().nextBytes(keyFileData);
            try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                fos.write(keyFileData);
            }
            logger.info("🔐 Fichier clé généré et sauvegardé.");
            return keyFileData;
        }
        logger.info("📥 Fichier clé chargé depuis le disque.");
        return Files.readAllBytes(Paths.get(DEFAULT_KEY_FILE));
    }

    /**
     * ✅ Génère la Composite Key (Mot de passe maître + Fichier clé).
     */
    private byte[] generateCompositeKey(String password, byte[] keyFileData) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] passwordHash = sha256.digest(password.getBytes());
        byte[] compositeKey = new byte[passwordHash.length + keyFileData.length];

        System.arraycopy(passwordHash, 0, compositeKey, 0, passwordHash.length);
        System.arraycopy(keyFileData, 0, compositeKey, passwordHash.length, keyFileData.length);

        logger.info("🔗 Composite Key générée.");
        return sha256.digest(compositeKey);
    }

    /**
     * ✅ Dérive la clé AES avec PBKDF2.
     */
    private byte[] deriveKey(byte[] compositeKey, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                Base64.getEncoder().encodeToString(compositeKey).toCharArray(),
                salt,
                ITERATIONS,
                KEY_SIZE
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        logger.info("🔑 Clé AES dérivée avec succès.");
        return factory.generateSecret(spec).getEncoded();
    }
    /**
     * Chiffre un texte.
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

            byte[] ivAndEncrypted = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, ivAndEncrypted, 0, iv.length);
            System.arraycopy(encrypted, 0, ivAndEncrypted, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(ivAndEncrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chiffrement", e);
        }
    }

    /**
     * Déchiffre un texte.
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
            throw new RuntimeException("Erreur lors du déchiffrement", e);
        }
    }

    /**
     * Sauvegarde un mot de passe de manière chiffrée.
     */
    public void savePassword(String password) {
        try {
            String encryptedPassword = chiffrer(password);
            File file = new File(PASSWORD_FILE);
            Files.createDirectories(Paths.get("keystore"));
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(encryptedPassword);
            }
            logger.info("✅ Mot de passe sauvegardé.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de la sauvegarde du mot de passe", e);
            throw new RuntimeException("Erreur lors de la sauvegarde du mot de passe", e);
        }
    }

    /**
     * Charge et déchiffre le mot de passe stocké.
     */
    public String loadSavedPassword() {
        File file = new File(PASSWORD_FILE);
        if (!file.exists() || file.length() == 0) {
            logger.warning("⚠️ Aucun mot de passe sauvegardé.");
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String encryptedPassword = reader.readLine();
            return (encryptedPassword != null && !encryptedPassword.isEmpty()) ? dechiffrer(encryptedPassword) : "";
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du mot de passe", e);
        }
    }
}

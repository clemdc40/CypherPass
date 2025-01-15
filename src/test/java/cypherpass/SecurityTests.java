package cypherpass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cipher.Outils.Chiffrement;
import com.cipher.Outils.DatabaseManager;

public class SecurityTests {

    private Chiffrement chiffrement;
    private DatabaseManager databaseManager;
    private static final String MASTER_PASSWORD = "TestPassword123!";

    @BeforeEach
    void setUp() {
        chiffrement = new Chiffrement(MASTER_PASSWORD);
        databaseManager = new DatabaseManager(chiffrement);
    }

    @Test
    void testPasswordEncryptionAndDecryption() {
        String originalPassword = "SecurePass123!";
        String encryptedPassword = chiffrement.chiffrer(originalPassword);
        String decryptedPassword = chiffrement.dechiffrer(encryptedPassword);

        assertNotEquals(originalPassword, encryptedPassword, "Le mot de passe ne doit pas être identique après chiffrement.");
        assertEquals(originalPassword, decryptedPassword, "Le mot de passe doit être correctement déchiffré.");
    }

    @Test
    void testAddPasswordToDatabase() {
        String site = "example.com";
        String username = "user@example.com";
        String password = "Test1234!";
        String encryptedPassword = chiffrement.chiffrer(password);

        databaseManager.addPassword(site, username, encryptedPassword);
        String retrievedPassword = databaseManager.getDecryptedPasswordBySite(site);

        assertEquals(password, retrievedPassword, "Le mot de passe récupéré doit correspondre au mot de passe initial.");
    }

    @Test
    void testMasterPasswordHashing() {
        String hashedPassword1 = chiffrement.chiffrer(MASTER_PASSWORD);
        String hashedPassword2 = chiffrement.chiffrer(MASTER_PASSWORD);

        assertNotEquals(MASTER_PASSWORD, hashedPassword1, "Le mot de passe ne doit pas être stocké en clair.");
        assertNotEquals(hashedPassword1, hashedPassword2, "Chaque chiffrement doit générer un résultat différent (IV aléatoire).");
    }

    @Test
    void testDatabaseEntryDeletion() {
        String site = "deleteTest.com";
        String username = "deleteUser";
        String password = "Delete1234!";

        databaseManager.addPassword(site, username, chiffrement.chiffrer(password));
        databaseManager.deleteSite(site);
        String retrievedPassword = databaseManager.getDecryptedPasswordBySite(site);

        assertNull(retrievedPassword, "Le mot de passe doit être supprimé de la base de données.");
    }
}

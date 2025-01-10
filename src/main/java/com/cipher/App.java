package com.cipher;
import com.cipher.Outils.Chiffrement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.sun.imageio.stream.StreamCloser;

import sun.util.calendar.CalendarSystem;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Chiffrement chiffrement = new Chiffrement(true);

        // Exemple de texte à chiffrer
        String texteClair = "Azertyuiop40/";

        // Chiffrement du texte
        String texteChiffre = chiffrement.chiffrer(texteClair);
        System.out.println("Texte chiffré : " + texteChiffre);

        // Déchiffrement du texte
        String texteDechiffre = chiffrement.dechiffrer(texteChiffre);
        System.out.println("Texte déchiffré : " + texteDechiffre);

        // Sauvegarde de la clé secrète dans un fichier
        try {
            chiffrement.saveKey(null);  // Sauvegarde dans le fichier par défaut "keeper.txt"
            System.out.println("Clé secrète sauvegardée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la clé : " + e.getMessage());
        }
}
}
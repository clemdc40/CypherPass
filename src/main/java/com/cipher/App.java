package com.cipher;

import com.cipher.Outils.Chiffrement;

public class App {
    public static void main(String[] args) {
        Chiffrement chiffrement = new Chiffrement(true);

        String texteClair = "Azertyuiop40/";
        String texteChiffre = chiffrement.chiffrer(texteClair);
        System.out.println("Texte chiffré : " + texteChiffre);

        String texteDechiffre = chiffrement.dechiffrer(texteChiffre);
        System.out.println("Texte déchiffré : " + texteDechiffre);

        try {
            chiffrement.saveKey(null);
            System.out.println("Clé secrète sauvegardée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la clé : " + e.getMessage());
        }
    }
}

package com.cipher;

import java.util.Scanner;

import com.cipher.Outils.Chiffrement;

public class App {
    public static void main(String[] args) {
        Chiffrement chiffrement = new Chiffrement(true);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre mot de passe : ");
        String texteClair = scanner.nextLine();

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

        try {
            chiffrement.savePassword(texteChiffre);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du mot de passe : " + e.getMessage());
        }        

        Boolean passwordMatch = false;
        while (!passwordMatch) {
            System.out.print("Entrez votre mot de passe pour vérifier : ");
            String inputPassword = scanner.nextLine();
            String encryptedInputPassword = chiffrement.chiffrer(inputPassword);

            try {
            passwordMatch = chiffrement.connectPassword(encryptedInputPassword);
            if (passwordMatch) {
                System.out.println("Mot de passe correct.");
            } else {
                System.out.println("Mot de passe incorrect. Veuillez réessayer.");
            }
            } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du mot de passe : " + e.getMessage());
            }
        }


        scanner.close();
    }
}

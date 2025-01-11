package com.cipher.Outils;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_+=<>?";

    public static String generatePassword(int length, boolean useLowercase, boolean useUppercase, boolean useDigits, boolean useSymbols) {
        StringBuilder characterSet = new StringBuilder();
        if (useLowercase) {
            characterSet.append(LOWERCASE);
        }
        if (useUppercase) {
            characterSet.append(UPPERCASE);
        }
        if (useDigits) {
            characterSet.append(DIGITS);
        }
        if (useSymbols) {
            characterSet.append(SYMBOLS);
        }

        if (characterSet.length() == 0) {
            throw new IllegalArgumentException("Au moins un type de caractère doit être sélectionné.");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterSet.length());
            password.append(characterSet.charAt(index));
        }

        return password.toString();
    }
}

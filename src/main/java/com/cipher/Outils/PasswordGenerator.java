package com.cipher.Outils;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_+=<>?";

    public static String generatePassword(int length, boolean useUppercase, boolean useNumbers, boolean useSymbols) {
        StringBuilder characterPool = new StringBuilder(LOWERCASE);
        SecureRandom random = new SecureRandom();

        if (useUppercase) {
            characterPool.append(UPPERCASE);
        }
        if (useNumbers) {
            characterPool.append(NUMBERS);
        }
        if (useSymbols) {
            characterPool.append(SYMBOLS);
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterPool.length());
            password.append(characterPool.charAt(index));
        }

        return password.toString();
    }
}

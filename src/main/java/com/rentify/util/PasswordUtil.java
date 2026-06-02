package com.rentify.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No se pudo aplicar hash a la contraseña.", e);
        }
    }

    public static boolean verificarPassword(String passwordPlano, String passwordHashGuardado) {
        if (passwordPlano == null || passwordHashGuardado == null) {
            return false;
        }

        String passwordHashIngresado = hashPassword(passwordPlano);
        return passwordHashIngresado.equals(passwordHashGuardado);
    }
}
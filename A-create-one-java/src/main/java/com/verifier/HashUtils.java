package com.verifier;

public class HashUtils {

    /**
     * Конвертирует байты хеша в hex-строку.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Сравнивает два хеша нечувствительно к регистру.
     */
    public static boolean compareHashes(String hash1, String hash2) {
        if (hash1 == null || hash2 == null) {
            return false;
        }
        return hash1.equalsIgnoreCase(hash2);
    }
}

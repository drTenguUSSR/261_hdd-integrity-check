/*
 *
 * @author DrTengu. 2026/02
 */

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
     * Более эффективное преобразование байтов хеша в hex-строку для процессора 11400F.
     */
    public static String bytesToHexOptimized(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
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

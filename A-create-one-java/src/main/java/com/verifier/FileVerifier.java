package com.verifier;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.MessageDigest;

public class FileVerifier {

    private static final int BUFFER_SIZE = 32 * 1024 * 1024; // 32 Мб

    /**
     * Читает файл с диска, вычисляет SHA256 для верификации.
     * @return Hex-строка хеша
     */
    public static String readAndHashFile(Path filePath, long totalBytes) throws Exception {
        byte[] buffer = new byte[BUFFER_SIZE];
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             BufferedInputStream bis = new BufferedInputStream(fis, BUFFER_SIZE)) {

            long read = 0;
            long timerStart = System.nanoTime();
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) > 0) {
                // Вычисление хеша прочитанных данных
                digest.update(buffer, 0, bytesRead);
                read += bytesRead;

                // Прогресс (обновление каждые 0.5 сек)
                double elapsed = (System.nanoTime() - timerStart) / 1_000_000_000.0;
                if (elapsed >= 0.5) {
                    double percent = (read * 100.0) / totalBytes;
                    double mb = read / (1024.0 * 1024.0);
                    double speed = mb / elapsed;
                    System.out.printf("\r  📊 Прогресс: %5.1f%% | Скорость: %7.1f MB/s | Прочитано: %.2f GB",
                            percent, speed, read / (1024.0 * 1024.0 * 1024.0));
                    timerStart = System.nanoTime();
                }
            }
        }

        System.out.println();
        return HashUtils.bytesToHex(digest.digest());
    }
}

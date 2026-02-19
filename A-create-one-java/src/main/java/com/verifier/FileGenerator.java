/*
 *
 * @author DrTengu. 2026/02
 */

package com.verifier;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class FileGenerator {

    private static final int BUFFER_SIZE = 32 * 1024 * 1024; // 32 Мб

    /**
     * Генерирует случайные данные, вычисляет SHA256 и записывает на диск.
     * @return Hex-строка хеша
     */
    public static String generateAndHashFile(Path filePath, long totalBytes) throws Exception {
        byte[] buffer = new byte[BUFFER_SIZE];
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        SecureRandom rng = new SecureRandom();

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE)) {

            long written = 0;
            long timerStart = System.nanoTime();

            while (written < totalBytes) {
                int toWrite = (int) Math.min(BUFFER_SIZE, totalBytes - written);

                // 1. Генерация криптографически случайных данных
                rng.nextBytes(buffer);

                // 2. Вычисление хеша в памяти
                digest.update(buffer, 0, toWrite);

                // 3. Запись на диск
                bos.write(buffer, 0, toWrite);
                written += toWrite;

                // Прогресс (обновление каждые 0.5 сек)
                double elapsed = (System.nanoTime() - timerStart) / 1_000_000_000.0;
                if (elapsed >= 0.5) {
                    double percent = (written * 100.0) / totalBytes;
                    double mb = written / (1024.0 * 1024.0);
                    double speed = mb / elapsed;
                    System.out.printf("\r  📊 Прогресс: %5.1f%% | Скорость: %7.1f MB/s | Записано: %.2f GB",
                            percent, speed, written / (1024.0 * 1024.0 * 1024.0));
                    timerStart = System.nanoTime();
                }
            }
            bos.flush();
        }

        System.out.println();
        return HashUtils.bytesToHex(digest.digest());
    }
}

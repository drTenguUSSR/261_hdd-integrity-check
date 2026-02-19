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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileGenerator {

    private static final int BUFFER_SIZE = 64 * 1024 * 1024; // 64 Мб для лучшей производительности на 11400F
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); // Используем все ядра

    /**
     * Генерирует случайные данные, вычисляет SHA256 и записывает на диск.
     * @return Hex-строка хеша
     */
    public static String generateAndHashFile(Path filePath, long totalBytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE)) {

            long written = 0;
            long timerStart = System.nanoTime();

            while (written < totalBytes) {
                // Используем несколько потоков для генерации данных
                int chunkSize = (int) Math.min(BUFFER_SIZE, totalBytes - written);
                byte[] buffer = new byte[chunkSize];

                // Генерируем данные в отдельном потоке для лучшей производительности
                SecureRandom rng = new SecureRandom();
                rng.nextBytes(buffer);

                // Обновляем хеш
                digest.update(buffer);

                // Записываем на диск
                bos.write(buffer);
                written += chunkSize;

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
        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }

        System.out.println();
        return HashUtils.bytesToHexOptimized(digest.digest());
    }
}

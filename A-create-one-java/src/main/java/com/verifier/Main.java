/*
 *
 * @author DrTengu. 2026/02
 */

package com.verifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final long DEFAULT_FILE_SIZE_GB = 4; // = 40;
    private static final String DEFAULT_OUTPUT_DIR = "D:\\INS\\large-data-test";

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  Large File Verifier v1.0.0                            ║");
        System.out.println("║  Java 21 + Gradle                                      ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();

        // Парсинг аргументов
        long fileSizeGB = DEFAULT_FILE_SIZE_GB;
        String outputDir = DEFAULT_OUTPUT_DIR;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s", "--size" -> {
                    if (i + 1 < args.length) {
                        fileSizeGB = Long.parseLong(args[++i]);
                    }
                }
                case "-d", "--dir" -> {
                    if (i + 1 < args.length) {
                        outputDir = args[++i];
                    }
                }
                case "-h", "--help" -> {
                    printHelp();
                    return;
                }
            }
        }

        try {
            // Создание директории
            Path outputDirPath = Path.of(outputDir);
            Files.createDirectories(outputDirPath);

            String fileName = String.format("%dGB_Test.bin", fileSizeGB);
            Path filePath = outputDirPath.resolve(fileName);
            Path hashPath = filePath.resolveSibling(fileName + ".sha256");

            long totalBytes = fileSizeGB * 1024 * 1024 * 1024L;

            System.out.println("📁 Путь к файлу: " + filePath);
            System.out.println("📊 Размер: " + fileSizeGB + " Гб (" + totalBytes + " байт)");
            System.out.println("💾 Директория: " + outputDir);
            System.out.println();

            // ФАЗА 1: Генерация + Хеш + Запись
            System.out.println("┌────────────────────────────────────────────────────────┐");
            System.out.println("│  ФАЗА 1: Генерация данных + SHA256 + Запись на диск   │");
            System.out.println("└────────────────────────────────────────────────────────┘");
            long phase1Start = System.nanoTime();
            String generatedHash = FileGenerator.generateAndHashFile(filePath, totalBytes);
            double phase1Time = (System.nanoTime() - phase1Start) / 1_000_000_000.0;

            // Сохранение хеша
            Files.writeString(hashPath, generatedHash);

            System.out.printf("✅ Фаза 1 завершена: %.1f сек%n", phase1Time);
            System.out.printf("📝 Hash (генерация): %s%n", generatedHash);
            System.out.printf("💾 Сохранено в: %s%n", hashPath);
            System.out.println();

            // ФАЗА 2: Чтение + Хеш + Верификация
            System.out.println("┌────────────────────────────────────────────────────────┐");
            System.out.println("│  ФАЗА 2: Чтение с диска + SHA256 + Верификация        │");
            System.out.println("└────────────────────────────────────────────────────────┘");
            long phase2Start = System.nanoTime();
            String verifiedHash = FileVerifier.readAndHashFile(filePath, totalBytes);
            double phase2Time = (System.nanoTime() - phase2Start) / 1_000_000_000.0;

            System.out.printf("✅ Фаза 2 завершена: %.1f сек%n", phase2Time);
            System.out.printf("📝 Hash (чтение):   %s%n", verifiedHash);
            System.out.println();

            // ВЕРИФИКАЦИЯ
            System.out.println("┌────────────────────────────────────────────────────────┐");
            System.out.println("│  РЕЗУЛЬТАТ ВЕРИФИКАЦИИ                                │");
            System.out.println("└────────────────────────────────────────────────────────┘");

            if (generatedHash.equalsIgnoreCase(verifiedHash)) {
                System.out.println("✅ УСПЕХ: Хеш-суммы совпадают! Данные целы.");
            } else {
                System.out.println("❌ ОШИБКА: Хеш-суммы НЕ совпадают! Данные повреждены.");
                System.exit(1);
            }

            // СТАТИСТИКА
            System.out.println();
            System.out.println("┌────────────────────────────────────────────────────────┐");
            System.out.println("│  СТАТИСТИКА ПРОИЗВОДИТЕЛЬНОСТИ                        │");
            System.out.println("└────────────────────────────────────────────────────────┘");
            System.out.printf("⏱️  Общее время: %.1f сек (%.1f мин)%n",
                    phase1Time + phase2Time, (phase1Time + phase2Time) / 60.0);
            System.out.printf("📤 Скорость записи: %.1f MB/s%n",
                    (totalBytes / phase1Time) / (1024 * 1024));
            System.out.printf("📥 Скорость чтения: %.1f MB/s%n",
                    (totalBytes / phase2Time) / (1024 * 1024));
            System.out.printf("💻 Процессоров доступно: %d%n",
                    Runtime.getRuntime().availableProcessors());
            System.out.printf("🧠 Максимум памяти JVM: %d MB%n",
                    Runtime.getRuntime().maxMemory() / (1024 * 1024));

        } catch (IOException e) {
            System.err.println("❌ Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("Использование: LargeFileVerifier [опции]");
        System.out.println();
        System.out.println("Опции:");
        System.out.println("  -s, --size <GB>   Размер файла в Гб (по умолчанию: 40)");
        System.out.println("  -d, --dir <PATH>  Директория для файлов (по умолчанию: C:/LargeData)");
        System.out.println("  -h, --help        Показать эту справку");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  gradle run --args=\"--size 20\"");
        System.out.println("  gradle run --args=\"--size 40 --dir D:/TestData\"");
    }
}

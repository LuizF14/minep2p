package br.edu.unifei.minep2p.infrastructure;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class DirectoryZipper {
    public static Path zipDirectory(Path sourceDir) throws IOException {
        Path zipPath = Files.createTempFile("packet_", ".zip");

        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path) && !path.getFileName().toString().equals("session.lock"))
                    .forEach(path -> {
                        try (InputStream in = Files.newInputStream(path)) {
                            ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                            zs.putNextEntry(zipEntry);
                            in.transferTo(zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
        return zipPath;
    }

    public static void unzipDirectory(Path zipPath, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(entry.getName()).normalize();

                if (!newPath.startsWith(targetDir)) {
                    throw new IOException("Entrada ZIP inválida: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        Files.createDirectories(newPath.getParent());
                    }
                    try (OutputStream out = Files.newOutputStream(newPath)) {
                        zis.transferTo(out);
                    }
                }
                zis.closeEntry();
            }
        }
    }
}

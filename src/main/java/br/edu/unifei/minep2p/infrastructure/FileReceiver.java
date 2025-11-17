package br.edu.unifei.minep2p.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReceiver {
    private static final int BUFFER_SIZE = 8192;
    public static Path receiveFile(long totalBytesToRead, InputStream stream) throws IOException {
        Path tempFilePath = Files.createTempFile("packet-", ".zip");

        try (OutputStream fileOut = Files.newOutputStream(tempFilePath)) {
            byte[] buffer = new byte[BUFFER_SIZE]; 
            long bytesRemaining = totalBytesToRead;
            
            while (bytesRemaining > 0) {
                int chunkSize = (int) Math.min(buffer.length, bytesRemaining);
                int bytesRead = stream.read(buffer, 0, chunkSize);

                // System.out.println("Progress: " + String.valueOf(totalBytesToRead - bytesRemaining) + " / " + String.valueOf(totalBytesToRead));
                
                if (bytesRead == -1) {
                    Files.deleteIfExists(tempFilePath);
                    throw new IOException("Fim inesperado do stream durante a transferência do arquivo.");
                }
                
                fileOut.write(buffer, 0, bytesRead);
                bytesRemaining -= bytesRead;
            }

            return tempFilePath;
        } catch (IOException e) {
            Files.deleteIfExists(tempFilePath);
            throw e;
        }
    }
}

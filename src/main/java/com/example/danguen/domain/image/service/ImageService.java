package com.example.danguen.domain.image.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public interface ImageService {
    default void deleteFolder(String folderPath) {
        Path path = Paths.get(folderPath);
        try {
            try (var stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (Exception e) {
            //todo throw error message to client
            System.out.println("폴더 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

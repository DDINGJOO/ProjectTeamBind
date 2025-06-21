package image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String store(MultipartFile file, String relativePath);
    String store(byte[] imageBytes, String relativePath);
    void delete(String relativePath);

}
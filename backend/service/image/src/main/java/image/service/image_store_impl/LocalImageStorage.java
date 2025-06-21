package image.service.image_store_impl;


import exception.error_code.image.ImageErrorCode;
import exception.excrptions.ImageException;
import image.service.ImageStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class LocalImageStorage implements ImageStorage {

    @Value("${image.upload.dir}")
    private String baseDir;

    @Override
    public String store(MultipartFile file, String relativePath) {
        try {
            File target = new File(baseDir + relativePath);
            target.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(target)) {
                fos.write(file.getBytes());
            }
        } catch (IOException e) {
            throw new ImageException(ImageErrorCode.IMAGE_DOWNLOAD_FAILED);
        }
        return relativePath;
    }

    // 바이트 배열 저장 (WebP 변환 결과 바로 저장)
    public String store(byte[] imageBytes, String relativePath) {
        try {
            File target = new File(baseDir + relativePath);
            target.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(target)) {
                fos.write(imageBytes);
            }
        } catch (IOException e) {
            throw new ImageException(ImageErrorCode.IMAGE_DOWNLOAD_FAILED);
        }
        return relativePath;
    }

    @Override
    public void delete(String relativePath) {
        File file = new File(baseDir + relativePath);
        if (file.exists()) file.delete();
    }
}
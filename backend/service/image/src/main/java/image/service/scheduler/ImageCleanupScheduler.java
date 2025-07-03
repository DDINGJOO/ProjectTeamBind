package image.service.scheduler;

import eurm.ImageStatus;
import image.entity.Image;
import image.repository.ImageRepository;
import image.service.ImageStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageCleanupScheduler {

    private final ImageRepository imageRepository;
    private final ImageStorage  imageStorage;
    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredImages() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);

        List<Image> expired = new ArrayList<>();
        expired.addAll(imageRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff));
        expired.addAll(imageRepository.findByStatusAndPendingDeleteAtBefore(ImageStatus.PENDING_DELETE, cutoff));
        expired.addAll(imageRepository.findByStatus(ImageStatus.REJECTED));

        for (Image image : expired) {
            imageStorage.delete(image.getStoredPath());
            imageRepository.delete(image);
        }
    }

}

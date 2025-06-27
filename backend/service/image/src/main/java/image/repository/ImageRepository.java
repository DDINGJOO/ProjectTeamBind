package image.repository;


import eurm.ImageStatus;
import eurm.ResourceCategory;
import image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository <Image,Long>
{
    List<Image> findByCategoryAndReferenceId(ResourceCategory category, Long referenceId);


    List<Image> findByStatusAndCreatedAtBefore(ImageStatus imageStatus, LocalDateTime cutoff);

    Collection<? extends Image> findByStatusAndPendingDeleteAtBefore(ImageStatus imageStatus, LocalDateTime cutoff);

    Collection<? extends Image> findByStatus(ImageStatus imageStatus);
    List<Image> findByCategoryAndReferenceIdAndStatus(ResourceCategory category, Long referenceId, ImageStatus status);




}

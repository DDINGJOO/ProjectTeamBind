package studio.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.entity.Image;
import studio.entity.Studio;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository <Image,Long> {
    List<Image> findAllByStudio(Studio studio);
}

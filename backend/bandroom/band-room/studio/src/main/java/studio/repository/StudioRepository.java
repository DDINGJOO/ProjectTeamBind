package studio.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import studio.entity.Studio;

import java.util.List;

@Repository
public interface StudioRepository extends JpaRepository<Studio,Long> {


    void deleteAllByBandRoomId(Long bandRoomId);

    List<Studio> findAllByBandRoomId(Long bandRoomId);
}

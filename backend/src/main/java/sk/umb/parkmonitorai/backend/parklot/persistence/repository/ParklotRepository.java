package sk.umb.parkmonitorai.backend.parklot.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.umb.parkmonitorai.backend.camera.persistence.entity.CameraEntity;
import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;

import java.util.List;
@Repository
public interface ParklotRepository extends CrudRepository<ParklotEntity, Long> {
    @Override
    List<ParklotEntity> findAll();

    @Transactional
    @Modifying
    @Query("DELETE FROM ParklotEntity WHERE camera=:camera_id")
    void deleteAllByCameraId(@Param("camera_id") CameraEntity camera_id);
    @Query("SELECT t FROM ParklotEntity t WHERE t.camera=:camera_id")
    List<ParklotEntity> findParklotsByCameraId(@Param("camera_id") CameraEntity camera_id);
}

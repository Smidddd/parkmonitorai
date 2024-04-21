package sk.umb.parkmonitorai.backend.occupancy.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.umb.parkmonitorai.backend.camera.persistence.entity.CameraEntity;
import sk.umb.parkmonitorai.backend.occupancy.persistence.entity.OccupancyCacheEntity;
import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;

import java.util.List;
@Repository
public interface OccupancyRepository extends CrudRepository<OccupancyCacheEntity, Long> {
    @Override
    List<OccupancyCacheEntity> findAll();

    @Query("SELECT t FROM OccupancyCacheEntity t WHERE t.camera_id=:camera_id")
    List<OccupancyCacheEntity> findOCEByCameraId(@Param("camera_id") Long camera_id);
}

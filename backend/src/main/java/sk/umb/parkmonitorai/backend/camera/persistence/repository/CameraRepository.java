package sk.umb.parkmonitorai.backend.camera.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sk.umb.parkmonitorai.backend.camera.persistence.entity.CameraEntity;

import java.util.List;

@Repository
public interface CameraRepository extends CrudRepository<CameraEntity, Long> {
    @Override
    List<CameraEntity> findAll();
}

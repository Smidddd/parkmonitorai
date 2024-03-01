package sk.umb.parkmonitorai.backend.parklot.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;

import java.util.List;
@Repository
public interface ParklotRepository extends CrudRepository<ParklotEntity, Long> {
    @Override
    List<ParklotEntity> findAll();
}

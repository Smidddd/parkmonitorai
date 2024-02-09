package sk.umb.parkmonitorai.backend.area.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sk.umb.parkmonitorai.backend.area.persistence.entity.AreaEntity;


import java.util.List;

@Repository
public interface AreaRepository extends CrudRepository<AreaEntity, Long> {
    @Override
    List<AreaEntity> findAll();
}
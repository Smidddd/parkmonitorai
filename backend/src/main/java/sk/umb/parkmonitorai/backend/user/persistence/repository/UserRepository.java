package sk.umb.parkmonitorai.backend.user.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.umb.parkmonitorai.backend.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    @Override
    List<UserEntity> findAll();
    @Query("SELECT t FROM UserEntity t WHERE t.email=:email")
    public UserEntity findByEmail(@Param("email") String email);
}

package sk.umb.parkmonitorai.backend.area.persistence.entity;

import jakarta.persistence.*;
import sk.umb.parkmonitorai.backend.user.persistence.entity.UserEntity;

import java.util.List;
@Entity
public class AreaEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(name = "access",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "area_id"))
    private List<UserEntity> userEntities;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }
}

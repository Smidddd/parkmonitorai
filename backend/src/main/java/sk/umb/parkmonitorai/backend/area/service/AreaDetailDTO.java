package sk.umb.parkmonitorai.backend.area.service;

import sk.umb.parkmonitorai.backend.user.persistence.entity.UserEntity;

import java.util.List;

public class AreaDetailDTO {
    private Long id;
    private String name;
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

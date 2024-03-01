package sk.umb.parkmonitorai.backend.camera.persistence.entity;

import jakarta.persistence.*;
import sk.umb.parkmonitorai.backend.area.persistence.entity.AreaEntity;
@Entity
public class CameraEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Float lattitude;
    private Float longitude;
    private String source;
    @ManyToOne
    @JoinColumn(name = "area_id")
    private AreaEntity areaId;
    private Long status;

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

    public Float getLattitude() {
        return lattitude;
    }

    public void setLattitude(Float lattitude) {
        this.lattitude = lattitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public AreaEntity getAreaId() {
        return areaId;
    }

    public void setAreaId(AreaEntity areaId) {
        this.areaId = areaId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}

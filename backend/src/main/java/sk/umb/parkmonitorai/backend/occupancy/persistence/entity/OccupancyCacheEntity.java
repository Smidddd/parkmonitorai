package sk.umb.parkmonitorai.backend.occupancy.persistence.entity;

import jakarta.persistence.*;
import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;

import java.sql.Timestamp;

@Entity
public class OccupancyCacheEntity {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn
    private ParklotEntity parklot;
    private boolean occupancy;
    private Long camera_id;
    private Timestamp saved_at = new Timestamp(System.currentTimeMillis());

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParklotEntity getParklot() {
        return parklot;
    }

    public void setParklot(ParklotEntity parklot) {
        this.parklot = parklot;
    }

    public boolean isOccupancy() {
        return occupancy;
    }

    public void setOccupancy(boolean occupancy) {
        this.occupancy = occupancy;
    }

    public Long getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(Long camera_id) {
        this.camera_id = camera_id;
    }

    public Timestamp getSaved_at() {
        return saved_at;
    }

    public void setSaved_at(Timestamp saved_at) {
        this.saved_at = saved_at;
    }
}

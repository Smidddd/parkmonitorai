package sk.umb.parkmonitorai.backend.parklot.service;

import sk.umb.parkmonitorai.backend.camera.persistence.entity.CameraEntity;

import java.awt.Point;
import java.util.List;


public class ParklotDetailDTO {
    private Long id;
    private List<Point> geometry;
    private Float latitude;
    private Float longitude;
    private CameraEntity camera;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Point> getGeometry() {
        return geometry;
    }

    public void setGeometry(List<Point> geometry) {
        this.geometry = geometry;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public CameraEntity getCamera() {
        return camera;
    }

    public void setCamera(CameraEntity camera) {
        this.camera = camera;
    }
}
package sk.umb.parkmonitorai.backend.occupancy.service;

import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;
import java.sql.Timestamp;

public class OccupancyCache {
    private ParklotEntity parklot;
    private boolean occupancy;

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
}

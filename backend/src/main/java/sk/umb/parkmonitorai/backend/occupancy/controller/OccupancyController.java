package sk.umb.parkmonitorai.backend.occupancy.controller;

import org.springframework.web.bind.annotation.*;
import sk.umb.parkmonitorai.backend.occupancy.service.OccupancyCache;
import sk.umb.parkmonitorai.backend.occupancy.service.OccupancyService;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotDetailDTO;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotRequestDTO;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotService;

import java.io.IOException;
import java.util.List;
@RestController
public class OccupancyController {
    private OccupancyService occupancyService;

    public OccupancyController(OccupancyService occupancyService){
        this.occupancyService = occupancyService;
    }

    @GetMapping("/api/occupancy/{cameraId}")
    public List<OccupancyCache> getAllParklots(@PathVariable Long cameraId) throws IOException {
        System.out.println("Get current occupancy called.");
        return occupancyService.getCurrentOccupancy(cameraId);
    }
}


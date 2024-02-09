package sk.umb.parkmonitorai.backend.area.controller;

import org.springframework.web.bind.annotation.*;
import sk.umb.parkmonitorai.backend.area.service.AreaDetailDTO;
import sk.umb.parkmonitorai.backend.area.service.AreaRequestDTO;
import sk.umb.parkmonitorai.backend.area.service.AreaService;

import java.util.List;
@RestController
public class AreaController {
    private AreaService areaService;

    public AreaController(AreaService areaService){
        this.areaService = areaService;
    }

    @GetMapping("/api/areas")
    public List<AreaDetailDTO> getAllAreas(){
        System.out.println("Get all areas called.");
        return areaService.getAllAreas();
    }
    @GetMapping("/api/areas/{areaId}")
    public AreaDetailDTO getAreaById(@PathVariable Long areaId){
        System.out.println("Get area by ID called.");
        return areaService.getAreaById(areaId);
    }
    @PostMapping("/api/areas")
    public Long createUser(@RequestBody AreaRequestDTO areaRequestDTO){
        System.out.println("Create area called.");
        return areaService.createArea(areaRequestDTO);
    }
    @PutMapping("/api/areas/{areaId}")
    public void updateUser(@PathVariable Long areaId, @RequestBody AreaRequestDTO areaRequestDTO){
        System.out.println("Update area called, userId: "+ areaId);
        areaService.updateArea(areaId, areaRequestDTO);
    }
    @DeleteMapping("/api/areas/{areaId}")
    public void deleteArea(@PathVariable Long areaId){
        areaService.deleteArea(areaId);
    }
}

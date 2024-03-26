package sk.umb.parkmonitorai.backend.parklot.controller;

import org.springframework.web.bind.annotation.*;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotDetailDTO;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotRequestDTO;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotService;

import java.util.List;
@RestController
public class ParklotController {
    private ParklotService parklotService;

    public ParklotController(ParklotService parklotService){
        this.parklotService = parklotService;
    }

    @GetMapping("/api/parklots")
    public List<ParklotDetailDTO> getAllParklots(){
        System.out.println("Get all parklots called.");
        return parklotService.getAllParklots();
    }
    @GetMapping("/api/parklots/{parklotId}")
    public ParklotDetailDTO getParklotById(@PathVariable Long parklotId){
        System.out.println("Get parklot by ID called.");
        return parklotService.getParklotById(parklotId);
    }
    @PostMapping("/api/parklots")
    public int createParklots(@RequestBody ParklotRequestDTO[] parklotRequestDTOS){
        System.out.println("Create parklot called.");
        return parklotService.createParklots(parklotRequestDTOS);
    }
    @PutMapping("/api/parklots/{parklotId}")
    public void updateParklot(@PathVariable Long parklotId, @RequestBody ParklotRequestDTO parklotRequestDTO){
        System.out.println("Update camera called, cameraId: "+ parklotId);
        parklotService.updateParklot(parklotId, parklotRequestDTO);
    }
    @DeleteMapping("/api/parklots/{parklotId}")
    public void deleteParklot(@PathVariable Long parklotId){
        parklotService.deleteParklot(parklotId);
    }
}

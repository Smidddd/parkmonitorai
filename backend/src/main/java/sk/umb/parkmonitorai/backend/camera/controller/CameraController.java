package sk.umb.parkmonitorai.backend.camera.controller;

import org.springframework.web.bind.annotation.*;
import sk.umb.parkmonitorai.backend.camera.service.CameraDetailDTO;
import sk.umb.parkmonitorai.backend.camera.service.CameraRequestDTO;
import sk.umb.parkmonitorai.backend.camera.service.CameraService;

import java.util.List;
@RestController
public class CameraController {
    private CameraService cameraService;

    public CameraController(CameraService cameraService){
        this.cameraService = cameraService;
    }

    @GetMapping("/api/cameras")
    public List<CameraDetailDTO> getAllCameras(){
        System.out.println("Get all cameras called.");
        return cameraService.getAllCameras();
    }
    @GetMapping("/api/cameras/{cameraId}")
    public CameraDetailDTO getCameraById(@PathVariable Long cameraId){
        System.out.println("Get camera by ID called.");
        return cameraService.getCameraById(cameraId);
    }
    @PostMapping("/api/cameras")
    public Long createCamera(@RequestBody CameraRequestDTO cameraRequestDTO){
        System.out.println("Create camera called.");
        return cameraService.createCamera(cameraRequestDTO);
    }
    @PutMapping("/api/cameras/{cameraId}")
    public void updateCamera(@PathVariable Long cameraId, @RequestBody CameraRequestDTO cameraRequestDTO){
        System.out.println("Update camera called, cameraId: "+ cameraId);
        cameraService.updateCamera(cameraId, cameraRequestDTO);
    }
    @DeleteMapping("/api/cameras/{cameraId}")
    public void deleteCamera(@PathVariable Long cameraId){
        System.out.println("Delete camera: "+cameraId+" called.");
        cameraService.deleteCamera(cameraId);
    }
}

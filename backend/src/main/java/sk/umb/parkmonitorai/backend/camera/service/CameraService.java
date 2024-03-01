package sk.umb.parkmonitorai.backend.camera.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import sk.umb.parkmonitorai.backend.camera.persistence.entity.CameraEntity;
import sk.umb.parkmonitorai.backend.camera.persistence.repository.CameraRepository;

import java.util.ArrayList;
import java.util.List;
@Service
public class CameraService {
    private final CameraRepository cameraRepository;

    public CameraService(CameraRepository cameraRepository){
        this.cameraRepository = cameraRepository;
    }

    private List<CameraDetailDTO> mapToDto(List<CameraEntity> cameraEntities) {
        List<CameraDetailDTO> dtos = new ArrayList<>();

        for (CameraEntity ce : cameraEntities) {
            CameraDetailDTO dto = new CameraDetailDTO();

            dto.setId(ce.getId());
            dto.setAreaId(ce.getAreaId());
            dto.setLattitude(ce.getLattitude());
            dto.setLongitude(ce.getLongitude());
            dto.setName(ce.getName());
            dto.setSource(ce.getSource());
            dto.setStatus(ce.getStatus());

            dtos.add(dto);
        }

        return dtos;
    }

    private CameraDetailDTO mapToDto(CameraEntity cameraEntity) {
        CameraDetailDTO dto = new CameraDetailDTO();

        dto.setId(cameraEntity.getId());
        dto.setAreaId(cameraEntity.getAreaId());
        dto.setLattitude(cameraEntity.getLattitude());
        dto.setLongitude(cameraEntity.getLongitude());
        dto.setName(cameraEntity.getName());
        dto.setSource(cameraEntity.getSource());
        dto.setStatus(cameraEntity.getStatus());

        return dto;
    }
    private CameraEntity mapToEntity(CameraRequestDTO dto) {
        CameraEntity ce = new CameraEntity();

        ce.setAreaId(dto.getAreaId());
        ce.setLattitude(dto.getLattitude());
        ce.setLongitude(dto.getLongitude());
        ce.setName(dto.getName());
        ce.setSource(dto.getSource());
        ce.setStatus(dto.getStatus());

        return ce;
    }

    public List<CameraDetailDTO> getAllCameras(){
        return mapToDto(cameraRepository.findAll());
    }

    public CameraDetailDTO getCameraById(Long cameraId){
        return mapToDto(cameraRepository.findById(cameraId).get());
    }

    public Long createCamera(CameraRequestDTO cameraRequestDTO){
        return cameraRepository.save(mapToEntity(cameraRequestDTO)).getId();
    }

    public void updateCamera(Long cameraId, CameraRequestDTO cameraRequestDTO) {
        CameraEntity cameraEntity = cameraRepository.findById(cameraId).get();

        if (! Strings.isEmpty(cameraRequestDTO.getName())) {
            cameraEntity.setName(cameraRequestDTO.getName());
        }
        if (cameraRequestDTO.getLattitude() != null) {
            cameraEntity.setLattitude(cameraRequestDTO.getLattitude());
        }
        if (cameraRequestDTO.getLongitude() != null) {
            cameraEntity.setLongitude(cameraRequestDTO.getLongitude());
        }
        if (cameraRequestDTO.getAreaId() != null) {
            cameraEntity.setAreaId(cameraRequestDTO.getAreaId());
        }
        if (! Strings.isEmpty(cameraRequestDTO.getSource())) {
            cameraEntity.setSource(cameraRequestDTO.getSource());
        }
        if (cameraRequestDTO.getStatus() != null) {
            cameraEntity.setAreaId(cameraRequestDTO.getAreaId());
        }

        cameraRepository.save(cameraEntity);
    }

    public void deleteCamera(Long cameraId){
        cameraRepository.deleteById(cameraId);
    }
}

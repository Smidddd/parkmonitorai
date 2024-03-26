package sk.umb.parkmonitorai.backend.parklot.service;

import org.springframework.stereotype.Service;
import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;
import sk.umb.parkmonitorai.backend.parklot.persistence.repository.ParklotRepository;

import java.util.ArrayList;
import java.util.List;
@Service
public class ParklotService {
    private final ParklotRepository parklotRepository;

    public ParklotService(ParklotRepository parklotRepository){
        this.parklotRepository = parklotRepository;
    }

    private List<ParklotDetailDTO> mapToDto(List<ParklotEntity> parklotEntities) {
        List<ParklotDetailDTO> dtos = new ArrayList<>();

        for (ParklotEntity pe : parklotEntities) {
            ParklotDetailDTO dto = new ParklotDetailDTO();

            dto.setId(pe.getId());
            dto.setGeometry(pe.getGeometry());
            dto.setLatitude(pe.getLatitude());
            dto.setLongitude(pe.getLongitude());
            dto.setCamera(pe.getCamera());

            dtos.add(dto);
        }

        return dtos;
    }

    private ParklotDetailDTO mapToDto(ParklotEntity parklotEntity) {
        ParklotDetailDTO dto = new ParklotDetailDTO();

        dto.setId(parklotEntity.getId());
        dto.setCamera(parklotEntity.getCamera());
        dto.setGeometry(parklotEntity.getGeometry());
        dto.setLongitude(parklotEntity.getLongitude());
        dto.setLatitude(parklotEntity.getLatitude());

        return dto;
    }
    private ParklotEntity mapToEntity(ParklotRequestDTO dto) {
        ParklotEntity pe = new ParklotEntity();

        pe.setCamera(dto.getCamera());
        pe.setLatitude(dto.getLatitude());
        pe.setLongitude(dto.getLongitude());
        pe.setGeometry(dto.getGeometry());

        return pe;
    }

    public List<ParklotDetailDTO> getAllParklots(){
        return mapToDto(parklotRepository.findAll());
    }

    public ParklotDetailDTO getParklotById(Long cameraId){
        return mapToDto(parklotRepository.findById(cameraId).get());
    }

    public Long createParklot(ParklotRequestDTO parklotRequestDTO){
        return parklotRepository.save(mapToEntity(parklotRequestDTO)).getId();
    }
    public int createParklots(ParklotRequestDTO[] parklotRequestDTOS){
        for (int i=0; i<parklotRequestDTOS.length; i++){
            parklotRepository.save(mapToEntity(parklotRequestDTOS[i]));
        }
        return parklotRequestDTOS.length;
    }

    public void updateParklot(Long parklotId, ParklotRequestDTO parklotRequestDTO) {
        ParklotEntity parklotEntity = parklotRepository.findById(parklotId).get();

        if (parklotRequestDTO.getGeometry().size() != 0) {
            parklotEntity.setGeometry(parklotRequestDTO.getGeometry());
        }
        if (parklotRequestDTO.getLatitude() != null) {
            parklotEntity.setLatitude(parklotRequestDTO.getLatitude());
        }
        if (parklotRequestDTO.getLongitude() != null) {
            parklotEntity.setLongitude(parklotRequestDTO.getLongitude());
        }
        if (parklotRequestDTO.getCamera() != null) {
            parklotEntity.setCamera(parklotRequestDTO.getCamera());
        }

        parklotRepository.save(parklotEntity);
    }

    public void deleteParklot(Long parklotId){
        parklotRepository.deleteById(parklotId);
    }
}

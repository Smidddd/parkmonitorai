package sk.umb.parkmonitorai.backend.area.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import sk.umb.parkmonitorai.backend.area.persistence.entity.AreaEntity;
import sk.umb.parkmonitorai.backend.area.persistence.repository.AreaRepository;

import java.util.ArrayList;
import java.util.List;
@Service
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    private List<AreaDetailDTO> mapToDto(List<AreaEntity> areaEntities) {
        List<AreaDetailDTO> dtos = new ArrayList<>();

        for (AreaEntity ue : areaEntities) {
            AreaDetailDTO dto = new AreaDetailDTO();

            dto.setId(ue.getId());
            dto.setName(ue.getName());
            dto.setUserEntities(ue.getUserEntities());

            dtos.add(dto);
        }

        return dtos;
    }

    private AreaDetailDTO mapToDto(AreaEntity areaEntity) {
        AreaDetailDTO dto = new AreaDetailDTO();

        dto.setId(areaEntity.getId());
        dto.setName(areaEntity.getName());
        dto.setUserEntities(areaEntity.getUserEntities());


        return dto;
    }
    private AreaEntity mapToEntity(AreaRequestDTO dto) {
        AreaEntity ae = new AreaEntity();

        ae.setName(dto.getName());
        ae.setUserEntities(dto.getUserEntities());

        return ae;
    }

    public List<AreaDetailDTO> getAllAreas(){
        return mapToDto(areaRepository.findAll());
    }

    public AreaDetailDTO getAreaById(Long areaId){
        return mapToDto(areaRepository.findById(areaId).get());
    }

    public Long createArea(AreaRequestDTO areaRequestDTO){
        return areaRepository.save(mapToEntity(areaRequestDTO)).getId();
    }

    public void updateArea(Long areaId, AreaRequestDTO areaRequestDTO) {
        AreaEntity areaEntity = areaRepository.findById(areaId).get();

        if (! Strings.isEmpty(areaRequestDTO.getName())) {
            areaEntity.setName(areaRequestDTO.getName());
        }

        if (areaRequestDTO.getUserEntities().size() > 0) {
            areaEntity.setUserEntities(areaRequestDTO.getUserEntities());
        }

        areaRepository.save(areaEntity);
    }

    public void deleteArea(Long areaId){
        areaRepository.deleteById(areaId);
    }
}

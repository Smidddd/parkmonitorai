package sk.umb.parkmonitorai.backend.user.service;

import org.apache.logging.log4j.util.Strings;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import sk.umb.parkmonitorai.backend.user.persistence.entity.UserEntity;
import sk.umb.parkmonitorai.backend.user.persistence.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final int logRounds = 10;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private List<UserDetailDTO> mapToDto(List<UserEntity> customerEntities) {
        List<UserDetailDTO> dtos = new ArrayList<>();

        for (UserEntity ue : customerEntities) {
            UserDetailDTO dto = new UserDetailDTO();

            dto.setId(ue.getId());
            dto.setEmail(ue.getEmail());
            dto.setPassword(ue.getPassword());
            dto.setRole(ue.getRole());

            dtos.add(dto);
        }

        return dtos;
    }

    private UserDetailDTO mapToDto(UserEntity userEntity) {
        UserDetailDTO dto = new UserDetailDTO();

        dto.setId(userEntity.getId());
        dto.setEmail(userEntity.getEmail());
        dto.setPassword(userEntity.getPassword());
        dto.setRole(userEntity.getRole());

        return dto;
    }
    private UserEntity mapToEntity(UserRequestDTO dto) {
        UserEntity ue = new UserEntity();

        ue.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(logRounds)));
        ue.setEmail(dto.getEmail());
        ue.setRole(dto.getRole());

        return ue;
    }
    public boolean verifyHash(String password, String email) {
        try {
            String hash = userRepository.findByEmail(email).getPassword();
            System.out.println(BCrypt.checkpw(password, hash));
            return BCrypt.checkpw(password, hash);
        }
        catch (Exception e){
            System.out.println("Email does not exist");
            return false;
        }
    }

    public boolean checkEmail(String customerEmail) {
        if(userRepository.findByEmail(customerEmail)==null){
            return true;
        } else {
            return false;
        }
    }

    public List<UserDetailDTO> getAllUsers(){
        return mapToDto(userRepository.findAll());
    }

    public UserDetailDTO getUserById(Long userId){
        return mapToDto(userRepository.findById(userId).get());
    }

    public UserDetailDTO getUserByEmail(String email){
        return mapToDto(userRepository.findByEmail(email));
    }

    public Long createUser(UserRequestDTO userRequestDTO){
        return userRepository.save(mapToEntity(userRequestDTO)).getId();
    }

    public void updateUser(Long userId, UserRequestDTO userRequestDTO) {
        UserEntity userEntity = userRepository.findById(userId).get();

        if (! Strings.isEmpty(userRequestDTO.getEmail())) {
            userEntity.setEmail(userRequestDTO.getEmail());
        }

        if (userRequestDTO.getRole() != null) {
            userEntity.setRole(userRequestDTO.getRole());
        }

        if (! Strings.isEmpty(userRequestDTO.getPassword())) {
            userEntity.setPassword(BCrypt.hashpw(userRequestDTO.getPassword(), BCrypt.gensalt(logRounds)) );
        }

        userRepository.save(userEntity);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }
}

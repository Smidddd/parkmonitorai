package sk.umb.parkmonitorai.backend.user.controller;

import org.springframework.web.bind.annotation.*;
import sk.umb.parkmonitorai.backend.user.service.UserDetailDTO;
import sk.umb.parkmonitorai.backend.user.service.UserRequestDTO;
import sk.umb.parkmonitorai.backend.user.service.UserService;
import sk.umb.parkmonitorai.backend.user.service.UserVerificationDTO;

import java.util.List;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public List<UserDetailDTO> getAllUsers(){
        System.out.println("Get all users called.");
        return userService.getAllUsers();
    }
    @GetMapping("/api/users/{userId}")
    public UserDetailDTO getUserById(@PathVariable Long userId){
        System.out.println("Get user by ID called.");
        return userService.getUserById(userId);
    }
    @GetMapping("/api/users/email/{userEmail}")
    public UserDetailDTO getUserByEmail(@PathVariable String userEmail){
        System.out.println("Get user by Email called.");
        return userService.getUserByEmail(userEmail);
    }
    //TODO: Also implement helper object in front-end
    @PostMapping("/api/users/verify")
    public boolean verifyPassword(@RequestBody UserVerificationDTO userVerificationDTO){
        System.out.println("Verify password called.");
        return userService.verifyHash(userVerificationDTO.getUserPassword(), userVerificationDTO.getUserEmail());
    }
    @PostMapping("/api/users")
    public Long createUser(@RequestBody UserRequestDTO userRequestDTO){
        System.out.println("Create user called.");
        return userService.createUser(userRequestDTO);
    }
    @PutMapping("/api/users/{userId}")
    public void updateUser(@PathVariable Long userId, @RequestBody UserRequestDTO userRequestDTO){
        System.out.println("Update user called, userId: "+ userId);
        userService.updateUser(userId, userRequestDTO);
    }
    @DeleteMapping("/api/users/{userId}")
    public void deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
    }
}

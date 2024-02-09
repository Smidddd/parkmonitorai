package sk.umb.parkmonitorai.backend.user.service;

public class UserVerificationDTO {
    private Long userId;
    private String userPassword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}

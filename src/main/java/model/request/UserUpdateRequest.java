package model.request;

import model.MultipartFile;

public class UserUpdateRequest {
    private String name;
    private String password;
    private String confirmedPassword;
    private String deleteProfileImage;
    private MultipartFile file;

    public UserUpdateRequest() {}

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public String getDeleteProfileImage() {
        return deleteProfileImage;
    }

    public MultipartFile getFile() {
        return file;
    }
}

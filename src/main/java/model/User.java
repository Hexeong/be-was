package model;

import java.util.Objects;

public class User {
    private String userId;
    private String profileImageUrl = "";
    private String password;
    private String name;
    // email 필드 삭제

    public User() {}

    // 생성자에서 email 파라미터 제거
    public User(String userId, String profileImageUrl, String password, String name) {
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.password = password;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getName() {
        return name;
    }

    // getEmail() 메서드 삭제

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

package com.example.gomind;

public class UserData {
    private String nickname;
    private String email;
    private int pears;

    // Геттеры и сеттеры
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPears() {
        return pears;
    }

    public void setPears(int pears) {
        this.pears = pears;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", pears=" + pears +
                '}';
    }
}

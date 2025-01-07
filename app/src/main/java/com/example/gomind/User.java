package com.example.gomind;

public class User {
    private String email;
    private String nickname;
    private int pears;
    private int count;

    public User(String email, String nickname, int pears, int count) {
        this.email = email;
        this.nickname = nickname;
        this.pears = pears;
        this.count = count;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPears() {
        return pears;
    }

    public void setPears(int pears) {
        this.pears = pears;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", pears=" + pears +
                ", count=" + count +
                '}';
    }
}

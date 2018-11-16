package com.lzh.config;

public class User {
    private String usernane;
    private String password;
    private String sex;

    public String getUsernane() {
        return usernane;
    }

    public void setUsernane(String usernane) {
        this.usernane = usernane;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + usernane + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}

package com.cqcst.entity;

public class Student {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getXuehao() {
        return xuehao;
    }

    public void setXuehao(Integer xuehao) {
        this.xuehao = xuehao;
    }

    public String getXname() {
        return xname;
    }

    public void setXname(String xname) {
        this.xname = xname;
    }

    public String getZhaohui() {
        return zhaohui;
    }

    public void setZhaohui(String zhaohui) {
        this.zhaohui = zhaohui;
    }

    private String username;
    private String password;
    private String nickname;
    private Integer xuehao;
    private String xname;
    private String zhaohui;
}

package com.badminton.model;

/**
 * 管理员实体类
 */
public class Admin {
    private String adminId; // 工号
    private String name;
    private String phone;
    private String password; // 简化处理，可拓展加密

    public Admin() {
    }

    public Admin(String adminId, String name, String phone, String password) {
        this.adminId = adminId;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}


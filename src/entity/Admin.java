package entity;

/**
 * 管理员实体类
 */
public class Admin {
    private String adminId;     // 工号
    private String name;        // 姓名
    private String phone;       // 联系电话

    public Admin() {
    }

    public Admin(String adminId, String name, String phone) {
        this.adminId = adminId;
        this.name = name;
        this.phone = phone;
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

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}


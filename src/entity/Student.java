package entity;

/**
 * 学生实体类
 */
public class Student {
    private String studentId;      // 学号
    private String name;            // 姓名
    private String phone;           // 联系电话

    public Student() {
    }

    public Student(String studentId, String name, String phone) {
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}


package model;

public class User {
    private String userName;
    private String fullName;
    private String password;
    private int role; // 1 = Manager, 0 = Member

    public User() {
    }

    public User(String userName, String fullName, String password, int role) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRoleString() {
        return role == 1 ? "Manager" : "Member";
    }
}

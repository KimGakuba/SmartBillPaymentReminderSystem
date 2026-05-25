package smartbill.server.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private int userId;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String createdAt;
    private String role;
    private List<Bill> bills;

    // Constructors
    public User() {}

    public User(String username, String email, String password,
                String phone, String createdAt) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.phone     = phone;
        this.createdAt = createdAt;
        this.role      = "USER";
    }

    public User(String username, String email, String password,
                String phone, String createdAt, String role) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.phone     = phone;
        this.createdAt = createdAt;
        this.role      = role;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Bill> getBills() { return bills; }
    public void setBills(List<Bill> bills) { this.bills = bills; }

    public boolean isAdmin() { return "ADMIN".equals(this.role); }
    
    private boolean isActive = true;

public boolean isActive() { return isActive; }
public void setActive(boolean isActive) { this.isActive = isActive; }

}
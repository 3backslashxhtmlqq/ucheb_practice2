package org.example.model;

public class User {

    private String login;
    private String password;
    private Role role;

    private boolean isBlocked = false;

    public User(
            String login,
            String password,
            Role role
    ) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isBlocked() { return isBlocked; }

    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }

    public void setRole(Role role) { this.role = role; }

}

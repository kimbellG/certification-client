package org.filippenkov.certification_client.models;

public class User {
    private Integer id;
    private String email;
    private Boolean is_admin;
    private String password;
    private String fio;


    public User() {

    }

    public User(String email, String fio, String password, boolean is_admin) {
        this.email = email;
        this.fio = fio;
        this.is_admin = is_admin;
        this.password = password;
    }

    public User(Integer id, String email, String fio, boolean is_admin) {
        this.id = id;
        this.email = email;
        this.fio = fio;
        this.is_admin = is_admin;
    }

    public User(AuthResponse user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.is_admin = user.isIs_admin();
        this.fio = user.getFio();
    }

    public User(UserAdapter adapter) {
        this.id = adapter.getId();
        this.email = adapter.getEmail();
        this.fio = adapter.getFio();
        is_admin = adapter.getStatus().equals("Админ");
    }

    public String getFio() {
        return fio;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

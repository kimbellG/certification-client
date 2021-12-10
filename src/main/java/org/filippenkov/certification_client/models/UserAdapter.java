package org.filippenkov.certification_client.models;

//TODO Try to refactor UserAdapter to User so there's no need in UserAdapter

public class UserAdapter {
    private final int id;
    private String Email;
    private String fio;
    private String status;


    public UserAdapter(User user) {
        if (user.getId() != null)
            this.id = user.getId();
        else
            this.id = 0;

        this.Email = user.getEmail();
        this.fio = user.getFio();
        if (user.getIs_admin()) {
            this.status = "Админ";
        } else {
            this.status = "Менеджер";
        }
    }

    public String getEmail() {
        return Email;
    }

    public String getFio() {
        return fio;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

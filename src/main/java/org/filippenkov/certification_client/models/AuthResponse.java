package org.filippenkov.certification_client.models;

public class AuthResponse {
    private int code;
    private final int id;
    private final String email;
    private final String fio;
    private final boolean is_admin;

    public AuthResponse(int id, String email, String fio, boolean is_admin) {
        this.id = id;
        this.email = email;
        this.fio = fio;
        this.is_admin = is_admin;
    }

    public String getEmail() {
        return email;
    }

    public String getFio() {
        return fio;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public int getId() {
        return id;
    }
}

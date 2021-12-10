package org.filippenkov.certification_client.models;

import java.util.Date;

public class Laboratory {
    private Long id;
    private String name;
    private String accreditation_number;
    private Date valid_until;

    public Laboratory(String name, String accreditation_number, Date valid_until) {
        this.name = name;
        this.accreditation_number = accreditation_number;
        this.valid_until = valid_until;
    }

    public Date getValid_until() {
        return valid_until;
    }

    public void setValid_until(Date valid_until) {
        this.valid_until = valid_until;
    }

    public String getAccreditation_number() {
        return accreditation_number;
    }

    public void setAccreditation_number(String accreditation_number) {
        this.accreditation_number = accreditation_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

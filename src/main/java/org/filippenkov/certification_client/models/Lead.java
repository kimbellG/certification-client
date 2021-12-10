package org.filippenkov.certification_client.models;

import java.util.Set;

public class Lead {
    private Long id;
    private String stage;
    private Long batch_number;
    private Long serial_number;
    private Detail detail;
    private Company company;
    private User responsible_user;
    private Set<Test> tests;

    public Long getId() {
        return id;
    }
    public Company getCompany() {
        return company;
    }
    public Detail getDetail() {
        return detail;
    }
    public Long getBatchNumber() {
        return batch_number;
    }
    public Long getSerial_number() {
        return serial_number;
    }
    public String getStage() {
        return stage;
    }
    public User getResponsible_user() {
        return responsible_user;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setBatchNumber(Long batchNumber) {
        this.batch_number = batchNumber;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public void setDetail(Detail detail) {
        this.detail = detail;
    }
    public void setResponsible_user(User responsible_user) {
        this.responsible_user = responsible_user;
    }
    public void setSerial_number(Long serial_number) {
        this.serial_number = serial_number;
    }
    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setTests(Set<Test> tests) {
        this.tests = tests;
    }

    public Set<Test> getTests() {
        return tests;
    }

}

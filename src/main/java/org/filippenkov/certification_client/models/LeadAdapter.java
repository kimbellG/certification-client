package org.filippenkov.certification_client.models;

public class LeadAdapter {
    private Long id;
    private String detail;
    private String company;
    private String stage;

    public LeadAdapter(Lead lead) {
        this.id = lead.getId();
        this.company = lead.getCompany().getName();
        this.detail = lead.getDetail().getName();
        this.stage = lead.getStage();
    }

    public String getStage() {
        return stage;
    }

    public String getDetail() {
        return detail;
    }

    public String getCompany() {
        return company;
    }

    public Long getId() {
        return id;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

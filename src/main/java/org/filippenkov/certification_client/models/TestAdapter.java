package org.filippenkov.certification_client.models;

public class TestAdapter {
    private Long id;
    private String number;
    private String laboratory;
    private String date;

    public TestAdapter(Test test) {
        id = test.getId();
        number = test.getNumber();
        laboratory = test.getLaboratory().getName();
        date = test.getTestDate().toLocaleString();


    }

    public Long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getLaboratory() {
        return laboratory;
    }

    public String getNumber() {
        return number;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

package org.filippenkov.certification_client.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Date;

public class Test extends Object {
    private Long id;
    private String number;
    private Laboratory laboratory;
    private Date test_date;

    public Test(String number, Laboratory laboratory, Date test_date) {
        this.number = number;
        this.laboratory = laboratory;
        this.test_date = test_date;
    }

    public Date getTestDate() {
        return test_date;
    }

    public void setTestDate(Date testDate) {
        this.test_date = testDate;
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}

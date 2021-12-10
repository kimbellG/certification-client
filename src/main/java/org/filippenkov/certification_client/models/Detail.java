package org.filippenkov.certification_client.models;

import java.util.Set;

public class Detail {

    private Long id;
    private String name;
    private Long code;

    public Detail(String name, Long code) {
        this.name = name;
        this.code = code;
    }

    private Set<Standard> standards;

    public Long getCode() {
        return code;
    }

    public Set<Standard> getStandards() {
        return standards;
    }

    public void setStandards(Set<Standard> standards) {
        this.standards = standards;
    }

    public void addStandard(Standard standard) {
        standards.add(standard);
    }

    public void removeStandard(Standard standard) {
        standards.remove(standard);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}

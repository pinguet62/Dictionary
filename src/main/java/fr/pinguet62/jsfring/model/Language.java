package fr.pinguet62.jsfring.model;

// Generated 16 août 2014 18:49:09 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Language generated by hbm2java
 */
@Entity
@Table(name = "language", catalog = "dictionary")
public class Language implements java.io.Serializable {

    private String code;
    private String name;
    private Set<Description> descriptions = new HashSet<Description>(0);

    public Language() {
    }

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Language(String code, String name, Set<Description> descriptions) {
        this.code = code;
        this.name = name;
        this.descriptions = descriptions;
    }

    @Id
    @Column(name = "CODE", unique = true, nullable = false, length = 2)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "NAME", nullable = false, length = 31)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "language")
    public Set<Description> getDescriptions() {
        return this.descriptions;
    }

    public void setDescriptions(Set<Description> descriptions) {
        this.descriptions = descriptions;
    }

}
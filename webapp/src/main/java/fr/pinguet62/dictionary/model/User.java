package fr.pinguet62.dictionary.model;

// Generated 16 ao�t 2014 18:49:09 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * User generated by hbm2java
 */
@Entity
@Table(name = "user", catalog = "dictionary")
public class User implements java.io.Serializable {

    private String login;
    private String password;
    private Set<Profile> profiles = new HashSet<Profile>(0);

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String password, Set<Profile> profiles) {
        this.login = login;
        this.password = password;
        this.profiles = profiles;
    }

    @Id
    @Column(name = "LOGIN", unique = true, nullable = false, length = 30)
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "PASSWORD", nullable = false, length = 30)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_profiles", catalog = "dictionary", joinColumns = { @JoinColumn(name = "USER", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "PROFILE", nullable = false, updatable = false) })
    public Set<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

}
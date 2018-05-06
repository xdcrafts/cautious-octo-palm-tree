package com.github.xdcrafts.octopalm.api.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.xdcrafts.octopalm.api.account.encryption.BCryptPasswordDeserializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = BCryptPasswordDeserializer.class)
    public String password;

    public String firstName;

    public String lastName;

    public String aboutMe;

    @CreatedDate
    public LocalDateTime createdAt;

    @LastModifiedDate
    public LocalDateTime updatedAt;

    @Version
    public Long version;

    public String ext;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(email, account.email) &&
                Objects.equals(password, account.password) &&
                Objects.equals(firstName, account.firstName) &&
                Objects.equals(lastName, account.lastName) &&
                Objects.equals(aboutMe, account.aboutMe) &&
                Objects.equals(createdAt, account.createdAt) &&
                Objects.equals(updatedAt, account.updatedAt) &&
                Objects.equals(version, account.version) &&
                Objects.equals(ext, account.ext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, aboutMe, createdAt, updatedAt, version, ext);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                ", ext='" + ext + '\'' +
                '}';
    }
}

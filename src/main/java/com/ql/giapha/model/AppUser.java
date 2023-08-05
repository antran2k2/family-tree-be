
package com.ql.giapha.model;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "app_user")
@Data
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appUserId;

    @Column(length = 30, nullable = false)
    private String username;

    private String fullname;

    @CreationTimestamp
    private Date joinAt;

    @Column(nullable = false)
    private String password;

    @Column(length = 1)
    private Boolean enabled;

    // @OneToOne
    // @JoinColumn(name = "employee_id")
    // @ToString.Exclude
    // private Employee employee;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Family> ownedFamilies;

    @ManyToMany(mappedBy = "listAppUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private List<Role> listRole;

}

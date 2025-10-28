package com.mineCryptos.model;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {


     @Id
     @Column(name = "ROLE_ID")
    private int roleId;

    @Column(name = "NAME")
    private String name;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
//
//    @Id
//    private Long roleId;
//    private String roleName;
//
//    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "role")
//    private Set<UserRole> userRoles=new HashSet<>();
//
//    public Set<UserRole> getUserRoles() {
//        return userRoles;
//    }
//
//    public void setUserRoles(Set<UserRole> userRoles) {
//        this.userRoles = userRoles;
//    }
//
//    public Role() {
//    }
//
//    public Role(Long roleId, String roleName) {
//        this.roleId = roleId;
//        this.roleName = roleName;
//    }
//
//    public Long getRoleId() {
//        return roleId;
//    }
//
//    public void setRoleId(Long roleId) {
//        this.roleId = roleId;
//    }
//
//    public String getRoleName() {
//        return roleName;
//    }
//
//    public void setRoleName(String roleName) {
//        this.roleName = roleName;
//    }
}

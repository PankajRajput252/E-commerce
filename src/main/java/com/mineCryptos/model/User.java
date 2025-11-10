package com.mineCryptos.model;

import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND IS_DELETED=0")
public class User  extends StandardFieldClass implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_PK_ID")
    private  int userPkId;

    @Column(name = "VERSION_ID", columnDefinition = "BINARY(16)")
    private UUID versionId;

    @Column(name = "NODE_ID")
    private String nodeId;

    @Column(name = "NAME")
    private  String name;

    @Column(name = "EMAIL")
    private  String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "REFERRAL_CODE")
    private String referralCode;

    @Column(name = "POSITION")
    private String position;

    @Column(name = "USER_STATUS")
    private String userStatus;

    @Column(name = "PARENT_NODE_ID")
    private String parentNodeId;

    @Column(name = "TRANSACTION_PASSWORD")
    private String transactionPassword;


    @Column(name = "DATE_OF_ACTIVATION")
    private LocalDateTime dateOfActivation;

    @Column(name = "IMAGE_ID")
    private String imageId;

    @Column(name = "PROFILE_IMAGE_URL",length = 1000)
    private String profileImageUrl; // <-- store S3 URL here



    @Transient
    private boolean isUserIsAdmin;



    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "users", referencedColumnName = "USER_PK_ID"),
            inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "ROLE_ID"))
    private Set<Role> roles = new HashSet<>();

//    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "user")
//    @JsonIgnore
//    private Set<UserRole> userRoles=new HashSet<>();
//
//    public Set<UserRole> getUserRoles() {
//        return userRoles;
//    }
//
//    public void setUserRoles(Set<UserRole> userRoles) {
//        this.userRoles = userRoles;
//    }


    public User() {
    }

    public int getUserPkId() {
        return userPkId;
    }

    public void setUserPkId(int userPkId) {
        this.userPkId = userPkId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setPassword(String password) {
        this.password = password;
    }



    public UUID getVersionId() {
        return versionId;
    }

    @PrePersist
    public void setVersionId() {
        this.versionId = UUID.randomUUID();
    }





    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority>authorities = this.roles.stream().map((role)->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return authorities;
    }


    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean getIsUserIsAdmin() {
        return isUserIsAdmin;
    }

    public void setIsUserIsAdmin(boolean isUserIsAdmin) {
        this.isUserIsAdmin = isUserIsAdmin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getDateOfActivation() {
        return dateOfActivation;
    }

    public String getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public void setDateOfActivation(LocalDateTime dateOfActivation) {
        this.dateOfActivation = dateOfActivation;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}

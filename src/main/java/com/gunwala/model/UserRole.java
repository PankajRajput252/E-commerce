//package com.exam.model;
//
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//import java.util.UUID;
//
//public class UserRole {
//
//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name = "USER_ROLE_ID",columnDefinition = "BINARY(16)")
//    private UUID userRoleId;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    private Role role;
//}

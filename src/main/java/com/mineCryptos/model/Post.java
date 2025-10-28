package com.mineCryptos.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URL;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Where(clause = "SAVE_STATE_CODE_FK_ID='SAVED' AND RECORD_STATE_CODE_FK_ID='CURRENT' AND  AND IS_DELETED=0")
public class Post extends StandardFieldClass{

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "POST_PK_ID")
//    private int postPkId;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "POST_PK_ID", columnDefinition = "BINARY(16)")
    private UUID postPkId;

    @Column(name = "VERSION_ID", columnDefinition = "BINARY(16)")
    private UUID versionId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT" ,columnDefinition = "VARCHAR(2000)")
    private String content;

    @Column(name = "IMAGE")
    private URL image;

    @Column(name = "USER_FK_ID")
    private int userFkId;

    @Column(name = "CATEGORY_FK_ID")
    private int categoryFkId;


//    public int getPostPkId() {
//        return postPkId;
//    }
//
//    public void setPostPkId(int postPkId) {
//        this.postPkId = postPkId;
//    }


    public UUID getPostPkId() {
        return postPkId;
    }

    public void setPostPkId(UUID postPkId) {
        this.postPkId = postPkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public int getUserFkId() {
        return userFkId;
    }

    public void setUserFkId(int userFkId) {
        this.userFkId = userFkId;
    }

    public int getCategoryFkId() {
        return categoryFkId;
    }

    public void setCategoryFkId(int categoryFkId) {
        this.categoryFkId = categoryFkId;
    }

    public UUID getVersionId() {
        return versionId;
    }

    @PrePersist
    public void setVersionId() {
        this.versionId = UUID.randomUUID();
    }

}

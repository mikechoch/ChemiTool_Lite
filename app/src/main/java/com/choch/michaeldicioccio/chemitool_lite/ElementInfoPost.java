package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 3/15/17.
 */

public class ElementInfoPost {

    //-----------------------------------------------------
    // ElementInfoPost Attributes
    //-----------------------------------------------------
    private Integer postNumber;
    private String postTitle;
    private String postSubTitle;
    private String postMass;
    private String postConfig;
    private String postType;
    private String postImage;
    private String postDescription;

    //-----------------------------------------------------
    // ElementInfoPost Constructor
    //-----------------------------------------------------
    public ElementInfoPost(Integer postNumber,
                           String postTitle,
                           String postSubTitle,
                           String postMass,
                           String postConfig,
                           String postType,
                           String postImage,
                           String postDescription) {
        this.postNumber = postNumber;
        this.postTitle = postTitle;
        this.postSubTitle = postSubTitle;
        this.postMass = postMass;
        this.postConfig = postConfig;
        this.postType = postType;
        this.postImage = postImage;
        this.postDescription = postDescription;
    }


    //-----------------------------------------------------
    // ElementInfoPost Accessors
    //-----------------------------------------------------
    public Integer getPostNumber() {
        return postNumber;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostSubTitle() {
        return postSubTitle;
    }

    public String getPostMass() {
        return postMass;
    }

    public String getPostConfig() {
        return postConfig;
    }

    public String getPostType() {
        return postType;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getPostDescription() {
        return postDescription;
    }


    //-----------------------------------------------------
    // ElementInfoPost Mutators
    //-----------------------------------------------------
    public void setPostNumber(Integer postNumber) {
        this.postNumber = postNumber;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public void setPostSubTitle(String postSubTitle) {
        this.postSubTitle = postSubTitle;
    }

    public void setPostMass(String postSubTitle) {
        this.postMass = postMass;
    }

    public void setPostConfig(String postConfig) {
        this.postConfig = postConfig;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }
}

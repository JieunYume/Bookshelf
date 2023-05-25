package org.techtown.bookshelf.user;

public class ProfileImageModel {
    private String imageURI;

    public ProfileImageModel(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}

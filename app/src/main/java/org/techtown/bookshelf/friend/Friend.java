package org.techtown.bookshelf.friend;

import android.net.Uri;

public class Friend {
    private String id;
    private Uri image;

    public Friend(String id, Uri image) {
        this.id = id;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}

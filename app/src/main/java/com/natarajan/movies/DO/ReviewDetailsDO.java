package com.natarajan.movies.DO;

/**
 * Created by Natarajan on 02/12/16
 */
@org.parceler.Parcel
public class ReviewDetailsDO {

    public String id;
    public String author;
    public String content;
    public String url;


    //Parceler will generate the No longer do you have to implement the Parcelable interface, the writeToParcel() or createFromParcel() or the public static final CREATOR.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}

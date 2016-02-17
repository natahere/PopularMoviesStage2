package com.natarajan.movies.DO;


/**
 * Created by Natarajan on 02/12/16
 */

@org.parceler.Parcel
public class TrailerDetailsDO {

    // Keep it as Public field

    public String source;
    public String name;

    //Parceler will generate the No longer do you have to implement the Parcelable interface, the writeToParcel() or createFromParcel() or the public static final CREATOR.

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

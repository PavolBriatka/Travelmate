package com.briatka.pavol.favouriteplaces.customobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favourite_places")
public class FavPlaceObject {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String coordinates;
    private byte[] photo;

    @Ignore
    public FavPlaceObject(String title, String coordinates, byte[] photo){
        this.title = title;
        this.coordinates = coordinates;
        this.photo = photo;
    }

    public FavPlaceObject(int id, String title, String coordinates, byte[] photo){
        this.id = id;
        this.title = title;
        this.coordinates = coordinates;
        this.photo = photo;
    }

    public int getId() {return id;}
    public void setId (int id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title){this.title = title;}

    public String getCoordinates() {return coordinates;}
    public void setCoordinates(String coordinates){this.coordinates = coordinates;}

    public byte[] getPhoto() {return photo;}
    public void setPhoto(byte[] photo) {this.photo = photo;}

}

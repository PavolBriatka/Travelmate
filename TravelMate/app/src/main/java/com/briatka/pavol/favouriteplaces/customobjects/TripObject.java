package com.briatka.pavol.favouriteplaces.customobjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trips")
public class TripObject {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    @ColumnInfo(name = "destination_data")
    private String destinationData;

    @Ignore
    public TripObject(String title, String destinationData){
        this.title = title;
        this.destinationData = destinationData;
    }

    public TripObject(int id, String title, String destinationData){
        this.id = id;
        this.title = title;
        this.destinationData = destinationData;
    }

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}

    public String getTitle() {return  title;}
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDestinationData() {return destinationData;}
    public void setDestinationData(String data) {
        this.destinationData = data;
    }
}

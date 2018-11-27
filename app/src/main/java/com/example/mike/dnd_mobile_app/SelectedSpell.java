package com.example.mike.dnd_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectedSpell implements Parcelable {
    private String name;
    private String description;

    public SelectedSpell(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public SelectedSpell(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);

        this.name = data[0];
        this.description = data[1];
    }
    public String toString(){
        return this.name;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.name, this.description
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<SelectedSpell>() {
        public SelectedSpell createFromParcel(Parcel in) {
            return new SelectedSpell(in);
        }

        public SelectedSpell[] newArray(int size) {
            return new SelectedSpell[size];
        }
    };
}

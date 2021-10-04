package com.prokonst.thingshouse.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.prokonst.thingshouse.BR;


@Entity(tableName = "things")
public class Thing extends BaseObservable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;
    private String unit;
    private String barCode;
    private String name;

    @Ignore
    public Thing() {
    }

    public Thing(String id, String unit, String barCode, String name) {
        this.id = id;
        this.unit = unit;
        this.barCode = barCode;
        this.name = name;
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        notifyPropertyChanged(BR.unit);
    }

    @Bindable
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
        notifyPropertyChanged(BR.barCode);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
}

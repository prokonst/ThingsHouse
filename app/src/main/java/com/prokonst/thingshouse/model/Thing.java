package com.prokonst.thingshouse.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.prokonst.thingshouse.BR;
import com.prokonst.thingshouse.Utils;


@Entity(tableName = "things")
public class Thing extends BaseObservable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;
    private String unit;
    private String barCode;
    private String name;
    private String mainPhotoId;

    @Ignore
    private String mainPhotoSrc;

    @Ignore
    public Thing() {
    }

    public Thing(String id, String unit, String barCode, String name, String mainPhotoId) {
        this.id = id;
        this.unit = unit;
        this.barCode = barCode;
        this.name = name;
        this.mainPhotoId = mainPhotoId;
        this.mainPhotoSrc = Utils.getImagePreviewPath(mainPhotoId);
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

    @Bindable
    public String getMainPhotoId() {
        return mainPhotoId;
    }

    public void setMainPhotoId(String mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
        notifyPropertyChanged(BR.mainPhotoId);

        setMainPhotoSrc(Utils.getImagePreviewPath(this.mainPhotoId));
    }

    @Bindable
    public String getMainPhotoSrc() {
        return mainPhotoSrc;
    }
    public void setMainPhotoSrc(String mainPhotoSrc) {
        this.mainPhotoSrc = mainPhotoSrc;
        notifyPropertyChanged(BR.mainPhotoSrc);
    }
}

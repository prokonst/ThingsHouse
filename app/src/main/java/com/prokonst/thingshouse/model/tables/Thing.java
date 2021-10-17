package com.prokonst.thingshouse.model.tables;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


import com.prokonst.thingshouse.tools.ShowThingsListParameters;
import com.prokonst.thingshouse.tools.Utils;

import java.io.Serializable;


@Entity(tableName = "things", indices = {@Index(value = {"barCode"}, unique = true)})
public class Thing extends BaseObservable implements Serializable, ShowThingsListParameters.ThingIdInterface {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "thing_id")
    private String thingId;
    private String unit;
    private String barCode;
    private String name;
    private String mainPhotoId;

    @Ignore
    private String mainPhotoSrc;

    @Ignore
    public Thing() {
    }

    public Thing(String thingId, String unit, String barCode, String name, String mainPhotoId) {
        this.thingId = thingId;
        this.unit = unit;
        this.barCode = barCode;
        this.name = name;
        this.mainPhotoId = mainPhotoId;
        this.mainPhotoSrc = Utils.getImagePreviewPath(mainPhotoId);
    }

    private Thing(@NonNull String thingId) {
        this.thingId = thingId;
    }

    public static Thing createPlugThing(String thingId) {
        return new Thing(thingId);
    }

    @Bindable
    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.thingId);
    }

    @Bindable
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.unit);
    }

    @Bindable
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.barCode);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.name);
    }

    @Bindable
    public String getMainPhotoId() {
        return mainPhotoId;
    }

    public void setMainPhotoId(String mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.mainPhotoId);

        setMainPhotoSrc(Utils.getImagePreviewPath(this.mainPhotoId));
    }

    @Bindable
    public String getMainPhotoSrc() {
        return mainPhotoSrc;
    }
    public void setMainPhotoSrc(String mainPhotoSrc) {
        this.mainPhotoSrc = mainPhotoSrc;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.mainPhotoSrc);
    }
}

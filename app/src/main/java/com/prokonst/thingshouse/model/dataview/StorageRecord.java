package com.prokonst.thingshouse.model.dataview;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;
import com.prokonst.thingshouse.tools.Utils;

import java.io.Serializable;

public class StorageRecord extends BaseObservable implements Serializable, ShowThingsListParameters.ThingIdInterface {
    @ColumnInfo(name = "storage_id")
    private String storageId;

    @ColumnInfo(name = "parent_id")
    private String parentId;

    @ColumnInfo(name = "child_id")
    private String childId;

    private double quantity;

    @ColumnInfo(name = "thing_id")
    private String thingId;
    private String unit;
    private String barCode;
    private String name;
    private String mainPhotoId;

    @Ignore
    private String mainPhotoPrevSrc;

    public StorageRecord(@NonNull String storageId, String parentId, String childId, double quantity,
                         String thingId, String unit, String barCode, String name, String mainPhotoId) {
        this.storageId = storageId;
        this.parentId = parentId;
        this.childId = childId;
        this.quantity = quantity;
        this.thingId = thingId;
        this.unit = unit;
        this.barCode = barCode;
        this.name = name;
        this.mainPhotoId = mainPhotoId;
        this.mainPhotoPrevSrc = Utils.getImagePreviewPath(mainPhotoId);
    }

    public Storage createStorage() {
        Storage storage = new Storage(this.storageId, this.parentId, this.childId, this.quantity);

        return storage;
    }

    public Thing createThing() {
        Thing thing = new Thing(thingId, unit, barCode, name, mainPhotoId);
        return thing;
    }

    @Bindable
    @NonNull
    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(@NonNull String storageId) {
        this.storageId = storageId;
        notifyPropertyChanged(BR.storageId);
    }

    @Bindable
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        notifyPropertyChanged(BR.parentId);
    }

    @Bindable
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
        notifyPropertyChanged(BR.childId);
    }

    @Bindable
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @Bindable
    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
        notifyPropertyChanged(BR.thingId);
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

        setMainPhotoPrevSrc(Utils.getImagePreviewPath(this.mainPhotoId));
    }

    @Bindable
    public String getMainPhotoPrevSrc() {
        return mainPhotoPrevSrc;
    }
    public void setMainPhotoPrevSrc(String mainPhotoPrevSrc) {
        this.mainPhotoPrevSrc = mainPhotoPrevSrc;
        notifyPropertyChanged(BR.mainPhotoPrevSrc);
    }
}

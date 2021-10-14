package com.prokonst.thingshouse.model.dataview;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.prokonst.thingshouse.Utils;

import java.io.Serializable;

public class StorageItem extends BaseObservable implements Serializable {
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
    private String mainPhotoSrc;

    public StorageItem(@NonNull String storageId, String parentId, String childId, double quantity,
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
        this.mainPhotoSrc = Utils.getImagePreviewPath(mainPhotoId);
    }

    @Bindable
    @NonNull
    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(@NonNull String storageId) {
        this.storageId = storageId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.storageId);
    }

    @Bindable
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.parentId);
    }

    @Bindable
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.childId);
    }

    @Bindable
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.quantity);
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

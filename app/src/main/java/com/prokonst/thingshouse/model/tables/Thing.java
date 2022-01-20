package com.prokonst.thingshouse.model.tables;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.database.Exclude;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;
import com.prokonst.thingshouse.tools.Utils;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;


@Entity(tableName = "things", indices = {@Index(value = {"barCode"}, unique = true)})
public class Thing extends BaseObservable implements Serializable, Synced, ShowThingsListParameters.ThingIdInterface {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "thing_id")
    private String id;

    private String unit;

    private String barCode;

    private String name;

    private String mainPhotoId;

    @TypeConverters({com.prokonst.thingshouse.tools.DateTimeConverter.class})
    private Date dateChange;

    public String dataHash;

    public boolean isDeleted;

    public String userId;

    @Exclude
    @Ignore
    private String mainPhotoBaseSrc;

    @Exclude
    @Ignore
    private String mainPhotoPrevSrc;


    @Ignore
    public Thing() {
    }

    public Thing(String id, String unit, String barCode, String name, String mainPhotoId) {
        this.id = id;
        this.unit = unit;
        this.barCode = barCode;
        this.name = name;
        this.mainPhotoId = mainPhotoId;
        this.mainPhotoBaseSrc = Utils.getImageBasePath(mainPhotoId);
        this.mainPhotoPrevSrc = Utils.getImagePreviewPath(mainPhotoId);
    }

    private Thing(@NonNull String id) {
        this.id = id;
    }


    @Bindable
    public String getId() {
        return id.toLowerCase();
    }

    public void setId(String id) {
        this.id = id.toLowerCase();
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.id);
    }

    @Exclude
    public String getThingId() {
        return id.toLowerCase();
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

        setMainPhotoBaseSrc(Utils.getImageBasePath(this.mainPhotoId));
        setMainPhotoPrevSrc(Utils.getImagePreviewPath(this.mainPhotoId));
    }

    @Exclude
    @Bindable
    public String getMainPhotoBaseSrc() {
        return mainPhotoBaseSrc;
    }
    @Exclude
    public void setMainPhotoBaseSrc(String mainPhotoBaseSrc) {
        this.mainPhotoBaseSrc = mainPhotoBaseSrc;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.mainPhotoBaseSrc);
    }

    @Exclude
    @Bindable
    public String getMainPhotoPrevSrc() {
        return mainPhotoPrevSrc;
    }
    @Exclude
    public void setMainPhotoPrevSrc(String mainPhotoPrevSrc) {
        this.mainPhotoPrevSrc = mainPhotoPrevSrc;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.mainPhotoPrevSrc);
    }

    public Date getDateChange() {
        return dateChange;
    }

    public void setDateChange(Date dateChange){
        this.dateChange = dateChange;
    }


    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash){
        this.dataHash = dataHash;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Exclude
    public String getDataForHash(){

        StringBuilder dataForHash = new StringBuilder();

        dataForHash.append("Unit: ");
        dataForHash.append(this.unit);

        dataForHash.append("BarCode: ");
        dataForHash.append(this.barCode);

        dataForHash.append("Name: ");
        dataForHash.append(this.name);

        dataForHash.append("MainPhotoId: ");
        dataForHash.append(this.mainPhotoId);

        dataForHash.append("IsDeleted: ");
        dataForHash.append(Boolean.toString(this.isDeleted).toLowerCase());

        return dataForHash.toString();
    }

}

package com.prokonst.thingshouse.tools;

import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.dataview.StorageRecord;

import java.util.List;

public interface ItemsCollectionInterface {
    LiveData<List<StorageRecord>> getStorageItems();
}

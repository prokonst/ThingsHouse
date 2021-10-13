package com.prokonst.thingshouse.tools;

import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.StorageItem;

import java.util.List;

public interface ItemsCollectionInterface {
    LiveData<List<StorageItem>> getStorageItems();
}

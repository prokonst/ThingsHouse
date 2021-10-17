package com.prokonst.thingshouse.viewmodel;

import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.prokonst.thingshouse.fragments.ShowStorageRecordsFragment;
import com.prokonst.thingshouse.fragments.ShowStorageRecordsFragmentDirections;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.dataview.StorageRecord;

import java.io.Serializable;

public class StorageRecordItemProvider implements Serializable {
    private StorageRecord storageRecord;
    private AppRepository appRepository;

    public StorageRecordItemProvider(AppRepository appRepository, StorageRecord storageRecord) {
        this.appRepository = appRepository;
        this.storageRecord = storageRecord;
    }

    public void clearQuantity(View view) {

        if(storageRecord.getQuantity() == 0.0) {
            Snackbar
                    .make(view, "Delete item \""+ storageRecord.getName() +"\"?", Snackbar.LENGTH_LONG)
                    .setAction("YES", (v) -> {
                        appRepository.deleteStorage(storageRecord.createStorage());
                    })
                    .show();
        } else {
            Snackbar
                    .make(view, "Set \""+ storageRecord.getName() +"\"quantity = 0.0 ?", Snackbar.LENGTH_LONG)
                    .setAction("YES", (v) -> {
                        storageRecord.setQuantity(0);
                        appRepository.updateStorage(storageRecord.createStorage());
                    })
                    .show();
        }

    }

    public void changeQuantity(View view) {

    }

    public void showThing(View view) {
        NavDirections action = ShowStorageRecordsFragmentDirections
                .actionShowStorageRecordsFragmentToThingDataFragment(storageRecord.createThing());
        Navigation.findNavController(view)
                .navigate(action);
    }

    public StorageRecord getStorageRecord() {
        return storageRecord;
    }
}

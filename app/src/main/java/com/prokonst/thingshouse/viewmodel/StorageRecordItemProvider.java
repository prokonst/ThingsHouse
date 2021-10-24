package com.prokonst.thingshouse.viewmodel;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.prokonst.thingshouse.dialog.InputStringValueDialog;
import com.prokonst.thingshouse.fragments.ShowStorageRecordsFragment;
import com.prokonst.thingshouse.fragments.ShowStorageRecordsFragmentDirections;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.tools.ScanBarCodeLauncher;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;

import java.io.Serializable;

public class StorageRecordItemProvider implements Serializable {
    private StorageRecord storageRecord;
    private AppRepository appRepository;

    public StorageRecordItemProvider(AppRepository appRepository, StorageRecord storageRecord) {
        this.appRepository = appRepository;
        this.storageRecord = storageRecord;
    }

    public void moveScan(View view) {
        ShowStorageRecordsFragment fragment = FragmentManager.findFragment(view);
        ScanBarCodeLauncher.startScanBarCodeLauncher(fragment.getActivity(), fragment.getScanForMoveActivityResultLauncher(storageRecord));
    }

    public void moveSelect(View view) {
        NavDirections action = ShowStorageRecordsFragmentDirections.actionShowStorageRecordsFragmentToShowThingsListFragment(
                new ShowThingsListParameters(true, "Select new storage",
                        ShowThingsListParameters.ActionType.MoveTo,
                        () -> storageRecord.getChildId(),
                        () -> storageRecord.getParentId(),
                        storageRecord.getQuantity()));

        Navigation.findNavController(view)
                .navigate(action);
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
        new InputStringValueDialog(view.getContext(), "Edit item storage", "Quantity", false,
                () -> Double.toString(storageRecord.getQuantity()),
                (newValue) -> {
                    try {
                        storageRecord.setQuantity(Double.parseDouble(newValue));
                        appRepository.updateStorage(storageRecord.createStorage());
                    } catch (Exception ex) {
                        Snackbar.make(view, "Enter a number", Snackbar.LENGTH_SHORT).show();
                    }
                }
        ).show();
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

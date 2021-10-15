package com.prokonst.thingshouse.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.viewmodel.StorageRecordsViewModel;
import com.prokonst.thingshouse.viewmodel.ThingsViewModel;

public class ShowStorageRecordsFragment extends Fragment {

    private StorageRecordsViewModel storageRecordsViewModel;

    public static ShowStorageRecordsFragment newInstance() {
        return new ShowStorageRecordsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        storageRecordsViewModel = new ViewModelProvider(this)
                .get(StorageRecordsViewModel.class);

        return inflater.inflate(R.layout.show_storage_records_fragment, container, false);
    }



}
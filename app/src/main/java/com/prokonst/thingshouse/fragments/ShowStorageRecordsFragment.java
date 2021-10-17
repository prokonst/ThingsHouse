package com.prokonst.thingshouse.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.databinding.FragmentShowStorageRecordsBinding;
import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.viewmodel.StorageRecordAdapter;
import com.prokonst.thingshouse.viewmodel.StorageRecordsViewModel;
import com.prokonst.thingshouse.viewmodel.ThingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowStorageRecordsFragment extends Fragment {

    private FragmentShowStorageRecordsBinding fragmentShowStorageRecordsBinding;
    private StorageRecordsViewModel storageRecordsViewModel;
    private ArrayList<StorageRecord> storageRecordArrayList;
    private RecyclerView storageRecordRecyclerView;
    private StorageRecordAdapter storageRecordAdapter;

    public static ShowStorageRecordsFragment newInstance() {
        return new ShowStorageRecordsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fragmentShowStorageRecordsBinding = FragmentShowStorageRecordsBinding.inflate(inflater, container, false);

        storageRecordsViewModel = new ViewModelProvider(this)
                .get(StorageRecordsViewModel.class);

        return fragmentShowStorageRecordsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageRecordRecyclerView = fragmentShowStorageRecordsBinding.recyclerview;
        storageRecordRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        storageRecordRecyclerView.setHasFixedSize(true);

        storageRecordAdapter = new StorageRecordAdapter();
        storageRecordRecyclerView.setAdapter(storageRecordAdapter);

        storageRecordsViewModel.getStorageRecordsByChildId("TODO: SetChildID").observe(this.getViewLifecycleOwner(), new Observer<List<StorageRecord>>() {
            @Override
            public void onChanged(List<StorageRecord> storageRecords) {
                storageRecordArrayList = (ArrayList<StorageRecord>) storageRecords;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    storageRecordAdapter.setStorageRecordArrayList(storageRecordArrayList);
                }
                storageRecordAdapter.getFilter().filter("");
            }
        });
    }
}
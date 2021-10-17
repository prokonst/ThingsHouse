package com.prokonst.thingshouse.fragments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokonst.thingshouse.databinding.FragmentShowStorageRecordsBinding;
import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.tools.ShowStorageRecordsParameters;
import com.prokonst.thingshouse.viewmodel.StorageRecordAdapter;
import com.prokonst.thingshouse.viewmodel.StorageRecordsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShowStorageRecordsFragment extends Fragment {

    private FragmentShowStorageRecordsBinding fragmentShowStorageRecordsBinding;
    private StorageRecordsViewModel storageRecordsViewModel;
    private ArrayList<StorageRecord> storageRecordArrayList;
    private RecyclerView storageRecordRecyclerView;
    private StorageRecordAdapter storageRecordAdapter;

    private ShowStorageRecordsParameters fragmentInputParams;

    public static ShowStorageRecordsFragment newInstance() {
        return new ShowStorageRecordsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ShowStorageRecordsFragmentArgs args = ShowStorageRecordsFragmentArgs.fromBundle(getArguments());
        fragmentInputParams = args.getShowStorageRecordsParameters();

        fragmentShowStorageRecordsBinding = FragmentShowStorageRecordsBinding.inflate(inflater, container, false);

        storageRecordsViewModel = new ViewModelProvider(this)
                .get(StorageRecordsViewModel.class);

        setTitleActionBar();

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

        LiveData<List<StorageRecord>> storageRecords;
        if(fragmentInputParams.getReportType() == ShowStorageRecordsParameters.ReportType.WhereUsed) {
            storageRecords = storageRecordsViewModel.getStorageRecordsByChildId(fragmentInputParams.getSourceThing().getThingId());
        } else if (fragmentInputParams.getReportType() == ShowStorageRecordsParameters.ReportType.SelfItems) {
            storageRecords = storageRecordsViewModel.getStorageRecordsByParentId(fragmentInputParams.getSourceThing().getThingId());
        }
        else {
            Toast.makeText(this.getContext(), "Unknown report type: " + fragmentInputParams.getReportType(), Toast.LENGTH_SHORT).show();
            return;
        }

        storageRecordAdapter.setOnItemClickListener(
                (storageRecord) -> {
                    NavDirections action = ShowStorageRecordsFragmentDirections
                            .actionShowStorageRecordsFragmentToThingDataFragment(storageRecord.createThing());
                    NavHostFragment.findNavController(ShowStorageRecordsFragment.this)
                            .navigate(action);
                }
        );

//        ItemTouchHelper.SimpleCallback itemTouchHelperSimpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                Toast.makeText(ShowStorageRecordsFragment.this.getContext(), "" + direction, Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        new ItemTouchHelper(itemTouchHelperSimpleCallback).attachToRecyclerView(storageRecordRecyclerView);


        storageRecords.observe(this.getViewLifecycleOwner(), new Observer<List<StorageRecord>>() {
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

    private void setTitleActionBar(){
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if(appCompatActivity == null)
            return;

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if(actionBar == null)
            return;

        String title;
        if(fragmentInputParams.getReportType() == ShowStorageRecordsParameters.ReportType.SelfItems) {
            title = "Items for: " + fragmentInputParams.getSourceThing().getName();
        } else if(fragmentInputParams.getReportType() == ShowStorageRecordsParameters.ReportType.WhereUsed) {
            title = "Where used: " + fragmentInputParams.getSourceThing().getName();
        } else {
            title = "Unknown report type: " + fragmentInputParams.getReportType();
        }

        actionBar.setTitle(title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentShowStorageRecordsBinding = null;
    }
}
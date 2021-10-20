package com.prokonst.thingshouse.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prokonst.thingshouse.dialog.ChangeValueInterface;
import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.tools.CaptureCameraImage;
import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.databinding.FragmentThingDataBinding;
import com.prokonst.thingshouse.dialog.AddThingToStorageDialog;
import com.prokonst.thingshouse.dialog.ChangeThingValueDialog;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.ItemsCollectionInterface;
import com.prokonst.thingshouse.tools.ScanBarCodeLauncher;
import com.prokonst.thingshouse.tools.ShowStorageRecordsParameters;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;
import com.prokonst.thingshouse.viewmodel.ThingsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public class ThingDataFragment extends Fragment {


    private ThingsViewModel thingsViewModel;
    private Thing thing;
    private String thingId;
    FragmentThingDataBinding fragmentThingDataBinding;
    private ActivityResultLauncher<Intent> scanForSetBarCodeActivityResultLauncher;
    private ActivityResultLauncher<Intent> scanForAddActivityResultLauncher;

    private double currentQuantity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thingId = ThingDataFragmentArgs.fromBundle(getArguments()).getSelectedThing().getThingId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thingsViewModel = new ViewModelProvider(this).get(ThingsViewModel.class);

        fragmentThingDataBinding = FragmentThingDataBinding.inflate(inflater, container, false);


        scanForSetBarCodeActivityResultLauncher = ThingDataFragment.this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {

                    try {
                        String barCode = ScanBarCodeLauncher.getBarCode(result);

                        if (barCode != null && thing != null) {

                            try {
                                thing.setBarCode(barCode);
                                thingsViewModel.updateThing(thing);
                            } catch (SQLiteConstraintException sqlEx) {
                                Toast.makeText(ThingDataFragment.this.getActivity(), "BarCode already used", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ThingDataFragment.this.getActivity(), "BarCodeNotScanned", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex) {
                        Toast.makeText(ThingDataFragment.this.getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
        );

        scanForAddActivityResultLauncher =  ThingDataFragment.this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {

                    try {
                        String barCode = ScanBarCodeLauncher.getBarCode(result);
                        if (barCode != null && thing != null) {
                            if(barCode.equals(thing.getBarCode())) {
                                Toast.makeText(getContext(), "Error: apply to self", Toast.LENGTH_LONG).show();
                            } else {
                                AppRepository appRepository = new AppRepository(ThingDataFragment.this.getActivity().getApplication());
                                appRepository.addQuantityToStorageByBarcode(barCode, thing.getThingId(), currentQuantity);
                            }
                        } else {
                            Toast.makeText(ThingDataFragment.this.getActivity(), "BarCodeNotScanned", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex) {
                        Toast.makeText(ThingDataFragment.this.getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
        );

        return fragmentThingDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentThingDataBinding.setCkickHandlers(new ThingDataClickHandlers());

        thingsViewModel.getThingById(thingId).observe(this.getViewLifecycleOwner(), new Observer<Thing>() {
            @Override
            public void onChanged(Thing t) {
                thing = t;
                fragmentThingDataBinding.setThing(thing);
                setTitleActionBar();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentThingDataBinding = null;
    }

    private void setTitleActionBar(){
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if(appCompatActivity == null)
            return;

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if(actionBar == null)
            return;

        actionBar.setTitle("Edit " + thing.getName());
    }


    public class ThingDataClickHandlers {

        public void onChangeNameClicked(View view) {
            new ChangeThingValueDialog(ThingDataFragment.this.getActivity(), "Edit thing", "Name",
                    () -> thing.getName(),
                    (newValue) -> {
                        thing.setName(newValue);
                        thingsViewModel.updateThing(thing);
                        setTitleActionBar();
                    }
            ).show();
        }

        public void onChangeUnitClicked(View view) {
            new ChangeThingValueDialog(ThingDataFragment.this.getActivity(),"Edit thing", "Unit",
                    () -> thing.getUnit(),
                    (newValue) -> {
                        thing.setUnit(newValue);
                        thingsViewModel.updateThing(thing);
                    }
            ).show();
        }

        public void onChangePhoto(View view) {
            CaptureCameraImage.getCaptureCameraImage().capture(thing);
        }

        public void onDeleteThing(View view) {
            final AlertDialog alertDialog = (new AlertDialog.Builder(view.getContext()))
                    .setMessage("Delete thing " + thing.getName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Delete ", (dialogBox, id) -> {
                        thingsViewModel.deleteThing(thing);
                        NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowThingListFragment(
                                new ShowThingsListParameters(true, "Browse things",
                                        ShowThingsListParameters.ActionType.ViewThings, null, null, 0) );
                        NavHostFragment.findNavController(ThingDataFragment.this)
                                .navigate(action);
                    })
                    .setNegativeButton("Cancel", (dialogBox, id) -> {
                        dialogBox.dismiss();
                    })
                    .create();

            alertDialog.show();
        }
        public void onSetBarCode(View view) {
            ScanBarCodeLauncher.startScanBarCodeLauncher(ThingDataFragment.this.getActivity(), scanForSetBarCodeActivityResultLauncher);
        }
        public void onAddTo(View view) {

            (new AddThingToStorageDialog(ThingDataFragment.this.getActivity(), AddThingToStorageDialog.ButtonsType.ScanAndSelect,
                    () -> "1",
                    (newValue, actionType) -> {
                        if(actionType == ChangeValueInterface.ActionType.Select) {

                            NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowThingListFragment(
                                    new ShowThingsListParameters(
                                    true, "Select storage for: " + thing.getName(),
                                    ShowThingsListParameters.ActionType.AddThingTo, thing, null, Double.parseDouble(newValue)) );
                            NavHostFragment.findNavController(ThingDataFragment.this)
                                    .navigate(action);
                        } else if(actionType == ChangeValueInterface.ActionType.Scan) {
                            currentQuantity = Double.parseDouble(newValue);
                            ScanBarCodeLauncher.startScanBarCodeLauncher(ThingDataFragment.this.getActivity(), scanForAddActivityResultLauncher);
                        }
                        else {
                            Toast.makeText(getContext(), "Unknown ActionType: " + actionType, Toast.LENGTH_LONG).show();
                        }

                    }
            )).show();
        }
        public void onWhereUsed(View view) {
//            AppRepository appRepository = new AppRepository(ThingDataFragment.this.getActivity().getApplication());
//            showItems(view, () ->  appRepository.getStorageRecordsByChildId(thing.getThingId())  );
            NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowStorageRecordsFragment(
                    new ShowStorageRecordsParameters(thing, ShowStorageRecordsParameters.ReportType.WhereUsed));
            NavHostFragment.findNavController(ThingDataFragment.this).navigate(action);

        }
        public void onItems(View view) {
//            AppRepository appRepository = new AppRepository(ThingDataFragment.this.getActivity().getApplication());
//            showItems(view, () -> appRepository.getStorageRecordsByParentId(thing.getThingId())  );
            NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowStorageRecordsFragment(
                    new ShowStorageRecordsParameters(thing, ShowStorageRecordsParameters.ReportType.SelfItems));
            NavHostFragment.findNavController(ThingDataFragment.this).navigate(action);

        }

        public void onShowPhoto(View view) {
            NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowPhotoFragment(thing);
            NavHostFragment.findNavController(ThingDataFragment.this).navigate(action);
        }

        private void showItems(View view, ItemsCollectionInterface itemsCollection) {
            itemsCollection.getStorageItems().observe(ThingDataFragment.this.getViewLifecycleOwner(), (listStorageItems) -> {
                if(listStorageItems.size() == 0) {
                    Toast.makeText(getContext(), "List is empty", Toast.LENGTH_LONG).show();
                } else {
                    StringBuilder sb = new StringBuilder();

                    for(StorageRecord curStorageRecord : listStorageItems) {

                        sb.append("-----\n");
                        sb.append("Name = " + curStorageRecord.getName());
                        sb.append("\n");
                        sb.append("Quantity = " + Double.toString(curStorageRecord.getQuantity()));
                        sb.append("\n");
                        sb.append("Unit = " + curStorageRecord.getUnit());
                        sb.append("-----\n");
                    }

                    Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
package com.prokonst.thingshouse.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prokonst.thingshouse.tools.CaptureCameraImage;
import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.databinding.FragmentThingDataBinding;
import com.prokonst.thingshouse.dialog.AddThingToStorageDialog;
import com.prokonst.thingshouse.dialog.ChangeThingValueDialog;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.dataview.StorageItem;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.ItemsCollectionInterface;
import com.prokonst.thingshouse.tools.ScanBarCodeLauncher;
import com.prokonst.thingshouse.viewmodel.ThingsViewModel;


public class ThingDataFragment extends Fragment {


    private ThingsViewModel thingsViewModel;
    private Thing thing;
    FragmentThingDataBinding fragmentThingDataBinding;
    private ActivityResultLauncher<Intent> scanForSetBarCodeActivityResultLauncher;
    private ActivityResultLauncher<Intent> scanForAddActivityResultLauncher;

    private double currentQuantity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thing = ThingDataFragmentArgs.fromBundle(getArguments()).getSelectedThing();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentThingDataBinding = FragmentThingDataBinding.inflate(inflater, container, false);
        fragmentThingDataBinding.setThing(thing);
        fragmentThingDataBinding.setCkickHandlers(new ThingDataClickHandlers());

        thingsViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(ThingsViewModel.class);

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

        setTitleActionBar();

        return fragmentThingDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView testTextView = view.findViewById(R.id.nameTextView);
        testTextView.setText(thing.getName());
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
            (new ChangeThingValueDialog(ThingDataFragment.this.getActivity(), "Name",
                    () -> thing.getName(),
                    (newValue) -> {
                        thing.setName(newValue);
                        thingsViewModel.updateThing(thing);
                        setTitleActionBar();
                    }
            )).show();
        }

        public void onChangeUnitClicked(View view) {
            (new ChangeThingValueDialog(ThingDataFragment.this.getActivity(), "Unit",
                    () -> thing.getUnit(),
                    (newValue) -> {
                        thing.setUnit(newValue);
                        thingsViewModel.updateThing(thing);
                    }
            )).show();
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
                                true, "Browse things", "ViewThings", null, null, null);
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

            (new AddThingToStorageDialog(ThingDataFragment.this.getActivity(),
                    () -> "1",
                    (newValue, actionNameKey) -> {
                        if(actionNameKey.equals("Select")) {

                            NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowThingListFragment(
                                    true, "Select storage for: " + thing.getName(), "AddThingTo", thing, null, newValue);
                            NavHostFragment.findNavController(ThingDataFragment.this)
                                    .navigate(action);
                        } else if(actionNameKey.equals("Scan")) {
                            currentQuantity = Double.parseDouble(newValue);
                            ScanBarCodeLauncher.startScanBarCodeLauncher(ThingDataFragment.this.getActivity(), scanForAddActivityResultLauncher);
                        }
                        else {
                            Toast.makeText(getContext(), "Unknown ActionNameKey: " + actionNameKey, Toast.LENGTH_LONG).show();
                        }

                    }
            )).show();
        }
        public void onWhereUsed(View view) {
            AppRepository appRepository = new AppRepository(ThingDataFragment.this.getActivity().getApplication());
            showItems(view, () ->  appRepository.getStorageItemsByChildId(thing.getThingId())  );
        }
        public void onItems(View view) {
            AppRepository appRepository = new AppRepository(ThingDataFragment.this.getActivity().getApplication());
            showItems(view, () -> appRepository.getStorageItemsByParentId(thing.getThingId())  );
        }

        private void showItems(View view, ItemsCollectionInterface itemsCollection) {
            itemsCollection.getStorageItems().observe(ThingDataFragment.this.getViewLifecycleOwner(), (listStorageItems) -> {
                if(listStorageItems.size() == 0) {
                    Toast.makeText(getContext(), "List is empty", Toast.LENGTH_LONG).show();
                } else {
                    StringBuilder sb = new StringBuilder();

                    for(StorageItem curStorageItem : listStorageItems) {

                        sb.append("-----\n");
                        sb.append("Name = " + curStorageItem.getName());
                        sb.append("\n");
                        sb.append("Quantity = " + Double.toString(curStorageItem.getQuantity()));
                        sb.append("\n");
                        sb.append("Unit = " + curStorageItem.getUnit());
                        sb.append("-----\n");
                    }

                    Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
package com.prokonst.thingshouse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.prokonst.thingshouse.databinding.FragmentThingDataBinding;
import com.prokonst.thingshouse.databinding.FragmentThingDataBindingImpl;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.Thing;
import com.prokonst.thingshouse.model.ThingsDataBase;
import com.prokonst.thingshouse.tools.ScanBarCodeLauncher;
import com.prokonst.thingshouse.viewmodel.ThingsViewModel;

import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;


public class ThingDataFragment extends Fragment {


    private ThingsViewModel thingsViewModel;
    private Thing thing;
    FragmentThingDataBinding fragmentThingDataBinding;
    private ActivityResultLauncher<Intent> startBarCodeScannerActivityResultLauncher;

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

        startBarCodeScannerActivityResultLauncher = ThingDataFragment.this.registerForActivityResult(
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Edit " + thing.getName());
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
                        NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToSecondFragment();
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
            ScanBarCodeLauncher.startScanBarCodeLauncher(ThingDataFragment.this.getActivity(), startBarCodeScannerActivityResultLauncher);
        }
    }
}
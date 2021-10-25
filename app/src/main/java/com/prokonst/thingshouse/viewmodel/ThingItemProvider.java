package com.prokonst.thingshouse.viewmodel;

import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.prokonst.thingshouse.dialog.AddThingToStorageDialog;
import com.prokonst.thingshouse.fragments.ShowThingsListFragment;
import com.prokonst.thingshouse.fragments.ShowThingsListFragmentDirections;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.ScanBarCodeLauncher;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;

import java.io.Serializable;

public class ThingItemProvider implements Serializable {
    private Thing thing;
    private AppRepository appRepository;

    public ThingItemProvider(Thing thing, AppRepository appRepository) {
        this.thing = thing;
        this.appRepository = appRepository;
    }

    public Thing getThing() {
        return thing;
    }
    public void addToScan(View view) {
        ShowThingsListFragment fragment = FragmentManager.findFragment(view);
        new AddThingToStorageDialog(fragment.getActivity(), AddThingToStorageDialog.ButtonsType.Ok,
                () -> "1",
                (newValue, actionType) -> {
                        double quantity = Double.parseDouble(newValue);
                        ScanBarCodeLauncher.startScanBarCodeLauncher(fragment.getActivity(),
                                fragment.getScanForAddActivityResultLauncher(thing, quantity));
                }
        ).show();
    }

    public void addToSelect(View view) {
        ShowThingsListFragment fragment = FragmentManager.findFragment(view);
        NavController navController = NavHostFragment.findNavController(fragment);
        new AddThingToStorageDialog(fragment.getActivity(), AddThingToStorageDialog.ButtonsType.Ok,
                () -> "1",
                (newValue, actionType) -> {
                    NavDirections action = ShowThingsListFragmentDirections.actionShowThingsListFragmentSelf(
                            new ShowThingsListParameters(
                                    true, "Select storage for: " + thing.getName(),
                                    ShowThingsListParameters.ActionType.AddThingTo, thing, null, Double.parseDouble(newValue)) );
                    navController.navigate(action);
                }
        ).show();
    }

    public void onThingClick(View view) {
        ShowThingsListFragment fragment = FragmentManager.findFragment(view);
        NavController navController = NavHostFragment.findNavController(fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        ShowThingsListParameters fragmentInputParams = fragment.getFragmentInputParams();

        if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.ViewThings) {
            NavDirections action = ShowThingsListFragmentDirections.actionShowThingsListFragmentToThingDataFragment(thing);
            NavHostFragment.findNavController(fragment)
                    .navigate(action);
        } else if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.AddThingTo) {
            ShowThingsListParameters.ThingIdInterface newParentThing = thing;
            ShowThingsListParameters.ThingIdInterface movingThing = fragmentInputParams.getSourceThing();

            if(newParentThing.getThingId().equals(movingThing.getThingId())) {
                Toast.makeText(view.getContext(), "Error: apply to self", Toast.LENGTH_LONG).show();
            }
            else {
                appRepository.addQuantityToStorageByParentId(newParentThing.getThingId(), movingThing.getThingId(), fragmentInputParams.getQuantity());
            }

            NavigationUI.navigateUp(navController, appBarConfiguration);
        } else if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.MoveTo) {

            ShowThingsListParameters.ThingIdInterface newParentThing = thing;
            ShowThingsListParameters.ThingIdInterface movingThing = fragmentInputParams.getSourceThing();
            ShowThingsListParameters.ThingIdInterface oldParentThing = fragmentInputParams.getTargetThing();

            appRepository.moveStorage(oldParentThing, movingThing, newParentThing);

            NavigationUI.navigateUp(navController, appBarConfiguration);

        } else {
            Toast.makeText(view.getContext(), "Unknown action type", Toast.LENGTH_LONG).show();
        }
    }
}

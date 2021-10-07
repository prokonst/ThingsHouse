package com.prokonst.thingshouse;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.prokonst.thingshouse.model.Thing;
import com.prokonst.thingshouse.model.ThingsDataBase;

import java.util.NavigableMap;


public class ThingDataFragment extends Fragment {


    private Thing thing;
    FragmentThingDataBinding fragmentThingDataBinding;

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

        return fragmentThingDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView testTextView = view.findViewById(R.id.nameTextView);
        testTextView.setText(thing.getName());
    }

    public class ThingDataClickHandlers {
        public void onChangeNameClicked(View view) {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ThingDataFragment.this.getActivity().getApplicationContext());
            View viewEditThing = layoutInflaterAndroid.inflate(R.layout.layout_edit_thing, null);

            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ThingDataFragment.this.getActivity());
            alertDialogBuilderUserInput.setView(viewEditThing);

            TextView thingTitleTextView = viewEditThing.findViewById(R.id.thingTitle);
            final EditText nameEditText = viewEditThing.findViewById(R.id.nameEditText);

            thingTitleTextView.setText("Edit Thing");
            nameEditText.setText(thing.getName());

            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("ChangeName", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    Toast.makeText(ThingDataFragment.this.getActivity(), "Command cancelled by user", Toast.LENGTH_SHORT).show();
                                }
                            });


            final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String newThingName = nameEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(newThingName)) {
                        Toast.makeText(ThingDataFragment.this.getActivity(), "Enter thing name!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        alertDialog.dismiss();
                    }

                    thing.setName(newThingName);
                    ThingsDataBase.UpdateThing(thing);
                }
            });
        }

        public void onChangePhoto(View view) {
            CaptureCameraImage.getCaptureCameraImage().capture(thing);
        }

        public void onDeleteThing(View view) {
            ThingsDataBase.DeleteThing(thing);
            NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToSecondFragment();
            NavHostFragment.findNavController(ThingDataFragment.this)
                    .navigate(action);
        }
    }
}
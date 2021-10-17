package com.prokonst.thingshouse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.prokonst.thingshouse.databinding.FragmentFirstBinding;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavDirections action = FirstFragmentDirections.actionFirstFragmentToShowThingsListFragment(
                        new ShowThingsListParameters(false, "Browse things",
                                "ViewThings", null, null, 0) );
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(action);
            }
        });

        binding.testButton.setOnClickListener((v) -> {
            //TODO:
            return;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
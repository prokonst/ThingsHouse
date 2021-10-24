package com.prokonst.thingshouse.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseUser;
import com.prokonst.thingshouse.databinding.FragmentFirstBinding;
import com.prokonst.thingshouse.model.Authorization;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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



        binding.loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                        Authorization.getInstance().signInWithEmailAndPassword(email, password,
                        FirstFragment.this.getActivity(), new Authorization.AuthorizationHandlerInterface() {
                    @Override
                    public void onSuccessAuth(FirebaseUser user) {
                        //123binding.emailEditText.setText("");
                        binding.passwordEditText.setText("");
                        showThingsListFragment();
                    }
                });
            }
        });

        binding.buttonFirst.setOnClickListener((v) -> {
            Toast.makeText(this.getContext(), "Button is empty" + Authorization.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            return;
        });

        binding.testButton.setOnClickListener((v) -> {
            Toast.makeText(this.getContext(), "" + Authorization.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            return;
        });

        binding.continueAsButton.setOnClickListener( (v) -> {
            showThingsListFragment();
        });

        if(Authorization.getCurrentUser() != null){
            binding.continueAsButton.setVisibility(View.VISIBLE);
            binding.continueAsButton.setText("Continue as " + Authorization.getCurrentUser().getEmail());
            binding.continueAsButton.setOnClickListener( (v) -> {
                showThingsListFragment();
            });
        }
        else {
            binding.continueAsButton.setVisibility(View.GONE);
            binding.continueAsButton.setOnClickListener( null );
        }



    }

    private void showThingsListFragment() {
        NavDirections action = FirstFragmentDirections.actionFirstFragmentToShowThingsListFragment(
                new ShowThingsListParameters(false, "Browse things",
                        ShowThingsListParameters.ActionType.ViewThings, null, null, 0) );
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
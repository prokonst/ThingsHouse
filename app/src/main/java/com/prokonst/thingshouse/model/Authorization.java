package com.prokonst.thingshouse.model;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.prokonst.thingshouse.dialog.InputStringValueDialog;
import com.prokonst.thingshouse.fragments.FirstFragment;

public class Authorization {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private static Authorization authorizationInstance = null;

    public static Authorization getInstance() {
        if(Authorization.authorizationInstance == null)
            Authorization.authorizationInstance = new Authorization();

        return Authorization.authorizationInstance;
    }

    public static FirebaseUser getCurrentUser(){
        return Authorization.getInstance().currentUser;
    }


    private Authorization() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.currentUser = this.firebaseAuth.getCurrentUser();
    }

    private void createUserWithEmailAndPassword(String email, String password, Activity activity, AuthorizationHandlerInterface authorizationHandler) {
        if(this.currentUser != null) {
            this.firebaseAuth.signOut();
            this.currentUser = null;
        }

        new InputStringValueDialog(activity, "Confirm password for: " + email, "Password", true,
                () -> "",
                (newValue) -> {
                    if(!newValue.equals(password)){
                        Toast.makeText(activity, "Password not confirmed", Toast.LENGTH_LONG).show();
                    }
                    else {
                        this.firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Authorization.this.currentUser = firebaseAuth.getCurrentUser();
                                            Toast.makeText(activity, "SignIn as new user: " + Authorization.this.currentUser.getEmail(),
                                                    Toast.LENGTH_SHORT).show();
                                            authorizationHandler.onSuccessAuth(Authorization.this.currentUser);
                                        } else {
                                            Exception exception = task.getException();
                                            Toast.makeText(activity, "Authentication failed.\n" + exception.getMessage() + "\n" + exception.getClass(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
        ).show("Confirm");
    }

    public void signInWithEmailAndPassword(String email, String password, Activity activity, Authorization.AuthorizationHandlerInterface authorizationHandler){
        if(this.currentUser != null) {
            this.firebaseAuth.signOut();
            this.currentUser = null;
        }

        if(email.isEmpty()) {
            Toast.makeText(activity, "Mail is empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()) {
            Toast.makeText(activity, "Password is empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        this.firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Authorization.this.currentUser = firebaseAuth.getCurrentUser();
                    Toast.makeText(activity, "SignIn as user: " + Authorization.this.currentUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                    authorizationHandler.onSuccessAuth(Authorization.this.currentUser);
                } else {
                    Exception exception = task.getException();

                    if(exception instanceof FirebaseAuthInvalidUserException){
                        Authorization.this.createUserWithEmailAndPassword(email, password, activity, authorizationHandler);
                    } else {
                        Toast.makeText(activity, "Authentication failed.\n" + exception.getMessage() + "\n" + exception.getClass(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public interface AuthorizationHandlerInterface{
        void onSuccessAuth(FirebaseUser user);
    }
}


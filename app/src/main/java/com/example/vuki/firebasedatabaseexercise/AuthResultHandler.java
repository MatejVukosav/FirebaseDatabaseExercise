package com.example.vuki.firebasedatabaseexercise;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by Vuki on 3.11.2015..
 */
public class AuthResultHandler implements Firebase.AuthResultHandler {
    /**
     * Utility class for authentication results
     */

    private final String provider;
    private final MainActivity mainActivityClass;

    public AuthResultHandler(String provider,MainActivity mainActivityClass) {
        this.provider = provider;
        this.mainActivityClass=mainActivityClass;
    }

    @Override
    public void onAuthenticated(AuthData authData) {
        mainActivityClass.mAuthProgressDialog.hide();
        Log.i(mainActivityClass.getClass().getSimpleName(), provider + " auth successful");
        mainActivityClass.setAuthenticatedUser(authData);
    }



    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        mainActivityClass.mAuthProgressDialog.hide();
        Log.i(mainActivityClass.getClass().getSimpleName(), "FIREBASE ERROR "+ firebaseError.getMessage());
        mainActivityClass.showErrorDialog(firebaseError.toString());
    }
}
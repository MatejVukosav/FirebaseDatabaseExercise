package com.example.vuki.firebasedatabaseexercise;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 *  Initialize Firebase with the application context. This must happen before the client is used.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}

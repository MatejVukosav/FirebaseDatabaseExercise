package com.example.vuki.firebasedatabaseexercise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registration extends AppCompatActivity {
    @Bind(R.id.register_email)
    EditText registerEmail;
    @Bind(R.id.register_password)
    EditText registerPassword;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        gson = new Gson();
    }

    int i = 0;

    @OnClick(R.id.register_btn)
    public void onRegisterClick() {
        User user = new User("admin", "admin", "admin");
        String userString = gson.toJson(user);

        /*MainActivity.mFirebaseRef.child("users").child("user" + i).child("name").setValue(user.name);
        MainActivity.mFirebaseRef.child("users").child("user" + i).child("username").setValue(user.surname);
        MainActivity.mFirebaseRef.child("users").child("user" + i).child("email").setValue(user.email);*/

        //MainActivity.mFirebaseRef.child("users").push().setValue(userString);
        String email=registerEmail.getText().toString();
        String pass=registerPassword.getText().toString();
        MainActivity.mFirebaseRef.child("users").push().createUser(email+"@medolino.com","medolino", new AuthRegistrationHandler());


    }


    private class AuthRegistrationHandler implements Firebase.ResultHandler{

        @Override
        public void onSuccess() {
            NotesHelpers.toastMessage(getApplicationContext(),"WUHUU rega");

        }

        @Override
        public void onError(FirebaseError firebaseError) {
            NotesHelpers.toastMessage(getApplicationContext(),"GRESKAA REGA"+firebaseError.toString());

        }
    }

}

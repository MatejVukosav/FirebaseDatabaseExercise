package com.example.vuki.firebasedatabaseexercise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //a dialog that is presented until the Firebase authentication finished
    public ProgressDialog mAuthProgressDialog;

    //login button for password and mail
    @Bind(R.id.login_btn)
    Button mLoginBtn;
    //login text for enter email
    @Bind(R.id.login_email)
    EditText mLoginEmail;
    //login text for enter password
    @Bind(R.id.login_password)
    EditText mLoginPassword;

    //register button for user registration
    @Bind(R.id.login_register_btn)
    Button mRegisterBtn;

    @Bind(R.id.email_text_input)
    TextInputLayout emailTxtInput;
    @Bind(R.id.password_text_input)
    TextInputLayout passwordTxtInput;


    public static Firebase mFirebaseRef; //a reference to the Firebase
    private AuthData mAuthData;//a Data from the Authenticated User
    private Firebase.AuthStateListener mAuthStateListener; //listener for Firebase session changes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setFirebase();
        init();

    }


    private void setFirebase() {
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

     /*   mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    NotesHelpers.logMessage(TAG, user.name + ": " +user.surname);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                NotesHelpers.logMessage(TAG,"on data canceled "+ firebaseError.getMessage());
            }
        });*/

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        //Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
        // user and hide hide any login buttons
        mFirebaseRef.addAuthStateListener(mAuthStateListener);
    }

boolean inputValidation;
boolean emailValidation;
boolean passwordValidation;

    private void init() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAuthInput();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,Registration.class);
                startActivity(intent);
            }
        });


        //setup the progress dialog that is displayed later when authenticating with firebase
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase..");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();

        emailTxtInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!ValidationHelpers.checkEmail(s.toString())){
                    emailTxtInput.setError("Email mora sadržavati znak @");
                    emailValidation =false;
                }else{
                    emailTxtInput.setError(null);
                    emailValidation =true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputValidation = emailValidation && passwordValidation;
            }
        });

        passwordTxtInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidationHelpers.checkPassword(s.toString())){
                    passwordTxtInput.setError("Password min. " + ValidationHelpers.MIN_PASSWORD_LENGTH + " znamenki!");
                    passwordValidation =false;
                }else{
                    passwordTxtInput.setError(null);
                    passwordValidation=true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputValidation = emailValidation&&passwordValidation;
            }
        });

    }


    private void checkAuthInput() {
       if(inputValidation){
           loginWithPassword();
       }else{
           NotesHelpers.toastMessage(this, "Pogreska prilikom prijave. Email ili password su pogresni!");
       }

    }


    // This method will attempt to authenticate a user to firebase given an oauth_token (and other
    // necessary parameters depending on the provider)
    private void authWithFirebase(final String provider, Map<String, String> options) {
        if (options.containsKey("error")) {
            showErrorDialog(options.get("error"));
        } else {
            mAuthProgressDialog.show();
            mFirebaseRef.authWithOAuthToken(provider, options.get("oauth_token"), new AuthResultHandler(provider, this));
        }
    }

    // Show errors to users
    public void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void firebaseLogout() {
        if (this.mAuthData != null) {
            //  logout of Firebase
            mFirebaseRef.unauth();
            //Logout of any of the Frameworks.Ensures the user is not logged into
            // Facebook/Google+ after logging out of Firebase
            setAuthenticatedUser(null);
        }
    }

    // Once a user is logged in ,take the mAuthData provided from Firebase and "use" it
    public void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            //logged in with passwor
            mLoginEmail.setVisibility(View.GONE);
            mLoginPassword.setVisibility(View.GONE);

         if (authData.getProvider().equals("user")) {
                mLoginBtn.setText("Odjavi se");
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }

        } else {
            //No authenticated user, show all the login buttons
            mLoginEmail.setVisibility(View.VISIBLE);
            mLoginPassword.setVisibility(View.VISIBLE);
            mLoginBtn.setText("Prijavi se");

        }

        this.mAuthData = authData;
        //invalidate options menu to hide/show the logout button
        supportInvalidateOptionsMenu();
    }


    /************************************
     * PASSWORD              *
     **************************************/

    public void loginWithPassword() {
        mAuthProgressDialog.show();
       //mFirebaseRef.authWithPassword(mLoginEmail.getText().toString(), mLoginPassword.getText().toString(), new AuthResultHandler("users", this));

        mFirebaseRef.child("users").child("user1").authWithPassword("admin", "admin", new AuthResultHandler("password", this));



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if changing configurations,stop tracking firebase session
        mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }


    // This method fires when any startActivityForResult finishes. The requestCode maps to
    // the value passed into startActivityForResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Map<String, String> options = new HashMap<String, String>();
        //check for different login methods

    }




}

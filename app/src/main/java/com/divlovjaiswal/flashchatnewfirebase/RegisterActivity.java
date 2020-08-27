package com.divlovjaiswal.flashchatnewfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";
    public static final String TAG="FlashchatDebug";
    // TODO: Add member variables here:
    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private ProgressBar mProgressBar;

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private String email,password;
    //SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = findViewById(R.id.register_email);
        mPasswordView = findViewById(R.id.register_password);
        mConfirmPasswordView = findViewById(R.id.register_confirm_password);
        mUsernameView = findViewById(R.id.register_username);
        mProgressBar=findViewById(R.id.progressBar2);
        //sp=getSharedPreferences(CHAT_PREFS,MODE_PRIVATE);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        // TODO: Get hold of an instance of FirebaseAuth
        mAuth=FirebaseAuth.getInstance();

    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {
        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();
        }
    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        String confirmPassword= mConfirmPasswordView.getText().toString();
        return confirmPassword.equals(password)&&password.length()>5;
    }

    // TODO: Create a Firebase user
    private void createFirebaseUser(){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG,"create User onComplete: "+task.isSuccessful());
                mProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if(!task.isSuccessful()){
                    Log.d(TAG,"User creation failed: "+task.getException());
                    showErrorDialog("Registration attempt failed! Could you please try again?");
                }
                else{
                    saveDisplayName(mAuth.getCurrentUser());
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    // TODO: Save the display name to Shared Preferences
    private void saveDisplayName(FirebaseUser user){
        if(user!=null) {
            String username = mUsernameView.getText().toString();
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username).build();
            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d(RegisterActivity.TAG, "Username uploaded!");
                }
            });
        }
        //shared preferences way
        // sp.edit().putString(DISPLAY_NAME_KEY,username).apply();
    }

    // TODO: Create an alert dialog to show in case registration failed
   private void showErrorDialog(String message){
       new AlertDialog.Builder(this)
               .setTitle("Oops")
               .setMessage(message)
               .setNeutralButton(android.R.string.ok,null)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .show();
   }

}
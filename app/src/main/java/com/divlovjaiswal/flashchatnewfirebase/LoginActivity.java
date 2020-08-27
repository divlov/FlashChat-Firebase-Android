package com.divlovjaiswal.flashchatnewfirebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class LoginActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView =findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);
        progressBar=findViewById(R.id.progressBar);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL || id==EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // TODO: Grab an instance of FirebaseAuth
        mAuth=FirebaseAuth.getInstance();
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // TODO: Call attemptLogin() here
        attemptLogin();

    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.divlovjaiswal.flashchatnewfirebase.RegisterActivity.class);
        startActivity(intent);
    }

    // TODO: Complete the attemptLogin() method
    private void attemptLogin() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String email=mEmailView.getText().toString();
        String password=mPasswordView.getText().toString();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) return;
        Toast.makeText(this,"Logging in",Toast.LENGTH_SHORT).show();

        // TODO: Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if(!task.isSuccessful()){
                    Log.d(RegisterActivity.TAG,"Problem signing in: "+task.getException());
                    showErrorDialog("Problem signing in. Please try again");
                }
                else{
                    Log.d(RegisterActivity.TAG,"Log in successful");
                    Intent intent=new Intent(LoginActivity.this,MainChatActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });


    }

    // TODO: Show error on screen with an alert dialog
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setNeutralButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
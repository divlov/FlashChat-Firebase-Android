package com.divlovjaiswal.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // TODO: Set up the display name and get the Firebase reference
        setupDisplayName();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();


        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_SEND||i==EditorInfo.IME_NULL){
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    // TODO: Retrieve the display name from the Shared Preferences
    private void setupDisplayName(){
//        SharedPreferences sp=getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
//        mDisplayName=sp.getString(RegisterActivity.DISPLAY_NAME_KEY,"Anonymous");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user != null)
            mDisplayName=user.getDisplayName();
        else
            mDisplayName="Anonymous";
        Log.d(RegisterActivity.TAG,"Username from database is "+mDisplayName);
    }

    private void sendMessage() {
        Log.d(RegisterActivity.TAG,"In sendMessage");
        // TODO: Grab the text the user typed in and push the message to Firebase
        String messageText=mInputText.getText().toString();
        InstantMessage message=new InstantMessage(mDisplayName,messageText);
        mDatabaseReference.child("messages").push().setValue(message);
        mInputText.setText("");

    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    protected void onStart(){
        super.onStart();
        mAdapter=new ChatListAdapter(this,mDatabaseReference,mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanUp();
    }

}

package com.divlovjaiswal.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private List<DataSnapshot> mSnapshotList;

    public ChatListAdapter(Context context, DatabaseReference databaseReference, String displayName) {
        mContext=context;
        mDatabaseReference = databaseReference.child("messages");
        mDatabaseReference.addChildEventListener(mChildEventListener);
        mDisplayName = displayName;
        mSnapshotList= new ArrayList<>();
    }

    private ChildEventListener mChildEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            mSnapshotList.add(snapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        DataSnapshot snapshot=mSnapshotList.get(i);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.chat_msg_row,viewGroup,false);
            ViewHolder viewHolder=new ViewHolder();
            viewHolder.author=view.findViewById(R.id.author);
            viewHolder.textMessage=view.findViewById(R.id.message);
            viewHolder.layoutParams=(LinearLayout.LayoutParams) viewHolder.author.getLayoutParams();
            view.setTag(viewHolder);
        }
        final InstantMessage instantMessage=getItem(i);
        ViewHolder viewHolder=(ViewHolder)view.getTag();
        boolean isMe=instantMessage.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe,viewHolder);
        viewHolder.textMessage.setText(instantMessage.getMessage());
        viewHolder.author.setText(instantMessage.getAuthor());
        return view;
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder viewHolder){
        if(isItMe){
            viewHolder.layoutParams.gravity= Gravity.END;
            viewHolder.author.setTextColor(Color.GREEN);
            viewHolder.textMessage.setBackgroundResource(R.drawable.bubble2);
        }
        else{
            viewHolder.layoutParams.gravity=Gravity.START;
            viewHolder.author.setTextColor(Color.BLUE);
            viewHolder.textMessage.setBackgroundResource(R.drawable.bubble1);
        }
        viewHolder.author.setLayoutParams(viewHolder.layoutParams);
        viewHolder.textMessage.setLayoutParams(viewHolder.layoutParams);

    }

    public void cleanUp(){
        mDatabaseReference.removeEventListener(mChildEventListener);
    }

    static class ViewHolder
    {
        TextView author;
        TextView textMessage;
        LinearLayout.LayoutParams layoutParams;
    }
}
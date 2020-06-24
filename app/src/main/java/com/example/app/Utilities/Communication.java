package com.example.app.Utilities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Messaging.ConversationRecyclerView;
import com.example.app.model.MessageObject;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Communication
{
    private Context context;
    private List<MessageObject> messageList;
    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private String chatId;
    private String userType;


    public Communication(Context context, List<MessageObject> messageList,
                         ConversationRecyclerView mAdapter, RecyclerView mRecyclerView,
                         String chatId, String userType) {
        this.context = context;
        this.messageList = messageList;
        this.mAdapter = mAdapter;
        this.chatId = chatId;
        this.userType = userType;
        this.mRecyclerView = mRecyclerView;
    }

    public void getMessages()
    {
        DatabaseReference mchatDB = FirebaseDatabase.getInstance().getReference().child("messages").child(chatId);
        mchatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    String text="", userType1="", image="", time="";
                    image = dataSnapshot.child("image").getValue(String.class);
                    text = dataSnapshot.child("text").getValue(String.class);
                    userType1 = dataSnapshot.child("userType").getValue(String.class);
                    time = dataSnapshot.child("time").getValue(String.class);

                    if(userType.equals(userType1))
                        userType1 = "ME";
                    else
                        userType1 = "YOU";

                    MessageObject messageObject = new MessageObject(dataSnapshot.getKey(), userType1, text, image, time);
                    messageList.add(messageObject);
                    mAdapter.notifyDataSetChanged();
                    //mAdapter.notifyItemInserted(messageList.size()-1); todo
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mAdapter.getItemCount()>0)
                                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                        }
                    }, 500);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void sendMessage(String message)
    {
        DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("messages").child(chatId).push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("image", "nullll");
        messageData.put("text", message);
        messageData.put("userType", userType);
        messageData.put("time", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE));
        newMessageDB.updateChildren(messageData);

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAdapter.getItemCount()>0)
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        }, 500);
    }
}

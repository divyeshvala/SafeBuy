package com.example.app.Messaging.Merchant;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app.Messaging.ChatData;
import com.example.app.Messaging.ConversationRecyclerView;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantConversationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText text;
    Toolbar toolbar;
    TextView title;
    private String merchantId;
    private FirebaseUser currentUser;
    private List<ChatData> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_conversation);

        String merchantName = getIntent().getStringExtra("customerName");
        merchantId = getIntent().getStringExtra("customerId");
        setupToolbarWithUpNav(R.id.toolbar, merchantName, R.drawable.ic_action_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerView(this, messagesList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }, 1000);

        text = (EditText) findViewById(R.id.et_message);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                    }
                }, 500);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Button send = (Button) findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!text.getText().equals(""))
                {
                    //sendMessage();
                }
            }
        });
    }

    private void sendMessage()
    {
        ChatData item = new ChatData();
        item.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE));
        item.setType("2");
        item.setText(text.getText().toString());
        messagesList.add(item);
        //mAdapter.addItem(messagesList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
        text.setText("");

        // send message
        final DatabaseReference sendMessageDB = FirebaseDatabase.getInstance()
                .getReference().child("merchants").child(merchantId).child("deliveryOrders").child(currentUser.getUid()).push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("details", item.getText());
        messageData.put("timeStamp", item.getTime());
        sendMessageDB.updateChildren(messageData);
    }

    public void setupToolbarWithUpNav(int toolbarId, String titlePage, @DrawableRes int res){
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(res);
        getSupportActionBar().setTitle("");
    }
}

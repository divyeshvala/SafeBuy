package com.example.app.Messaging.Customer;

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
import com.example.app.Messaging.ConversationRecyclerView;
import com.example.app.R;
import com.example.app.Utilities.Communication;
import com.example.app.model.MessageObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class CustomerConversationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    public ConversationRecyclerView mAdapter;
    private EditText messageText;
    Toolbar toolbar;
    TextView title;
    private String merchantId;
    private FirebaseUser currentUser;
    public List<MessageObject> messagesList = new ArrayList<>();
    private Communication communication;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        String merchantName = getIntent().getStringExtra("merchantName");
        merchantId = getIntent().getStringExtra("merchantId");
        String chatId = getIntent().getStringExtra("chatId");

        setupToolbarWithUpNav(R.id.toolbar, merchantName, R.drawable.ic_action_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerView(this, messagesList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAdapter.getItemCount()>0)
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        }, 1000);

        messageText = (EditText) findViewById(R.id.et_message);
        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mAdapter.getItemCount()>0)
                            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    }
                }, 500);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        communication = new Communication(CustomerConversationActivity.this,
                messagesList, mAdapter, "chatId", "customer");

        communication.getMessages();

        Button send = (Button) findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!messageText.getText().toString().equals(""))
                {
                    communication.sendMessage(messageText.getText().toString());
                    messageText.setText("");
                }
            }
        });
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

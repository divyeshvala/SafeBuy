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
import com.example.app.Messaging.ConversationRecyclerView;
import com.example.app.R;
import com.example.app.Utilities.Communication;
import com.example.app.model.MessageObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MerchantConversationActivity extends AppCompatActivity {

    public RecyclerView mRecyclerView;
    public ConversationRecyclerView mAdapter;
    private EditText text;
    Toolbar toolbar;
    TextView title;
    private String merchantId;
    private FirebaseUser currentUser;
    public List<MessageObject> messagesList = new ArrayList<>();
    private Communication communication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_conversation);

        String customerName = getIntent().getStringExtra("customerName");
        merchantId = getIntent().getStringExtra("customerId");
        String chatId = getIntent().getStringExtra("chatId");
        //setupToolbarWithUpNav(R.id.toolbar, merchantName, R.drawable.ic_action_back);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(customerName);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerView(this, messagesList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if(mRecyclerView.getAdapter().getItemCount()>0)
                {
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                }
            }
        }, 1000);

        text = (EditText) findViewById(R.id.et_message);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mRecyclerView.getAdapter().getItemCount()>0)
                        {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }
                }, 500);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        communication = new Communication(MerchantConversationActivity.this,
                messagesList, mAdapter, mRecyclerView, chatId, "merchant", customerName);

        communication.getMessages();

        FloatingActionButton send = findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!text.getText().toString().equals(""))
                {
                    communication.sendMessage(text.getText().toString());
                    text.setText("");
                }
            }
        });
    }
}

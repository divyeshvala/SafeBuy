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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MerchantConversationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
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

        communication = new Communication(MerchantConversationActivity.this,
                messagesList, mAdapter, "todo", "merchant");
        // todo:
        communication.getMessages();

        Button send = (Button) findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!text.getText().toString().equals(""))
                {
                    sendMessage();
                }
            }
        });
    }

    private void sendMessage()
    {
        // update UI
        MessageObject item = new MessageObject();
        item.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE));
        item.setUserType("ME");
        item.setText(text.getText().toString());
        messagesList.add(item);
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getItemCount()>0)
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1);
        text.setText("");

        //todo:
        communication.sendMessage(text.getText().toString());
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

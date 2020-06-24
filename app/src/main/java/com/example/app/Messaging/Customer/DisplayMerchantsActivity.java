package com.example.app.Messaging.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.app.Messaging.Chat;
import com.example.app.Messaging.ChatAdapter;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayMerchantsActivity extends AppCompatActivity implements ChatAdapter.ViewHolder.ClickListener {

    private static final String TAG = "DisplayMerchants";
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private TextView tv_selection;
    private List<Chat> merchantsList;
    private String myUid;
    Toolbar toolbar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_merchants);

        setupToolbar(R.id.toolbar, "Nearby Merchants");

        merchantsList = new ArrayList<>();
        tv_selection = (TextView) findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayMerchantsActivity.this));
        mAdapter = new ChatAdapter(DisplayMerchantsActivity.this, merchantsList,this);
        mRecyclerView.setAdapter (mAdapter);

        myUid = FirebaseAuth.getInstance().getUid();

        getMerchantsList();
    }

    private void getMerchantsList()
    {
        final DatabaseReference merchantsDB = FirebaseDatabase.getInstance().getReference()
                .child("merchants");

        merchantsDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Chat chat = new Chat();
                chat.setTime("");
                chat.setName(dataSnapshot.child("name").getValue(String.class));
                chat.setImage(R.drawable.user2);
                chat.setOnline(true);
                chat.setLastChat(dataSnapshot.child("phone").getValue(String.class));
                chat.setUserId(dataSnapshot.getKey());
                merchantsList.add(chat);
                //mAdapter.notifyDataSetChanged();
                mAdapter.notifyItemInserted(merchantsList.size()-1); //todo
                Log.i(TAG, "Merchant :"+chat.getName());
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

    @Override
    public void onItemClicked(final int position)
    {
        // see if there is already chatId with this merchant. If not then create it.
        final DatabaseReference mDB = FirebaseDatabase.getInstance().getReference()
                .child("customers").child(myUid).child("chatIds");

        final String merchantId = merchantsList.get(position).getUserId();

        mDB.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean exists = false;
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot chatIdSnapshot : dataSnapshot.getChildren())
                    {
                        if(chatIdSnapshot.child("merchantId").getValue(String.class).equals(merchantId))
                        {
                            exists = true;
                            String mChatId = chatIdSnapshot.getKey();
                            GoToNextActivity(merchantsList.get(position), mChatId);
                        }
                    }
                }
                if(!exists)
                {
                    DatabaseReference newChatId = mDB.push();
                    Map<String, Object> data = new HashMap<>();
                    data.put("merchantId", merchantsList.get(position).getUserId());
                    data.put("name", merchantsList.get(position).getName());
                    newChatId.updateChildren(data);

                    DatabaseReference merchantDB = FirebaseDatabase.getInstance().getReference()
                            .child("merchants").child(merchantsList.get(position).getUserId()).child("chatIds").child(newChatId.getKey());

                    data = new HashMap<>();
                    data.put("customerId", myUid);
                    data.put("name", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    merchantDB.updateChildren(data);

                    GoToNextActivity(merchantsList.get(position), newChatId.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void GoToNextActivity(Chat merchant, String mChatId)
    {
        Intent intent = new Intent(DisplayMerchantsActivity.this, CustomerConversationActivity.class);
        intent.putExtra("merchantName", merchant.getName());
        intent.putExtra("merchantId", merchant.getUserId());
        intent.putExtra("chatId", mChatId);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection (position);
        if (mAdapter.getSelectedItemCount()>0){
            tv_selection.setVisibility(View.VISIBLE);
        }else
            tv_selection.setVisibility(View.GONE);

        runOnUiThread(new Runnable() {
            public void run()
            {
                tv_selection.setText("Delete ("+mAdapter.getSelectedItemCount()+")");
            }
        });
    }

    public final void setupToolbar(int toolbarId, String titlePage){
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setTitle("");
    }
}

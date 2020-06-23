package com.example.app.Messaging.Merchant;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DisplayCustomersActivity extends AppCompatActivity implements ChatAdapter.ViewHolder.ClickListener{

    private static final String TAG = "DisplayMerchants";
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private TextView tv_selection;
    private List<Chat> merchantsList;
    Toolbar toolbar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_customers);

        setupToolbar(R.id.toolbar, "Nearby Merchants");

        merchantsList = new ArrayList<>();
        tv_selection = (TextView) findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayCustomersActivity.this));
        mAdapter = new ChatAdapter(DisplayCustomersActivity.this, merchantsList,this);
        mRecyclerView.setAdapter (mAdapter);
        getMerchantsList();
    }

    private void getMerchantsList()
    {
        final DatabaseReference merchantsDB = FirebaseDatabase.getInstance().getReference()
                .child("merchants").child("merchantId1").child("todo"); //todo

        merchantsDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Chat chat = new Chat();
                chat.setmTime("5:04pm");
                chat.setName(dataSnapshot.child("name").getValue(String.class));
                chat.setImage(R.drawable.user2);
                chat.setOnline(true);
                chat.setLastChat(dataSnapshot.child("phone").getValue(String.class));
                chat.setId(dataSnapshot.getKey());
                merchantsList.add(chat);
                mAdapter.notifyDataSetChanged();
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
    public void onItemClicked(int position)
    {
        Intent intent = new Intent(DisplayCustomersActivity.this, MerchantConversationActivity.class);
        intent.putExtra("customerName", merchantsList.get(position).getName());
        intent.putExtra("customerId", merchantsList.get(position).getId());
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

    public final void setupToolbar(int toolbarId, String titlePage)
    {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setTitle("");
    }
}

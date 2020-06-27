package com.example.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.Messaging.Chat;
import com.example.app.Messaging.Merchant.MerchantConversationActivity;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class FragmentCustomers extends Fragment  {

    private static final String TAG = "DisplayCustomers";
    private ListAdapter mListadapter;
    private ProgressBar progressBar;
    private ArrayList<Chat> customersList;
    private String myUid;

    public static FragmentCustomers newInstance()
    {
        return new FragmentCustomers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);

        customersList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mListadapter = new ListAdapter(customersList);
        recyclerView.setAdapter(mListadapter);
        myUid = FirebaseAuth.getInstance().getUid();

        getCustomersList();

        return view;
    }

    private void getCustomersList()
    {
        final DatabaseReference merchantsDB = FirebaseDatabase.getInstance().getReference()
                .child("merchants").child(myUid).child("chatIds");

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
                chat.setUserId(dataSnapshot.child("customerId").getValue(String.class));
                chat.setChatId(dataSnapshot.getKey());
                customersList.add(chat);
                mListadapter.notifyDataSetChanged(); //todo
                Log.i(TAG, "Merchant :"+chat.getName());
                progressBar.setVisibility(View.INVISIBLE);
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

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<Chat> dataList;

        private ListAdapter(ArrayList<Chat> data) {
            this.dataList = data;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewAddress;
            LinearLayout cardLayout;

            private ViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.id_userName);
                this.textViewAddress = (TextView) itemView.findViewById(R.id.id_address);
                this.cardLayout = itemView.findViewById(R.id.id_chatLayout);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textViewName.setText(dataList.get(position).getName());
            holder.textViewAddress.setText(dataList.get(position).getLastChat());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    Intent intent = new Intent(getActivity(), MerchantConversationActivity.class);
                    intent.putExtra("customerName", dataList.get(position).getName());
                    intent.putExtra("customerId", dataList.get(position).getUserId());
                    intent.putExtra("chatId", dataList.get(position).getChatId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}
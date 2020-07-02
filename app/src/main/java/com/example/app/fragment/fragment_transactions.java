package com.example.app.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class fragment_transactions extends Fragment {

    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private ArrayList<Transaction> list;

    public fragment_transactions() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        recyclerView = view.findViewById(R.id.id_suggestionsRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        mAdapter = new ListAdapter(list);
        recyclerView.setAdapter(mAdapter);

        getAllTransactions();

        return view;
    }

    private void getAllTransactions()
    {
        String myUid = FirebaseAuth.getInstance().getUid();

        final SharedPreferences settings = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);



        FirebaseDatabase.getInstance().getReference().child(settings.getString("userType", "customer")+"s")
                .child(myUid).child("transactions").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Transaction transaction = new Transaction();
                transaction.setAmount(dataSnapshot.child("amount").getValue(String.class));
                transaction.setDate(dataSnapshot.child("date").getValue(String.class));
                transaction.setMerchantId(dataSnapshot.child("id").getValue(String.class));
                transaction.setMerchantName(dataSnapshot.child("name").getValue(String.class));
                transaction.setTime(dataSnapshot.child("time").getValue(String.class));

                list.add(transaction);
                mAdapter.notifyDataSetChanged();
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
        private ArrayList<Transaction> list;

        private ListAdapter(ArrayList<Transaction> data) {
            this.list = data;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            private ViewHolder(View itemView) {
                super(itemView);
                this.textView = itemView.findViewById(R.id.name);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_transactions, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textView.setText(list.get(position).getMerchantName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
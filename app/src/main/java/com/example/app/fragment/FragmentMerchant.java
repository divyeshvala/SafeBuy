package com.example.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Activities.MapsActivity;
import com.example.app.CustomUI.Divider;
import com.example.app.Messaging.Chat;
import com.example.app.Messaging.ChatAdapter;
import com.example.app.Messaging.Customer.CustomerConversationActivity;
import com.example.app.R;
import com.example.app.model.ATMObject;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentMerchant extends Fragment {

    private static final String TAG = "FragmentMerchant";
    private ListAdapter mListadapter;
    private ProgressBar progressBar;
    private ArrayList<Chat> merchantsList;
    private String myUid;

    public static FragmentMerchant newInstance()
    {
        return new FragmentMerchant();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant, container, false);

        merchantsList = new ArrayList<>();
        RecyclerView recyclerViewNearYou = (RecyclerView) view.findViewById(R.id.recyclerViewNearYou);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNearYou.setLayoutManager(layoutManager);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        merchantsList = new ArrayList<>();
        mListadapter = new ListAdapter(merchantsList);
        recyclerViewNearYou.setAdapter(mListadapter);
        myUid = FirebaseAuth.getInstance().getUid();

        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.google_maps_key));
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        getMerchantsList();

        return view;
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
                mListadapter.notifyItemInserted(merchantsList.size()-1); //todo
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

    private void GoToNextActivity(Chat merchant, String mChatId)
    {
        Intent intent = new Intent(getActivity(), CustomerConversationActivity.class);
        intent.putExtra("merchantName", merchant.getName());
        intent.putExtra("merchantId", merchant.getUserId());
        intent.putExtra("chatId", mChatId);
        startActivity(intent);
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<Chat> dataList;

        private ListAdapter(ArrayList<Chat> data) {
            this.dataList = data;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewAddress;
            TextView textViewDistance;
            //TextView isOpen;
            TextView directions;
            TextView shop;

            private ViewHolder(View itemView) {
                super(itemView);
                this.textViewName = itemView.findViewById(R.id.name);
                this.textViewAddress = itemView.findViewById(R.id.address);
                this.textViewDistance = itemView.findViewById(R.id.distance);
                //this.isOpen = itemView.findViewById(R.id.open);
                this.directions = itemView.findViewById(R.id.direction);
                this.shop = itemView.findViewById(R.id.shop);
            }
        }

        @NonNull
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_merchant, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textViewName.setText(dataList.get(position).getName());
            holder.textViewAddress.setText("404, Southside lane");
            holder.textViewDistance.setText("3km");
            //holder.isOpen.setText("open");

            //if(!dataList.get(position).isSafe())
            //holder.atmCardLayout.setBackground(getResources().getDrawable(R.drawable.red_back_up_round));

            holder.shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getActivity(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();

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
            });

            holder.directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}
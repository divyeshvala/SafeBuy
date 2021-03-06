package com.example.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.Activities.MapsActivity;
import com.example.app.Messaging.Customer.CustomerConversationActivity;
import com.example.app.R;
import com.example.app.Utilities.GetNearbyMerchants;
import com.example.app.model.LocationObject;
import com.example.app.model.MerchObject;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FragmentMerchants extends Fragment
{
    private static final String TAG = "FragmentMerchants";
    private RecyclerView recyclerViewNearYou;
    private ListAdapter mListadapter;
    private ProgressBar progressBar;
    private ArrayList<MerchObject> dataList;
    private static final int containmentZoneRadius = 100;
    public ArrayList<MerchObject> MerchantsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyMerchants getMerchants;
    private static LatLng mLatLng;
    private ArrayList<String> containmentZoneLatLngs;
    private String myUid;
    FilterBottomSheetFragment bottomSheetFragment;
    private TextView progressMessage;
    private TextView noMerchants;
    ImageView search_icon;
    private Address address;
    private String placename;
    private MerchObject dummyMerchant = new MerchObject(40.714181, -74.015568, "Red Wheelbarrow", "Restaurant", "3km", "EGWhb2mBweNjiq5x4Z0wGQi4EZo1");
    public static List<String> categories = new ArrayList<>();
    public static String distanceText;
    public static String distance_unit;
    public static boolean isFilterChanged;

    public static FragmentMerchants newInstance()
    {
        return new FragmentMerchants();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant, container, false);

        Log.i(TAG, "inside oncreate");
        FloatingActionButton fab = view.findViewById(R.id.fab);
        address=null;

        bottomSheetFragment = new FilterBottomSheetFragment();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        setDefaultValues();
        dummyMerchant.setPan("4761360055652118");
        dummyMerchant.setAddress("Financial center, Vesey Street");
        noMerchants = view.findViewById(R.id.id_noMerchants);
        progressMessage = view.findViewById(R.id.id_progressMessage);
        recyclerViewNearYou = view.findViewById(R.id.recyclerViewNearYou);
        search_icon = view.findViewById(R.id.searching_icon);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNearYou.setLayoutManager(layoutManager);
        progressBar = view.findViewById(R.id.progress_bar);
        dataList = new ArrayList<>();
        mListadapter = new ListAdapter(dataList);
        recyclerViewNearYou.setAdapter(mListadapter);
        containmentZoneLatLngs = new ArrayList<>();

        myUid = FirebaseAuth.getInstance().getUid();

        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_MERCHANTS_LIST");
        Objects.requireNonNull(getActivity()).registerReceiver(merchantsListReceiver, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter("ACTION_FILTER_APPLIED");
        Objects.requireNonNull(getActivity()).registerReceiver(filterReceiver, intentFilter2);

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
                System.out.println("Inside place selection bar");

                noMerchants.setVisibility(View.INVISIBLE);
                MerchantsList = new ArrayList<>();
                containmentZonesList = new ArrayList<>();
                getMerchants = new GetNearbyMerchants(getActivity(), MerchantsList, containmentZonesList, distanceText);

                address = null;
                try {
                    Geocoder geocoder = new Geocoder(getActivity(),
                            Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(
                            place.getName(),
                            1
                    );
                    if(addresses.size()>0){
                        address = addresses.get(0);
                        mLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Invalid Address", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                placename=place.getName();
                getMerchants();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        return view;
    }

    private void getMerchants()
    {
        if(address!=null)
        {
            progressBar.setVisibility(View.VISIBLE);
            noMerchants.setVisibility(View.GONE);
            progressMessage.setVisibility(View.VISIBLE);
            progressMessage.setText("Searching merchants near " + placename + "...");
            final Address finalAddress = address;
            dataList.clear();
            mListadapter.notifyDataSetChanged();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getMerchants.getListOfMerchants(placename, String.valueOf(finalAddress.getLatitude()), String.valueOf(finalAddress.getLongitude()),
                            categories, distanceText, distance_unit);
                }
            }).start();
        }
        else{
            System.out.println("Address not set");
            noMerchants.setVisibility(View.GONE);
            progressMessage.setVisibility(View.VISIBLE);
            progressMessage.setText("Please enter a valid location.");
        }
    }
    private void GoToNextActivity(MerchObject merchant, String mChatId)
    {
        Intent intent = new Intent(getActivity(), CustomerConversationActivity.class);
        intent.putExtra("merchantName", merchant.getStoreName());
        intent.putExtra("merchantId", merchant.getMerchantId());
        intent.putExtra("chatId", mChatId);
        intent.putExtra("merchantPan", merchant.getPan());
        startActivity(intent);
    }

    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            getMerchants();
            bottomSheetFragment.dismiss();
        }
    };

    private final BroadcastReceiver merchantsListReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {

            dataList.clear();
            mListadapter.notifyDataSetChanged();

            if(intent.getStringExtra("status").equals("failed"))
            {
                noMerchants.setVisibility(View.VISIBLE);
                noMerchants.setText("Some error occured please try again.");
                progressBar.setVisibility(View.INVISIBLE);
                progressMessage.setVisibility(View.INVISIBLE);
                return;
            }

            dataList.add(dummyMerchant);
            findSafeAndUnsafeMerchants(MerchantsList, containmentZonesList);

        }
    };

    private void findSafeAndUnsafeMerchants(ArrayList<MerchObject> MerchantsList, ArrayList<LocationObject> containmentZonesList)
    {
        // Get LatLngs of containmentZones
        for(LocationObject zoneObject : containmentZonesList)
        {
            containmentZoneLatLngs.add(zoneObject.getLatitude()+"_"+zoneObject.getLongitude());
        }

        boolean isInContainment;
        // check which Merchants are in safe area.
        for(MerchObject merchObject : MerchantsList)
        {
            String address = getAddress(merchObject.getLat(), merchObject.getLon());
            merchObject.setAddress(address);

            isInContainment = false;
            for( LocationObject zoneObject : containmentZonesList )
            {
                float distanceBTW = getDistance(merchObject.getLat(), merchObject.getLon(),
                        zoneObject.getLatitude(), zoneObject.getLongitude());

                if(distanceBTW<=containmentZoneRadius)  // in containment zone.
                {
                    isInContainment = true;
                    dataList.add(merchObject);
                    break;
                }
            }
            if(!isInContainment)  // outside containment zone
            {
                dataList.add(merchObject);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        progressMessage.setVisibility(View.INVISIBLE);
        search_icon.setVisibility(View.INVISIBLE);
        mListadapter.notifyDataSetChanged();
    }

    private String getAddress(double latitude, double longitude)
    {
        Address address = null;
        try {
            Geocoder geocoder = new Geocoder(getActivity(),
                    Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address!=null)
            return address.getFeatureName()+", "+address.getSubLocality()+", "+address.getLocality();
        return "";
    }

    private float getDistance(double latitude, double longitude, double latitude1, double longitude1)
    {
        Location start = new Location("");
        start.setLatitude(latitude);
        start.setLongitude(longitude);
        Location dest = new Location("");
        dest.setLatitude(latitude1);
        dest.setLongitude(longitude1);

        return start.distanceTo(dest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            Objects.requireNonNull(getActivity()).unregisterReceiver(filterReceiver);
            Objects.requireNonNull(getActivity()).unregisterReceiver(merchantsListReceiver);
        }catch (Exception e){ e.printStackTrace(); }
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<MerchObject> dataList;

        private ListAdapter(ArrayList<MerchObject> data) {
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_merchant, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textViewName.setText(dataList.get(position).getStoreName());
            holder.textViewAddress.setText(dataList.get(position).getAddress());
            holder.textViewDistance.setText(dataList.get(position).getDistanceDesc());

            holder.shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    // see if there is already chatId with this merchant. If not then create it.
                    final DatabaseReference mDB = FirebaseDatabase.getInstance().getReference()
                            .child("customers").child(myUid).child("chatIds");

                    final String merchantId = dataList.get(position).getMerchantId();

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
                                        GoToNextActivity(dataList.get(position), mChatId);
                                    }
                                }
                            }
                            if(!exists)
                            {
                                final DatabaseReference newChatId = mDB.push();
                                Map<String, Object> data = new HashMap<>();
                                data.put("merchantId", dataList.get(position).getMerchantId());
                                data.put("name", dataList.get(position).getStoreName());
                                newChatId.updateChildren(data);

                                FirebaseDatabase.getInstance().getReference()
                                        .child("customers").child(myUid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Map<String, Object> data1 = new HashMap<>();
                                        data1.put("customerId", myUid);
                                        data1.put("name", dataSnapshot.child("firstName").getValue(String.class)+" "+dataSnapshot.child("lastName").getValue(String.class));

                                        DatabaseReference merchantDB = FirebaseDatabase.getInstance().getReference()
                                                .child("merchants").child(dataList.get(position).getMerchantId()).child("chatIds").child(newChatId.getKey());
                                        merchantDB.updateChildren(data1);

                                        GoToNextActivity(dataList.get(position), newChatId.getKey());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
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
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("queryType", "Merchant");
                    intent.putExtra("originLatitude", mLatLng.latitude);
                    intent.putExtra("originLongitude", mLatLng.longitude);
                    intent.putExtra("destinationLatitude", dataList.get(position).getLat());
                    intent.putExtra("destinationLongitude", dataList.get(position).getLon());
                    intent.putStringArrayListExtra("containmentZoneLatLngs", containmentZoneLatLngs);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    private void setDefaultValues()
    {
        isFilterChanged = false;
        distanceText="5";
        distance_unit="KM";
        categories.add("5411");
        categories.add("5912");
        categories.add("8062");
        categories.add("5812");
        categories.add("5814");
        categories.add("5462");
        categories.add("5137");
        categories.add("7230");
    }
}
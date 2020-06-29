package com.example.app.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Activities.MapsActivity;
import com.example.app.Messaging.Chat;
import com.example.app.Messaging.Customer.CustomerConversationActivity;
import com.example.app.R;
import com.example.app.Utilities.GetNearbyATMs;
import com.example.app.Utilities.GetNearbyMerchants;
import com.example.app.Utilities.MyLocationListener;
import com.example.app.model.ATMObject;
import com.example.app.model.LocationObject;
import com.example.app.model.MerchObject;
import com.example.app.model.Merchant;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentMerchants extends Fragment 
{
    private static final String TAG = "FragmentMerchants";
    private RecyclerView recyclerViewNearYou;
    private ListAdapter mListadapter;
    private ProgressBar progressBar;
    private ArrayList<MerchObject> dataList;
    private static final int containmentZoneRadius = 100;
    public ArrayList<MerchObject> nearbyMerchantsList;
    public ArrayList<LocationObject> nearbycontainmentZonesList;
    public ArrayList<MerchObject> MerchantsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyMerchants getNearbyMerchants, getMerchants;
    private boolean isUsingMyLocation;
    private LatLng mLatLng;
    private ArrayList<String> containmentZoneLatLngs;
    private String myUid;
    
    String[] category = new String[] {"Fast Food Restaurants", "Pharmacies", "Book Stores"};
    
    public static FragmentMerchants newInstance()
    {
        return new FragmentMerchants();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant, container, false);


        Log.i(TAG, "inside onCreate.");

        recyclerViewNearYou = view.findViewById(R.id.recyclerViewNearYou);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNearYou.setLayoutManager(layoutManager);
        isUsingMyLocation = true;
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        dataList = new ArrayList<>();
        mListadapter = new ListAdapter(dataList);
        recyclerViewNearYou.setAdapter(mListadapter);

        containmentZoneLatLngs = new ArrayList<>();

        nearbyMerchantsList = new ArrayList<>();
        nearbycontainmentZonesList = new ArrayList<>();
        getNearbyMerchants = new GetNearbyMerchants(getActivity(), nearbyMerchantsList, nearbycontainmentZonesList,
                isUsingMyLocation, "2000");

        myUid = FirebaseAuth.getInstance().getUid();

        IntentFilter intentFilter2 = new IntentFilter("ACTION_FILTER_APPLIED");
        getActivity().registerReceiver(filterReceiver, intentFilter2);

        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_MERCHANTS_LIST");
        getActivity().registerReceiver(ATMListReceiver, intentFilter1);

        IntentFilter intentFilter = new IntentFilter("ADDRESS_FOUND");
        getActivity().registerReceiver(locationReceiver, intentFilter);


        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.google_maps_key));
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME)); //todo

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress());

                try{
                    getActivity().unregisterReceiver(locationReceiver);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                isUsingMyLocation = false;

                MerchantsList = new ArrayList<>();
                containmentZonesList = new ArrayList<>();

                getMerchants = new GetNearbyMerchants(getActivity(), MerchantsList, containmentZonesList,
                        isUsingMyLocation, "1000"); //todo

                Address address = null;
                try {
                    Geocoder geocoder = new Geocoder(getActivity(),
                            Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(
                            place.getName(),
                            1
                    );
                    address = addresses.get(0);
                    mLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(address!=null){
                    final Address finalAddress = address;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //todo
                            getMerchants.getListOfMerchants(place.getName(), finalAddress.getLatitude(), finalAddress.getLongitude(),
                                    5814, 1000, "m");
                        }
                    }).start();
                }
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        setupLocationAPI();

        return view;
    }

    private void GoToNextActivity(MerchObject merchant, String mChatId)
    {
        Intent intent = new Intent(getActivity(), CustomerConversationActivity.class);
        intent.putExtra("merchantName", merchant.getStoreName());
        intent.putExtra("merchantId", merchant.getMerchantId());
        intent.putExtra("chatId", mChatId);
        startActivity(intent);
    }

    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Inside filter receiver");
            String distance = intent.getStringExtra("distance");
        }
    };

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "inside locationReceiver, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
            final double lat = intent.getDoubleExtra("latitude", 0);
            final double lon = intent.getDoubleExtra("longitude", 0);
            final String addressLine = intent.getStringExtra("addressLine");
            try{
                getActivity().unregisterReceiver(locationReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(isUsingMyLocation) {
                mLatLng = new LatLng(lat, lon);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        getNearbyMerchants.getListOfMerchants(addressLine, lat, lon, 5814, 1000, "m");
                    }
                }).start();
            }
        }
    };

    private final BroadcastReceiver ATMListReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "inside ATMListReceiver, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));

            dataList.clear();
            mListadapter.notifyDataSetChanged();

            if(intent.getBooleanExtra("isUsingMyLocation", true) && isUsingMyLocation)
            {
                findSafeAndUnsafeMerchants(nearbyMerchantsList, nearbycontainmentZonesList);
            }
            else if(!intent.getBooleanExtra("isUsingMyLocation", true))
            {
                Log.i(TAG, "custom location");
                findSafeAndUnsafeMerchants(MerchantsList, containmentZonesList);
            }
        }
    };

    private void findSafeAndUnsafeMerchants(ArrayList<MerchObject> MerchantsList, ArrayList<LocationObject> containmentZonesList)
    {
        // Get LatLngs of containmentZones
        for(LocationObject zoneObject : containmentZonesList)
        {
            containmentZoneLatLngs.add(zoneObject.getLatitude()+"_"+zoneObject.getLongitude());
        }

        if(MerchantsList.size()==0){
            dataList.add(new MerchObject(0, 0, "There are no Merchants near you.", "", "", ""));
        }
        boolean isInContainment;
        // check which Merchants are in safe area.
        for(MerchObject merchObject : MerchantsList)
        {
            float distance = getDistance(merchObject.getLat(), merchObject.getLon(),
                    mLatLng.latitude, mLatLng.longitude)/1000;

            String address = getAddress(merchObject.getLat(), merchObject.getLon());

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
            if(containmentZonesList.size()==0)
            {
                dataList.add(merchObject);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);

        Collections.sort(dataList, new CustomComparator());
        mListadapter.notifyDataSetChanged();
    }

    public class CustomComparator implements Comparator<MerchObject> {
        @Override
        public int compare(MerchObject o1, MerchObject o2)
        {
            Log.i(TAG, "inside compare");
            // todo:
//            if(o1.isSafe() && o2.isSafe())
//            {
//                return o1.getDistance()<o2.getDistance() ? 1 : 0;
//            }
//
//            return o1.isSafe() ? 1 : 0;
            return 0;
        }
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

    private void setupLocationAPI()
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener(getActivity(), "ADDRESS_FOUND");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 50000, 5, locationListener);
            }
        }
        else
        {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 50000, 5, locationListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            getActivity().unregisterReceiver(filterReceiver);
            getActivity().unregisterReceiver(locationReceiver);
            getActivity().unregisterReceiver(ATMListReceiver);
        }catch (Exception e){ e.printStackTrace(); }
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<MerchObject> dataList;

        public ListAdapter(ArrayList<MerchObject> data) {
            this.dataList = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_merchant, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textViewName.setText(dataList.get(position).getStoreName());
            holder.textViewAddress.setText(dataList.get(position).getLat()+":"+dataList.get(position).getLon());
            holder.textViewDistance.setText(dataList.get(position).getDistanceDesc());
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
    
}
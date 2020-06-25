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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.Utilities.GetNearbyATMs;
import com.example.app.Utilities.LocationObject;
import com.example.app.Utilities.MyLocationListener;
import com.example.app.model.Merchant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentNearYou extends Fragment  {

    private static final String TAG = "FragmentNearYou";
    private RecyclerView recyclerViewNearYou;
    private ListAdapter mListadapter;

    public static FragmentNearYou newInstance() {
        return new FragmentNearYou();
    }

    private ArrayList<Merchant> dataList;
    private static final int containmentZoneRadius = 1000;
    public ArrayList<LocationObject> nearbyATMsList;
    public ArrayList<LocationObject> nearbycontainmentZonesList;
    public ArrayList<LocationObject> ATMsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyATMs getNearbyATMs, getATMs;
    private boolean isUsingMyLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_you, container, false);

        Log.i(TAG, "inside onCreate.");

        recyclerViewNearYou = (RecyclerView) view.findViewById(R.id.recyclerViewNearYou);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNearYou.setLayoutManager(layoutManager);
        isUsingMyLocation = true;

        dataList = new ArrayList<>();
        mListadapter = new ListAdapter(dataList);
        recyclerViewNearYou.setAdapter(mListadapter);

        nearbyATMsList = new ArrayList<>();
        nearbycontainmentZonesList = new ArrayList<>();
        getNearbyATMs = new GetNearbyATMs(getActivity(), nearbyATMsList, nearbycontainmentZonesList,
                isUsingMyLocation, "2000");

        IntentFilter intentFilter2 = new IntentFilter("ACTION_FILTER_APPLIED");
        getActivity().registerReceiver(filterReceiver, intentFilter2);

        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_ATM_LIST");
        getActivity().registerReceiver(ATMListReceiver, intentFilter1);

        IntentFilter intentFilter = new IntentFilter("ADDRESS_FOUND");
        getActivity().registerReceiver(locationReceiver, intentFilter);
        setupLocationAPI();
        return view;
    }

    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Inside filter receiver");
            String distance = intent.getStringExtra("distance");
            final String addressLine = intent.getStringExtra("addressLine");
            try{
                getActivity().unregisterReceiver(locationReceiver);
                getActivity().unregisterReceiver(filterReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
            isUsingMyLocation = false;

            if(distance.equals(""))
                distance = "2000";

            ATMsList = new ArrayList<>();
            containmentZonesList = new ArrayList<>();
            getATMs = new GetNearbyATMs(getActivity(), ATMsList, containmentZonesList,
                    isUsingMyLocation, distance);

            Address address = null;
            try {
                Geocoder geocoder = new Geocoder(getActivity(),
                        Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(
                        addressLine,
                        1
                );
                address = addresses.get(0);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(address!=null){
                final Address finalAddress = address;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getATMs.getListOfATMs(addressLine, finalAddress.getLatitude(), finalAddress.getLongitude());
                    }
                }).start();
            }
        }
    };

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            final double lat = intent.getDoubleExtra("latitude", 0);
            final double lon = intent.getDoubleExtra("longitude", 0);
            final String addressLine = intent.getStringExtra("addressLine");
            getActivity().unregisterReceiver(locationReceiver);

            if(isUsingMyLocation) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        getNearbyATMs.getListOfATMs(addressLine, lat, lon);
                    }
                }).start();
            }
        }
    };

    private final BroadcastReceiver ATMListReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "inside ATMListReceiver, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
            getActivity().unregisterReceiver(ATMListReceiver);

            if(intent.getBooleanExtra("isUsingMyLocation", true)&& isUsingMyLocation)
            {
                findSafeAndUnsafeATMs(nearbyATMsList, nearbycontainmentZonesList);
            }
            else
            {
                findSafeAndUnsafeATMs(ATMsList, containmentZonesList);
            }
        }
    };

    private void findSafeAndUnsafeATMs(ArrayList<LocationObject> ATMsList, ArrayList<LocationObject> containmentZonesList)
    {
        if(ATMsList.size()==0){
            //unsafeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
            //safeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
            dataList.add(new Merchant("There are no ATMs near you.", "", true, ""));
            mListadapter.notifyDataSetChanged();
        }
        // check which ATMs are in safe area.
        for(LocationObject atmObject : ATMsList)
        {
            for(LocationObject zoneObject : containmentZonesList )
            {
                float distance = getDistance(atmObject.getLatitude(), atmObject.getLongitude(),
                        zoneObject.getLatitude(), zoneObject.getLongitude());

                if(distance<=containmentZoneRadius)  // in containment zone.
                {
                    dataList.add(new Merchant(atmObject.getPlaceName(), atmObject.getLatitude()+":"+atmObject.getLongitude(), true, "Safe"));
                    mListadapter.notifyDataSetChanged();
                }
                else
                {
                    dataList.add(new Merchant(atmObject.getPlaceName(), atmObject.getLatitude()+":"+atmObject.getLongitude(), true, "Unsafe"));
                    mListadapter.notifyDataSetChanged();                }
            }
            //if(containmentZonesList.size()==0)
              //  safeList.setText(safeList.getText().toString()+"\n"+atmObject.getPlaceName());
        }
        //progressBar.setVisibility(View.INVISIBLE);
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
        LocationListener locationListener = new MyLocationListener(getActivity());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
            }
        }
        else
        {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
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
        private ArrayList<Merchant> dataList;

        public ListAdapter(ArrayList<Merchant> data) {
            this.dataList = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewAddress;
            TextView textViewOpen;
            TextView textViewDistance;

            public ViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.name);
                this.textViewAddress = (TextView) itemView.findViewById(R.id.address);
                this.textViewOpen = (TextView) itemView.findViewById(R.id.open);
                this.textViewDistance = (TextView) itemView.findViewById(R.id.distance);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_merchant, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {
            holder.textViewName.setText(dataList.get(position).getName());
            holder.textViewAddress.setText(dataList.get(position).getAddress());
            holder.textViewOpen.setText(dataList.get(position).isOpen() ? "true" : "false");
            holder.textViewDistance.setText(dataList.get(position).getDistance());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}

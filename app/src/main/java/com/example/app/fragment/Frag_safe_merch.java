package com.example.app.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.Utilities.GetNearbyMerchs;
import com.example.app.model.LocationObject;
import com.example.app.Utilities.MyLocationListener;
import com.example.app.model.Merchant;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_safe_merch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_safe_merch extends Fragment {

    private RecyclerView rcview;
    private LocationObject mLocation;
    public ArrayList<LocationObject> MerchsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyMerchs getNearbyMerchs;
    private FragmentATM fgny;
    private String send2;
    private ArrayList<Merchant> dataList;
    private ListAdapter mListadapter;
    public ArrayList<LocationObject> nearbycontainmentZonesList;
    public ArrayList<LocationObject> nearbyMerchsList;
    private boolean isUsingMyLocation;
    private LatLng mLatLng;
    public Frag_safe_merch() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_safe_merch.
     */
    //cfm
    // TODO: Rename and change types and number of parameters
    public static Frag_safe_merch newInstance() {
        return new Frag_safe_merch();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_frag_safe_merch, container, false);
        Log.i(TAG, "inside onCreate.");
        dataList = new ArrayList<>();
        mListadapter = new ListAdapter(dataList);
        rcview=(RecyclerView)view.findViewById(R.id.rcviewid);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcview.setLayoutManager(layoutManager);
        MerchsList = new ArrayList<>();
        containmentZonesList = new ArrayList<>();
        isUsingMyLocation = true;
        getNearbyMerchs = new GetNearbyMerchs(getActivity(), nearbyMerchsList, nearbycontainmentZonesList,
                isUsingMyLocation, "2000");
        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_MERCH_LIST");
        getActivity().registerReceiver(MerchListReceiver, intentFilter1);
        send2="ADDRESS_FOUND2";
        IntentFilter intentFilter = new IntentFilter(send2);
        getActivity().registerReceiver(locationReceiver, intentFilter);
        setupLocationAPI(send2);
        return view;
    }
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "inside locationReceiver234, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
            final double lat = intent.getDoubleExtra("latitude", 0);
            final double lon = intent.getDoubleExtra("longitude", 0);
            final String addressLine = intent.getStringExtra("addressLine");
            try{
                getActivity().unregisterReceiver(locationReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }

            //mLocation = new LocationObject(lat, lon, addressLine);

//            if(doesUserWantOurLocationsATMs)
//            {
//                doesUserWantOurLocationsATMs = false;
//            }
//
            //getNearbyMerchs.getListOfMerchs("vile parle, mumbai", 19.0968, 72.8517);
            //getNearbyMerchs.getListOfMerchs(addressLine,lat,lon);
            if(isUsingMyLocation) {
                Log.i(TAG, "inside locationReceiver69, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
                mLatLng = new LatLng(lat, lon);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //getNearbyATMs.getListOfATMs(addressLine, lat, lon);

                        getNearbyMerchs.getListOfMerchs("Three World Financial Center, 230 Vesey St, New York, NY 10281, USA", 40.7127, -74.0153);
                    }
                }).start();
            }
            else{
                Log.i(TAG, "inside locationReceiver100, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
            }

        }
    };
    private final BroadcastReceiver MerchListReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "inside MerchListReceiver, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));

            dataList.clear();
            mListadapter.notifyDataSetChanged();

            if(intent.getBooleanExtra("isUsingMyLocation", true) && isUsingMyLocation)
            {
                findSafeAndUnsafeMerchs(nearbyMerchsList, nearbycontainmentZonesList);
            }
            else if(!intent.getBooleanExtra("isUsingMyLocation", true))
            {
                Log.i(TAG, "custom location");
                findSafeAndUnsafeMerchs(MerchsList, containmentZonesList);
            }
        }
    };
    public void onDestroy() {
        super.onDestroy();
        try{
            getActivity().unregisterReceiver(locationReceiver);
        }catch (Exception e){ e.printStackTrace(); }
    }
    private void setupLocationAPI(String send)
    {
        Log.i("location msg","int the api function");
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener(getActivity(),"ADDRESS_FOUND2");

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
    private void findSafeAndUnsafeMerchs(ArrayList<LocationObject> MerchsList, ArrayList<LocationObject> containmentZonesList)
    {
        if(MerchsList.size()==0){
            //unsafeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
            //safeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
            dataList.add(new Merchant("There are no ATMs near you.", "", true, ""));
            mListadapter.notifyDataSetChanged();
        }
        // check which ATMs are in safe area.
        for(LocationObject merchObject : MerchsList)
        {
//            for(LocationObject zoneObject : containmentZonesList )
//            {
//                float distance = getDistance(atmObject.getLatitude(), atmObject.getLongitude(),
//                        zoneObject.getLatitude(), zoneObject.getLongitude());
//
//                if(distance<=containmentZoneRadius)  // in containment zone.
//                {
//                    dataList.add(new Merchant(atmObject.getPlaceName(), atmObject.getLatitude()+":"+atmObject.getLongitude(), true, "Safe"));
//                    mListadapter.notifyDataSetChanged();
//                }
//                else
//                {
//                    dataList.add(new Merchant(atmObject.getPlaceName(), atmObject.getLatitude()+":"+atmObject.getLongitude(), true, "Unsafe"));
//                    mListadapter.notifyDataSetChanged();                }
//            }
            dataList.add(new Merchant(merchObject.getPlaceName(), merchObject.getLatitude()+":"+merchObject.getLongitude(), true, "Safe"));
            mListadapter.notifyDataSetChanged();
            //if(containmentZonesList.size()==0)
            //  safeList.setText(safeList.getText().toString()+"\n"+atmObject.getPlaceName());
        }
        //progressBar.setVisibility(View.INVISIBLE);
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
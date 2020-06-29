package com.example.app.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.Utilities.GetNearbyMerchants;
import com.example.app.model.LocationObject;
import com.example.app.Utilities.MyLocationListener;
import com.example.app.model.MerchObject;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_safe_merch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_safe_merch extends Fragment {
//    private RecyclerView rcview;
//    private LocationObject mLocation;
//    public ArrayList<MerchObject> MerchsList;
//    public ArrayList<LocationObject> containmentZonesList;
//    private GetNearbyMerchants getNearbyMerchants;
//    private FragmentATM fgny;
//    private String send2;
//    private ArrayList<MerchObject> dataList;
//    private ListAdapter mListadapter;
//    public ArrayList<LocationObject> nearbycontainmentZonesList;
//    public ArrayList<MerchObject> nearbyMerchsList;
//    private boolean isUsingMyLocation;
//    private LatLng mLatLng;
//    private BottomSheetListener bottomSheetListener;
//    public int categoryCode;
//    Map<String,Integer > map=new HashMap<>();
//    String[] category = new String[] {"Fast Food Restaurants", "Pharmacies", "Book Stores"};
//    String categoryDesc;
//    String distanceUnit;
//    Integer distance;
//    private double latitude,longitude;
//    String firstplacename;
    public Frag_safe_merch() {
        // Required empty public constructor
    }

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
        View view= inflater.inflate(R.layout.fragment_frag_safe_merch, container, false);
//        Log.i(TAG, "inside onCreate.");
//        dataList = new ArrayList<>();
//        mListadapter = new ListAdapter(dataList);
//        rcview=(RecyclerView)view.findViewById(R.id.rcviewid);
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rcview.setLayoutManager(layoutManager);
//        rcview.setAdapter(mListadapter);
//        MerchsList = new ArrayList<>();
//        containmentZonesList = new ArrayList<>();
//        //isUsingMyLocation = true;
//        getNearbyMerchants = new GetNearbyMerchants(getActivity(), MerchsList, nearbycontainmentZonesList,
//                isUsingMyLocation, "2000");
//        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_MERCH_LIST");
//        getActivity().registerReceiver(MerchListReceiver, intentFilter1);
//        if (!Places.isInitialized()) {
//            Places.initialize(getActivity(), getString(R.string.google_maps_key));
//        }
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(final Place place) {
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress());
//                firstplacename=place.getName();
//                Address address = null;
//                try {
//                    Geocoder geocoder = new Geocoder(getActivity(),
//                            Locale.getDefault());
//                    List<Address> addresses = geocoder.getFromLocationName(
//                            place.getName(),
//                            1
//                    );
//                    address = addresses.get(0);
//                    //mLatLng = new LatLng(address.getLatitude(), address.getLongitude());
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if(address!=null){
////                    final Address finalAddress = address;
////                    new Thread(new Runnable() {
////                        @Override
////                        public void run() {
////                            getATMs.getListOfATMs(place.getName(), finalAddress.getLatitude(), finalAddress.getLongitude());
////                        }
////                    }).start();
//                    latitude=address.getLatitude();
//                    longitude=address.getLongitude();
//                }
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
//
//
//        //Drop Down layout starts
//        map.put("Fast Food Restaurants",5814);
//        map.put("Books Stores",5942);
//        map.put("Pharmacies",5912);
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(
//                        getContext(),
//                        R.layout.dropdown_menu_popup_item,
//                        category);
//        final AutoCompleteTextView editTextFilledExposedDropdown =
//                view.findViewById(R.id.filled_exposed_dropdown);
//        editTextFilledExposedDropdown.setAdapter(adapter);
//
//        String[] distance_format = new String[] {"km", "m"};
//        ArrayAdapter<String> distance_adapter =
//                new ArrayAdapter<>(
//                        getContext(),
//                        R.layout.dropdown_menu_popup_item,
//                        distance_format);
//        final AutoCompleteTextView edittextFilledDistanceValues =
//                view.findViewById(R.id.distance_dropdown);
//        edittextFilledDistanceValues.setText(distance_format[0]);
//        edittextFilledDistanceValues.setAdapter(distance_adapter);
//
//        final TextInputLayout distancef = view.findViewById(R.id.distance);
//        final Button applyFilterBTN = view.findViewById(R.id.id_apply_filter_btn);
//        applyFilterBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                categoryDesc=editTextFilledExposedDropdown.getText().toString();
//                distanceUnit=edittextFilledDistanceValues.getText().toString();
//                categoryCode=map.get(categoryDesc);
//                distance = Integer.valueOf(distancef.getEditText().getText().toString());
//                //bottomSheetListener.onButtonClicked();
//                if(distanceUnit=="km"){
//                    distanceUnit="KM";
//                    distance=distance*1000;
//                }
//                else{
//                    distanceUnit="M";
//                }
//                System.out.println(categoryDesc+" "+categoryCode+" "+distanceUnit+" "+distance);
//                getNearbyMerchants.getListOfMerchants(firstplacename, latitude, longitude,categoryCode,distance,distanceUnit);
//            }
//        });
        return view;
    }
//    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent)
//        {
//            final double lat = intent.getDoubleExtra("latitude", 0);
//            final double lon = intent.getDoubleExtra("longitude", 0);
//            final String addressLine = intent.getStringExtra("addressLine");
//            try{
//                getActivity().unregisterReceiver(locationReceiver);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            if(isUsingMyLocation) {
//                Log.i(TAG, "inside locationReceiver69, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
//                mLatLng = new LatLng(lat, lon);
//                new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        getNearbyMerchants.getListOfMerchants("Testing", 40.7127, -74.0153,categoryCode,distance,distanceUnit);
//                    }
//                }).start();
//            }
//            else{
//                Log.i(TAG, "inside locationReceiver100, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
//            }
//
//        }
//    };
//    private final BroadcastReceiver MerchListReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent)
//        {
//            Log.i(TAG, "inside MerchListReceiver, response type : "+intent.getBooleanExtra("isUsingMyLocation", true));
//            dataList.clear();
//            mListadapter.notifyDataSetChanged();
////
////            if(intent.getBooleanExtra("isUsingMyLocation", true) && isUsingMyLocation)
////            {
////                findSafeAndUnsafeMerchs(nearbyMerchsList, nearbycontainmentZonesList);
////            }
////            else if(!intent.getBooleanExtra("isUsingMyLocation", true))
////            {
////
////            }
//            Log.i(TAG, "custom location");
//            findSafeAndUnsafeMerchs(MerchsList, containmentZonesList);
//        }
//    };
//    public void onDestroy() {
//        super.onDestroy();
//        try{
//            getActivity().unregisterReceiver(locationReceiver);
//        }catch (Exception e){ e.printStackTrace(); }
//    }
//    private void setupLocationAPI(String send)
//    {
//        Log.i("location msg","int the api function");
//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        LocationListener locationListener = new MyLocationListener(getActivity(),"ADDRESS_FOUND2");
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
//            }
//        }
//        else
//        {
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
//        }
//    }
//    private void findSafeAndUnsafeMerchs(ArrayList<MerchObject> MerchsList, ArrayList<LocationObject> containmentZonesList)
//    {
//        System.out.println("InthesafeATMfunction");
//        if(MerchsList.size()==0){
//            //unsafeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
//            //safeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
//            dataList.add(new MerchObject(0,0,"No stores","No category","No distance", ""));
//            mListadapter.notifyDataSetChanged();
//            System.out.println("There is nothing in the list");
//        }
//        // check which ATMs are in safe area.
//
//        for(MerchObject merchObject : MerchsList)
//        {
////            for(LocationObject zoneObject : containmentZonesList )
////            {
////                float distance = getDistance(atmObject.getLatitude(), atmObject.getLongitude(),
////                        zoneObject.getLatitude(), zoneObject.getLongitude());
////
////                if(distance<=containmentZoneRadius)  // in containment zone.
////                {
////                    dataList.add(new Merchant(atmObject.getPlaceName(), atmObject.getLatitude()+":"+atmObject.getLongitude(), true, "Safe"));
////                    mListadapter.notifyDataSetChanged();
////                }
////                else
////                {
////                    dataList.add(new Merchant(atmObject.getPlaceName(), atmObject.getLatitude()+":"+atmObject.getLongitude(), true, "Unsafe"));
////                    mListadapter.notifyDataSetChanged();                }
////            }
//            dataList.add(new MerchObject(merchObject.getLat(),merchObject.getLon(),merchObject.getStoreName(),merchObject.getCategoryDesc(),merchObject.getDistanceDesc(), ""));
//            mListadapter.notifyDataSetChanged();
//            //if(containmentZonesList.size()==0)
//            //  safeList.setText(safeList.getText().toString()+"\n"+atmObject.getPlaceName());
//        }
//        System.out.println("The last print");
//        //progressBar.setVisibility(View.INVISIBLE);
//    }
//    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
//        private ArrayList<MerchObject> dataList;
//
//        public ListAdapter(ArrayList<MerchObject> data) {
//            this.dataList = data;
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            TextView textViewName;
//            TextView textViewAddress;
//            //TextView textViewOpen;
//            TextView textViewDistance;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                this.textViewName = (TextView) itemView.findViewById(R.id.name);
//                this.textViewAddress = (TextView) itemView.findViewById(R.id.address);
//                //this.textViewOpen = (TextView) itemView.findViewById(R.id.open);
//                this.textViewDistance = (TextView) itemView.findViewById(R.id.distance);
//            }
//        }
//
//        @Override
//        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_merchant, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder(view);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {
//            holder.textViewName.setText(dataList.get(position).getStoreName());
//            holder.textViewAddress.setText("Address feature later");
//            //holder.textViewOpen.setText(dataList.get(position).isOpen() ? "true" : "false");
//            holder.textViewDistance.setText(dataList.get(position).getDistanceDesc());
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getActivity(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return dataList.size();
//        }
//    }
//    public interface BottomSheetListener{
//        void onButtonClicked();
//    }
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try{
//            bottomSheetListener = (BottomSheetListener) context;
//        }catch (ClassCastException e){
//            e.printStackTrace();
//        }
//    }
}
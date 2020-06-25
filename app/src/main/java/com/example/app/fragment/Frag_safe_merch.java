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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.Utilities.GetNearbyATMs;
import com.example.app.Utilities.GetNearbyMerchs;
import com.example.app.Utilities.LocationObject;
import com.example.app.Utilities.MyLocationListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_safe_merch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_safe_merch extends Fragment {
    private RecyclerView rcview;
    private LocationObject mLocation;
    public ArrayList<LocationObject> ATMsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyMerchs getNearbyMerchs;
    private FragmentNearYou fgny;
    private String send2;
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
        rcview=(RecyclerView)view.findViewById(R.id.rcviewid);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcview.setLayoutManager(layoutManager);
        ATMsList = new ArrayList<>();
        containmentZonesList = new ArrayList<>();
        getNearbyMerchs = new GetNearbyMerchs(getActivity(), ATMsList, containmentZonesList);
//        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_ATM_LIST");
//        getActivity().registerReceiver(ATMListReceiver, intentFilter1);
        send2="ADDRESS_FOUND2";
        IntentFilter intentFilter = new IntentFilter(send2);
        getActivity().registerReceiver(locationReceiver, intentFilter);
        setupLocationAPI(send2);
        return view;
    }
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Toast.makeText(context, "Location found yipee", Toast.LENGTH_LONG).show();
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
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener(getActivity(),send);

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
}
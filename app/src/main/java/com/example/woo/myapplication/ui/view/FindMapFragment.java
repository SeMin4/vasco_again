package com.example.woo.myapplication.ui.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.woo.myapplication.R;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;


public class FindMapFragment extends Fragment implements OnMapReadyCallback {



    MapFragment mapFragment;
    FragmentTransaction fragmentTransaction;
    Fragment findMapFragment;



    // TODO: Rename and change types and number of parameters
    public static FindMapFragment newInstance() {
        FindMapFragment fragment = new FindMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.findmap, mapFragment).addToBackStack(null).commit();
        }
        mapFragment.getMapAsync(this);
        findMapFragment = fm.findFragmentById(R.id.findmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_map, container, true);
        return  rootView;
    }




    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap){

    }
}

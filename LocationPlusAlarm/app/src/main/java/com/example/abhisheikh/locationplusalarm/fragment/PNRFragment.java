package com.example.abhisheikh.locationplusalarm.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhisheikh.locationplusalarm.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PNRFragment extends Fragment {


    public PNRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pnr, container, false);
    }

}
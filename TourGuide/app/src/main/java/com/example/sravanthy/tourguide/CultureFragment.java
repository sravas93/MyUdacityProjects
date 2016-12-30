package com.example.sravanthy.tourguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sandeep on 11/23/2016.
 */
public class CultureFragment extends Fragment {

    public CultureFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.culture_fragment, container, false);

        return rootView;
    }
}


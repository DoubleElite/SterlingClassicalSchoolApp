package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactFragment extends Fragment {

    // Views
    Button phoneBtn;

    // Map
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);

        setUpMapIfNeeded();
        LatLng sterling = new LatLng(30.578316, -97.869884);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sterling, 13));

        map.addMarker(new MarkerOptions()
                .title("Sterling Classical School")
                .snippet("We take college preperatory material to a whole new level!")
                .position(sterling));


        // Get the button and set a click listener.
        phoneBtn = (Button)view.findViewById(R.id.btn_contact_phone);
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the phone dialer with the number already in the dialer.
                // The 'tel:' prefix is required, otherwhise the following exception will be thrown: java.lang.IllegalStateException: Could not execute method of the activity.
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:5122592722"));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title. We do that here because if the user presses the back button
        // to get back to this fragment we need to update the title from the previous title.
        getActivity().getActionBar()
                .setTitle(R.string.fragment_title_contact_us);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
    }
}

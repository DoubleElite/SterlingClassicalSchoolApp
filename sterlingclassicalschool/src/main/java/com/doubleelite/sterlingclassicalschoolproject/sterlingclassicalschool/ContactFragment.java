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

public class ContactFragment extends Fragment {

    Button phoneBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
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
}

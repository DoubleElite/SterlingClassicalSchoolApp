package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InformationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.information_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title. We do that here because if the user presses the back button
        // to get back to this fragment we need to update the title from the previous title.
        getActivity().getActionBar()
                .setTitle(R.string.fragment_title_our_goals);
    }

}

package com.josiahgaskin.opticon2015demo;

import com.optimizely.Optimizely;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class GeofencingDetailFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GeofencingDetailFragment newInstance(int sectionNumber) {
        GeofencingDetailFragment fragment = new GeofencingDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GeofencingDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geofencing, container, false);
        final SharedPreferences prefs = getActivity()
                .getSharedPreferences("brickandmortar", Context.MODE_PRIVATE);
        int totalVisits = prefs.getInt("TOTAL_VISITS", 0);
        ((TextView)rootView.findViewById(R.id.total_visits)).setText(String.format("Total Visits: %d", totalVisits));

        StringBuilder sb = new StringBuilder();
        sb.append("You have visited the following locations: ");
        for (String locationName : prefs.getStringSet("VISITED_SET", Collections.<String>emptySet())) {
            sb.append(locationName).append(" ");
        }
        ((TextView)rootView.findViewById(R.id.all_locations)).setText(sb.toString());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}

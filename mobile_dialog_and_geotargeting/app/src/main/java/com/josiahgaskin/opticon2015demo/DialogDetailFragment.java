package com.josiahgaskin.opticon2015demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class DialogDetailFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private LiveVariableDialogBuilder mSalesDialogBuilder;
    private LiveVariableDialogBuilder mAnotherDialogBuilder;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DialogDetailFragment newInstance(int sectionNumber) {
        DialogDetailFragment fragment = new DialogDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DialogDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);
        rootView.findViewById(R.id.sales_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSalesDialogBuilder.show();
            }
        });
        rootView.findViewById(R.id.another_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnotherDialogBuilder.show();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        mSalesDialogBuilder = new LiveVariableDialogBuilder(activity).setVariableKey("SalesDialog");
        mAnotherDialogBuilder = new LiveVariableDialogBuilder(activity).setVariableKey("AnotherDialog");
    }
}

package com.josiahgaskin.opticon2015demo;

import com.optimizely.Optimizely;
import com.optimizely.Variable.LiveVariable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * Dialog builder that uses LiveVariables for message and title.
 */
public class LiveVariableDialogBuilder extends AlertDialog.Builder {
    LiveVariable<String> mMessageVariable;
    LiveVariable<String> mTitleVariable;

    public LiveVariableDialogBuilder(Context context) { super(context); }
    public LiveVariableDialogBuilder(Context context, int theme) { super(context, theme); }

    public LiveVariableDialogBuilder setVariableKey(@NonNull String variableKey) {
        mMessageVariable = Optimizely.stringVariable(variableKey + "_message", "");
        mTitleVariable = Optimizely.stringVariable(variableKey+"_title", "");
        return this;
    }

    @NonNull @Override
    public AlertDialog create() {
        if (mMessageVariable != null) { setMessage(mMessageVariable.get()); }
        if (mTitleVariable != null) { setTitle(mTitleVariable.get()); }
        return super.create();
    }
}

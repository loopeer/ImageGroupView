/**
 * Created by YuGang Yang on September 05, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.librarys.imagegroupview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

public class GetImageDialogFragment extends DialogFragment implements View.OnClickListener {

    public interface ClickListener {
        void click();
    }

    private ClickListener mPositiveListener;
    private ClickListener mNegativeListener;

    public static GetImageDialogFragment newInstance() {
        GetImageDialogFragment getImageDialogFragment = new GetImageDialogFragment();
        return getImageDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;

        LayoutInflater inflater =
                (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_fragment_get_photo, null);

        layout.findViewById(android.R.id.button1).setOnClickListener(this);
        layout.findViewById(android.R.id.button2).setOnClickListener(this);

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case android.R.id.button1:
                mNegativeListener.click();
                break;
            case android.R.id.button2:
                mPositiveListener.click();
                break;

        }
        dismiss();
    }


    public static class Builder {
        GetImageDialogFragment getImageDialogFragment;
        private FragmentManager mManager;


        public Builder(FragmentManager manager) {
            getImageDialogFragment = GetImageDialogFragment.newInstance();
            mManager = manager;
        }

        public Builder setPositiveListener(ClickListener clickListener) {
            getImageDialogFragment.mPositiveListener = clickListener;
            return this;
        }

        public Builder setNegativeListener(ClickListener clickListener) {
            getImageDialogFragment.mNegativeListener = clickListener;
            return this;
        }

        public void show() {
            getImageDialogFragment.show(mManager, "");
        }
    }

}

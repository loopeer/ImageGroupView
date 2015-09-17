/**
 * Created by YuGang Yang on September 05, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.imagegroupview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

public class GetPhotoDialogFragment extends DialogFragment implements View.OnClickListener {

    public interface ClickListener {
        public void click();
    }

    private ClickListener mPositiveListener;
    private ClickListener mNegativeListener;

    public static GetPhotoDialogFragment newInstance() {
        GetPhotoDialogFragment getPhotoDialogFragment = new GetPhotoDialogFragment();
        return getPhotoDialogFragment;
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
        GetPhotoDialogFragment getPhotoDialogFragment;
        private FragmentManager mManager;


        public Builder(FragmentManager manager) {
            getPhotoDialogFragment = GetPhotoDialogFragment.newInstance();
            mManager = manager;
        }

        public Builder setPositiveListener(ClickListener clickListener) {
            getPhotoDialogFragment.mPositiveListener = clickListener;
            return this;
        }

        public Builder setNegativeListener(ClickListener clickListener) {
            getPhotoDialogFragment.mNegativeListener = clickListener;
            return this;
        }

        public void show() {
            getPhotoDialogFragment.show(mManager, "");
        }
    }

}

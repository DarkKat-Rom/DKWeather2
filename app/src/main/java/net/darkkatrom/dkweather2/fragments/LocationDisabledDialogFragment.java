/*
 * Copyright (C) 2019 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.darkkatrom.dkweather2.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.darkkatrom.dkweather2.R;

public class LocationDisabledDialogFragment extends DialogFragment {

    private LocationDisabledDialogListener mListener = null;

    public interface LocationDisabledDialogListener {
        void onLocationDisabledDialogPositiveClick(DialogFragment dialog);
        void onLocationDisabledDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.location_disabled_dialog_title)
                .setMessage(R.string.location_disabled_dialog_message)
                .setPositiveButton(R.string.dialog_enable, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onLocationDisabledDialogPositiveClick(LocationDisabledDialogFragment.this);
                        }
                   }
               })
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onLocationDisabledDialogNegativeClick(LocationDisabledDialogFragment.this);
                        }
                   }
               });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationDisabledDialogListener) {
            mListener = (LocationDisabledDialogListener) context;
        }
    }
}

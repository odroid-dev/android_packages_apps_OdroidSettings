/*
 * Copyright (C) 2015 The Android Open Source Project
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
 * limitations under the License
 */
package com.hardkernel.odroid.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.util.Log;

import com.hardkernel.odroid.settings.R;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.LinkMovementMethod;
import android.support.annotation.Keep;
import android.app.AlertDialog;
import android.text.TextUtils;

@Keep
public class MiscFragment extends LeanbackAddBackPreferenceFragment
implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "MiscFragment";
    private static final boolean GAPPS = SystemProperties.getBoolean("ro.opengapps_installed", false);

    private Preference pref_gsf_id;

    private static final Uri sUri = Uri.parse("content://com.google.android.gsf.gservices");

    private View view_dialog;
    private AlertDialog mAlertDialog = null;

    public static MiscFragment newInstance() {
        return new MiscFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        setPreferencesFromResource(R.xml.misc_settings, null);

        pref_gsf_id = (Preference) findPreference(getString(R.string.pref_gsf_id));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pref_gsf_id.setVisible(GAPPS);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if (preference.equals(pref_gsf_id)) {
            showDialog();
        }
        return super.onPreferenceTreeClick(preference);
    }

    public static String getGSFID(Context context) {
        try {
            Cursor query = context.getContentResolver().query(sUri, null, null, new String[] {
                "android_id"
            }, null);
            if (query == null) {
                return "Not found";
            }
            if (!query.moveToFirst() || query.getColumnCount() < 2) {
                query.close();
                return "Not found";
            }
            final String toHexString = Long.toHexString(Long.parseLong(query.getString(1)));
            query.close();
            return toHexString.toUpperCase().trim();
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showDialog() {
        if (mAlertDialog == null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view_dialog = inflater.inflate(R.layout.dialog_gsfid, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            mAlertDialog = builder.create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        }

        Activity activity = (Activity) getContext();
        final ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        TextView TVgsfid = view_dialog.findViewById(R.id.textView_gsfid);
        TextView GsfHelp = view_dialog.findViewById(R.id.textView_gsf_url);
        GsfHelp.setMovementMethod(LinkMovementMethod.getInstance());
        long gsfint = Long.parseLong(getGSFID(activity), 16);
        final String gsfid = Long.toString(gsfint);
        TVgsfid.setText(gsfid);
        ImageView copyText = view_dialog.findViewById(R.id.copy);

        copyText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ClipData clip = ClipData.newPlainText("Copied Text", gsfid);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity, "Text copied to Clipboard", Toast.LENGTH_LONG).show();
            }
        });

        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(view_dialog);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(true);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}

/*
 * Copyright (C) 2018 DarkKat
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

package net.darkkatrom.dkweather.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.darkkatrom.dkweather.R;
import net.darkkatrom.dkweather.colorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dkweather.utils.Config;
import net.darkkatrom.dkweather.utils.JobUtil;

public class WidgetSettings extends SettingsColorPickerFragment implements
        OnPreferenceChangeListener  {

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        updatePreferenceScreen();
    }

    public void updatePreferenceScreen() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.widget_settings);

        findPreference(Config.PREF_KEY_WIDGET_BACKGROUND).setOnPreferenceChangeListener(this);
        findPreference(Config.PREF_KEY_WIDGET_BACKGROUND_COLOR).setOnPreferenceChangeListener(this);
        findPreference(Config.PREF_KEY_WIDGET_FRAME_COLOR).setOnPreferenceChangeListener(this);
        findPreference(Config.PREF_KEY_WIDGET_TEXT_COLOR).setOnPreferenceChangeListener(this);
        findPreference(Config.PREF_KEY_WIDGET_ICON_COLOR).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showResetDialog(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (Config.PREF_KEY_WIDGET_BACKGROUND.equals(key)
                || Config.PREF_KEY_WIDGET_BACKGROUND_COLOR.equals(key)
                || Config.PREF_KEY_WIDGET_FRAME_COLOR.equals(key)
                || Config.PREF_KEY_WIDGET_TEXT_COLOR.equals(key)
                || Config.PREF_KEY_WIDGET_ICON_COLOR.equals(key)) {
            JobUtil.startWidgetUpdate(getActivity());
            return true;
        }
        return false;
    }

    private void showResetDialog(int id) {
        DialogFragment newFragment = ResetDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class ResetDialogFragment extends DialogFragment {

        public static ResetDialogFragment newInstance(int id) {
            ResetDialogFragment frag = new ResetDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        WidgetSettings getOwner() {
            return (WidgetSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.dlg_cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getOwner().resetWidgetSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    public void resetWidgetSettings() {
        Config.resetWidgetSettings(getActivity());
        JobUtil.startWidgetUpdate(getActivity());
        updatePreferenceScreen();
    }
}

package com.clearanglestudios.drive_catalogue;

import java.util.prefs.Preferences;

public class Settings {

    // Get reference to the system preferences
    private static final Preferences prefs = Preferences.userNodeForPackage(Settings.class);

    // Keys
    private static final String KEY_SHOW_ALL_INGEST = "show_all_ingest";
    private static final String KEY_SHOW_ALL_RETURN = "show_all_return";
    private static final String KEY_EMAIL_ENABLED = "email_enabled";

    // Getters
    public static boolean isShowAllIngest() {
        return prefs.getBoolean(KEY_SHOW_ALL_INGEST, false);
    }

    public static boolean isShowAllReturn() {
        return prefs.getBoolean(KEY_SHOW_ALL_RETURN, false);
    }

    public static boolean isEmailEnabled() {
        return prefs.getBoolean(KEY_EMAIL_ENABLED, true);
    }

    // Setters
    public static void setShowAllIngest(boolean value) {
        prefs.putBoolean(KEY_SHOW_ALL_INGEST, value);
    }

    public static void setShowAllReturn(boolean value) {
        prefs.putBoolean(KEY_SHOW_ALL_RETURN, value);
    }

    public static void setEmailEnabled(boolean value) {
        prefs.putBoolean(KEY_EMAIL_ENABLED, value);
    }
}
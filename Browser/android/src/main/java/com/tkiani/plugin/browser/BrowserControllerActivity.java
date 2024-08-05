package com.tkiani.plugin.browser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Activity that controls the lifecycle of the custom Chrome Tab.
 * This activity is used to control the display of the custom Chrome Tab
 * and to handle the resume and close of the custom Chrome Tab.
 */
public class BrowserControllerActivity extends Activity {

    // Flag to indicate if the custom Chrome Tab is open
    private boolean isCustomTabsOpen = false;

    /**
     * Sets up the activity on create.
     *
     * @param savedInstanceState the saved instance state<<<<<< capacitorBrowser

     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCustomTabsOpen = false; // Set the flag to false on create
        notifyControllerListener(); // Notify the controller listener
    }

    /**
     * Handles the new intent.
     *
     * @param intent the new intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleCloseIntent(intent); // Handle the close intent
    }

    /**
     * Handles the activity resume.
     */
    @Override
    protected void onResume() {
        super.onResume();
        handleResume(); // Handle the resume
    }

    /**
     * Opens the browser with the given browser, URL, and toolbar color.
     *
     * @param browser the browser
     * @param url     the URL
     * @param toolbarColor the toolbar color
     */
    public void openBrowser(Browser browser, Uri url, @Nullable Integer toolbarColor) {
        browser.open(url, toolbarColor); // Open the browser
    }

    /**
     * Sets up the activity on destroy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCustomTabsOpen = false; // Set the flag to false on destroy
        BrowserPlugin.setControllerListener(null); // Set the controller listener to null
    }

    /**
     * Notifies the controller listener.
     */
    private void notifyControllerListener() {
        BrowserControllerListener controllerListener = BrowserPlugin.getControllerListener();
        if (controllerListener != null) {
            controllerListener.onReady(this); // Notify the controller listener
        }
    }

    /**
     * Handles the close intent.
     *
     * @param intent the intent
     */
    private void handleCloseIntent(Intent intent) {
        if (intent.hasExtra("close")) {
            finish(); // Finish the activity if the close intent is present
        }
    }

    /**
     * Handles the resume.
     */
    private void handleResume() {
        if (isCustomTabsOpen) {
            finish(); // Finish the activity if the custom Chrome Tab is already open
        } else {
            isCustomTabsOpen = true; // Set the flag to true if the custom Chrome Tab is not open
        }
    }
}
package com.tkiani.plugin.browser;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.WebColor;

/**
 * BrowserPlugin class is a plugin for displaying a web page in a custom Chrome Tab.
 */
@CapacitorPlugin(name = "Browser")
public class BrowserPlugin extends Plugin {

    // The implementation of the browser logic
    private Browser browserImplementation;
    // A listener for the browser controller activity
    private static BrowserControllerListener browserControllerListener;
    // An instance of the browser controller activity
    private static BrowserControllerActivity browserControllerActivityInstance;

    /**
     * Sets a listener for the browser controller activity.
     *
     * @param listener The listener to set.
     */
    public static void setBrowserControllerListener(BrowserControllerListener listener) {
        browserControllerListener = listener;
        if (listener == null) {
            browserControllerActivityInstance = null;
        }
    }

    /**
     * Loads the browser implementation.
     */
    @Override
    public void load() {
        browserImplementation = new Browser(getContext());
        browserImplementation.setBrowserEventListener(this::onBrowserEvent);
    }

    /**
     * Opens a web page in a custom Chrome Tab.
     *
     * @param call The call object with the URL and toolbar color.
     */
    @PluginMethod
    public void open(PluginCall call) {
        String urlString = call.getString("url");
        if (urlString == null || urlString.isEmpty()) {
            call.reject("Must provide a valid URL to open");
            return;
        }

        Uri url = Uri.parse(urlString);

        String toolbarColorString = call.getString("toolbarColor");
        Integer toolbarColor = toolbarColorString != null ? WebColor.parseColor(toolbarColorString) : null;

        try {
            startBrowserControllerActivity(url, toolbarColor, call);
        } catch (ActivityNotFoundException e) {
            call.reject("Unable to display URL");
        }
    }

    /**
     * Closes the browser controller activity.
     *
     * @param call The call object.
     */
    @PluginMethod
    public void close(PluginCall call) {
        if (browserControllerActivityInstance != null) {
            browserControllerActivityInstance = null;
            Intent intent = new Intent(getContext(), BrowserControllerActivity.class);
            intent.putExtra("close", true);
            getContext().startActivity(intent);
        }
        call.resolve();
    }

    /**
     * Called when the plugin is resumed. Binds the service.
     */
    @Override
    protected void handleOnResume() {
        browserImplementation.bindService();
    }

    /**
     * Called when the plugin is paused. Unbinds the service.
     */
    @Override
    protected void handleOnPause() {
        browserImplementation.unbindService();
    }

    /**
     * Starts the browser controller activity.
     *
     * @param url        The URL of the web page.
     * @param toolbarColor The color of the toolbar.
     * @param call       The call object.
     * @throws ActivityNotFoundException If the activity is not found.
     */
    private void startBrowserControllerActivity(Uri url, Integer toolbarColor, PluginCall call)
            throws ActivityNotFoundException {
        Intent intent = new Intent(getContext(), BrowserControllerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);

        setBrowserControllerListener(activity -> {
            activity.open(browserImplementation, url, toolbarColor);
            browserControllerActivityInstance = activity;
            call.resolve();
        });
    }

    /**
     * Called when a browser event occurs. Notifies the listeners.
     *
     * @param event The event that occurred.
     */
    private void onBrowserEvent(int event) {
        String eventName;
        switch (event) {
            case Browser.BROWSER_LOADED:
                eventName = "browserPageLoaded";
                break;
            case Browser


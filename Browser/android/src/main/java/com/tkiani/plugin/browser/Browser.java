package com.tkiani.plugin.browser;

import static androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_ON;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.*;

/**
 * The Browser class implements Custom Chrome Tabs. See
 * https://developer.chrome.com/multidevice/android/customtabs for background
 * on how this code works.
 */
public class Browser {

    /**
     * Callback interface for browser events.
     */
    interface BrowserEventListener {
        /**
         * Called when a browser event occurs.
         *
         * @param event the event that occurred (EVENT_LOADED or EVENT_FINISHED)
         */
        void onBrowserEvent(int event);
    }

    static final int EVENT_LOADED = 1;
    static final int EVENT_FINISHED = 2;

    @Nullable
    private BrowserEventListener browserEventListener;
    private Context context;
    private static final String FALLBACK_PACKAGE_NAME = "com.android.chrome";
    private CustomTabsClient client;
    private CustomTabsSession session;
    private boolean isInitialLoad = false;
    private EventGroup eventGroup;
    private CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
        @Override
        public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
            Browser.this.client = client;
            client.warmup(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    /**
     * Constructs a new Browser instance.
     *
     * @param context the context in which the browser is running
     */
    public Browser(@NonNull Context context) {
        this.context = context;
        this.eventGroup = new EventGroup(this::handleGroupCompletion);
    }

    /**
     * Sets the browser event listener.
     *
     * @param listener the listener to set
     */
    public void setBrowserEventListener(@Nullable BrowserEventListener listener) {
        browserEventListener = listener;
    }

    /**
     * Gets the browser event listener.
     *
     * @return the browser event listener
     */
    @Nullable
    public BrowserEventListener getBrowserEventListener() {
        return browserEventListener;
    }

    /**
     * Opens a URL in the browser.
     *
     * @param url the URL to open
     */
    public void open(Uri url) {
        open(url, null);
    }

    /**
     * Opens a URL in the browser with an optional toolbar color.
     *
     * @param url the URL to open
     * @param toolbarColor the color of the toolbar
     */
    public void open(Uri url, @Nullable Integer toolbarColor) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getCustomTabsSession());
        builder.setShareState(SHARE_STATE_ON);

        if (toolbarColor != null) {
            CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(toolbarColor.intValue())
                    .build();
            builder.setDefaultColorSchemeParams(params);
        }

        CustomTabsIntent tabsIntent = builder.build();
        tabsIntent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + context.getPackageName()));

        isInitialLoad = true;
        eventGroup.reset();
        tabsIntent.launchUrl(context, url);
    }

    /**
     * Binds the browser to the custom tabs service.
     *
     * @return true if the service was bound successfully, false otherwise
     */
    public boolean bindService() {
        String packageName = CustomTabsClient.getPackageName(context, null);
        if (packageName == null) {
            packageName = FALLBACK_PACKAGE_NAME;
        }
        boolean result = CustomTabsClient.bindCustomTabsService(context, packageName, connection);
        eventGroup.leave();
        return result;
    }

    /**
     * Unbinds the browser from the custom tabs service.
     */
    public void unbindService() {
        context.unbindService(connection);
        eventGroup.enter();
    }

    /**
     * Handles a navigation event.
     *
     * @param navigationEvent the navigation event that occurred
     */
    private void handleNavigationEvent(int navigationEvent) {
        switch (navigationEvent) {
            case CustomTabsCallback.NAVIGATION_FINISHED:
                if (isInitialLoad) {
                    if (browserEventListener != null) {
                        browserEventListener.onBrowserEvent(EVENT_LOADED);
                    }
                    isInitialLoad = false;
                }
                break;
            case CustomTabsCallback.TAB_HIDDEN:
                eventGroup.leave();
                break;
            case CustomTabsCallback.TAB_SHOWN:
                eventGroup.enter();
                break;
        }
    }

    /**
     * Handles the completion of the event group.
     */
    private void handleGroupCompletion() {
        if (browserEventListener != null) {
            browserEventListener.onBrowserEvent(EVENT_FINISHED);
        }
    }

    /**
     * Gets the custom tabs session.
     *
     * @return the custom tabs session, or null if the client is null
     */
    @Nullable
    private CustomTabsSession getCustomTabsSession() {
        if (client == null) {
            return null;
        }

        if (session == null) {
            session = client.newSession(new CustomTabsCallback() {
                @Override
                public void onNavigationEvent(int navigationEvent, Bundle extras) {
                    handleNavigationEvent(navigationEvent);
                }
            });
        }

        return session;
    }
}
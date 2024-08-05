import type { PluginListenerHandle } from '@capacitor/core';

/**
 * Plugin interface for the Browser plugin.
 *
 * @since 1.0.0
 */
export interface BrowserPlugin {
  /**
   * Opens a web page with the specified options.
   *
   * @param options - The options for opening the web page.
   * @returns A promise that resolves when the web page is opened.
   *
   * @since 1.0.0
   */
  open(options: OpenOptions): Promise<void>;

  /**
   * Closes an open browser window.
   *
   * No-op on other platforms.
   *
   * @returns A promise that resolves when the browser window is closed.
   *
   * @since 1.0.0
   */
  close(): Promise<void>;

  /**
   * Listens for the browser finished event.
   * Fires when the browser is closed by the user.
   *
   * @param eventName - The name of the event.
   * @param listener - The listener function to be called when the event is fired.
   * @returns A promise that resolves with a listener handle.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'browserFinished',
    listener: () => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listens for the page loaded event.
   * Fires when the URL passed to open method finishes loading.
   *
   * @param eventName - The name of the event.
   * @param listener - The listener function to be called when the event is fired.
   * @returns A promise that resolves with a listener handle.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'browserPageLoaded',
    listener: () => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Removes all native listeners for this plugin.
   *
   * @returns A promise that resolves when all listeners are removed.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}

/**
 * Represents the options passed to the `open` method.
 *
 * @since 1.0.0
 */
export interface OpenOptions {
  /**
   * The URL to which the browser is opened.
   *
   * @since 1.0.0
   */
  url: string;

  /**
   * Optional target for browser open. Follows
   * the `target` property for `window.open`.
   * Defaults to `_blank`.
   *
   * Ignored on other platforms.
   *
   * @since 1.0.0
   */
  windowName?: string;

  /**
   * A hex color to which the toolbar color is set.
   *
   * @since 1.0.0
   */
  toolbarColor?: string;

  /**
   * The presentation style of the browser on iOS.
   * Defaults to `fullscreen`.
   *
   * Ignored on other platforms.
   *
   * @since 1.0.0
   */
  presentationStyle?: 'fullscreen' | 'popover';

  /**
   * The width of the browser when using `presentationStyle` 'popover' on iPads.
   *
   * Ignored on other platforms.
   *
   * @since 4.0.0
   */
  width?: number;

  /**
   * The height of the browser when using `presentationStyle` 'popover' on iPads.
   *
   * Ignored on other platforms.
   *
   * @since 4.0.0
   */
  height?: number;
}


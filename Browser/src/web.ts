import { WebPlugin } from '@capacitor/core';

import type { BrowserPlugin, OpenOptions } from './definitions';

/**
 * The Web implementation of the Browser plugin.
 *
 * @since 1.0.0
 */
export class BrowserWeb extends WebPlugin implements BrowserPlugin {
  /**
   * The last opened window.
   *
   * @since 1.0.0
   */
  private lastWindow: Window | null;

  /**
   * Constructs a new instance of BrowserWeb.
   *
   * @since 1.0.0
   */
  constructor() {
    super();
    this.lastWindow = null;
  }

  /**
   * Opens a web page with the specified options.
   *
   * @param options - The options for opening the web page.
   * @returns A promise that resolves when the web page is opened.
   *
   * @since 1.0.0
   */
  async open({ url, windowName = '_blank' }: OpenOptions): Promise<void> {
    this.lastWindow = window.open(url, windowName);
  }

  /**
   * Closes an open browser window.
   *
   * @returns A promise that resolves when the browser window is closed.
   *
   * @since 1.0.0
   */
  async close(): Promise<void> {
    if (this.lastWindow) {
      this.lastWindow.close();
      this.lastWindow = null;
    } else {
      throw new Error('No active window to close!');
    }
  }
}

/**
 * The instance of the BrowserWeb class.
 *
 * @since 1.0.0
 */
export const Browser = new BrowserWeb();
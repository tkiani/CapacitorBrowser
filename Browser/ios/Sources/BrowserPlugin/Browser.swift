import Foundation
import SafariServices

/// Enumeration representing different events that can occur in the browser.
@objc public enum BrowserEvent: Int {
    /// Event indicating that the page has finished loading.
    case loaded
    /// Event indicating that the browser has finished.
    case finished
}

/// Class representing the browser functionality.
@objc public class Browser: NSObject, SFSafariViewControllerDelegate, UIPopoverPresentationControllerDelegate {
    
    /// The SFSafariViewController instance used to display the web content.
    private var safariViewController: SFSafariViewController?
    
    /// The callback block that will be called when a browser event occurs.
    public typealias BrowserEventCallback = (BrowserEvent) -> Void
    
    /// The callback block that will be called when a browser event occurs.
    public var browserEventDidOccur: BrowserEventCallback?
    
    /// The view controller representing the browser.
    public var viewController: UIViewController? {
        return safariViewController
    }
    
    /// Prepares the browser for display.
    ///
    /// - Parameters:
    ///   - url: The URL to be displayed.
    ///   - tintColor: The tint color for the browser. Default is nil.
    ///   - style: The modal presentation style for the browser. Default is .fullScreen.
    /// - Returns: True if the browser was successfully prepared, false otherwise.
    public func prepare(url: URL, withTintColor tintColor: UIColor? = nil, modalPresentationStyle style: UIModalPresentationStyle = .fullScreen) -> Bool {
        // Check if the browser is already prepared or if the URL scheme is not http or https
        guard safariViewController == nil, let scheme = url.scheme?.lowercased(), ["http", "https"].contains(scheme) else {
            return false
        }
        
        // Create a new SFSafariViewController instance with the provided URL
        let safariViewController = SFSafariViewController(url: url)
        safariViewController.delegate = self
        safariViewController.preferredBarTintColor = tintColor
        safariViewController.modalPresentationStyle = style
        
        // If the presentation style is .popover, set the popover presentation controller delegate
        if style == .popover {
            DispatchQueue.main.async {
                safariViewController.popoverPresentationController?.delegate = self
            }
        }
        
        // Set the SFSafariViewController instance as the browser's safariViewController property and return true
        self.safariViewController = safariViewController
        return true
    }
    
    /// Cleans up the browser by setting the safariViewController property to nil.
    public func cleanup() {
        safariViewController = nil
    }
    
    /// Called when the SFSafariViewController finishes.
    ///
    /// - Parameter controller: The SFSafariViewController instance that finished.
    public func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        // Call the browserEventDidOccur callback block with .finished as the event
        browserEventDidOccur?(.finished)
        // Set the safariViewController property to nil
        safariViewController = nil
    }
    
    /// Called when the SFSafariViewController finishes initial loading.
    ///
    /// - Parameters:
    ///   - controller: The SFSafariViewController instance that finished initial loading.
    ///   - didLoadSuccessfully: A boolean indicating if the initial load was successful.
    public func safariViewController(_ controller: SFSafariViewController, didCompleteInitialLoad didLoadSuccessfully: Bool) {
        // Call the browserEventDidOccur callback block with .loaded as the event
        browserEventDidOccur?(.loaded)
    }
    
    /// Called when the presentation controller is dismissed.
    ///
    /// - Parameter presentationController: The presentation controller that was dismissed.
    public func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        // Call the browserEventDidOccur callback block with .finished as the event
        browserEventDidOccur?(.finished)
        // Set the safariViewController property to nil
        safariViewController = nil
    }
    
    /// Called when the popover presentation controller is dismissed.
    ///
    /// - Parameter popoverPresentationController: The popover presentation controller that was dismissed.
    public func popoverPresentationControllerDidDismissPopover(_ popoverPresentationController: UIPopoverPresentationController) {
        // Call the browserEventDidOccur callback block with .finished as the event
        browserEventDidOccur?(.finished)
        // Set the safariViewController property to nil
        safariViewController = nil
    }
}
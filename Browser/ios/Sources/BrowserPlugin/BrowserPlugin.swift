import Foundation
import Capacitor

/**
 A plugin for displaying a web page in a custom Chrome Tab.
 */
@objc(BrowserPlugin)
public class BrowserPlugin: CAPPlugin, CAPBridgedPlugin {
    // MARK: - Properties
    
    /// The identifier of the plugin.
    public let identifier = "BrowserPlugin"
    
    /// The JavaScript name of the plugin.
    public let jsName = "Browser"
    
    /// The plugin methods exposed to JavaScript.
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "open", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "close", returnType: CAPPluginReturnPromise)
    ]
    
    /// The implementation of the browser logic.
    private let browserImplementation = Browser()
    
    // MARK: - Methods
    
    /**
     Opens a web page in a custom Chrome Tab.
     
     - Parameters:
        - call: The call object with the URL and toolbar color.
     */
    @objc func open(_ call: CAPPluginCall) {
        // Validate the URL
        guard let urlString = call.getString("url"), let url = URL(string: urlString) else {
            call.reject("Invalid URL")
            return
        }
        
        // Extract the optional parameters
        let toolbarColorString = call.getString("toolbarColor")
        let toolbarColor = toolbarColorString.flatMap { UIColor.capacitor.color(fromHex: $0) }
        
        let presentationStyleString = call.getString("presentationStyle")
        let presentationStyle = self.presentationStyle(for: presentationStyleString)
        
        // Prepare for display
        guard browserImplementation.prepare(for: url, withTint: toolbarColor, modalPresentation: presentationStyle),
              let viewController = browserImplementation.viewController else {
            call.reject("Unable to display URL")
            return
        }
        
        // Set the event listener
        browserImplementation.browserEventDidOccur = { [weak self] (event) in
            self?.notifyListeners(event.listenerEvent, data: nil)
        }
        
        // Display
        DispatchQueue.main.async { [weak self] in
            if presentationStyle == .popover {
                if let width = call.getInt("width"), let height = call.getInt("height") {
                    self?.setCenteredPopover(viewController, size: CGSize(width: width, height: height))
                } else {
                    self?.setCenteredPopover(viewController)
                }
            }
            
            self?.bridge?.presentVC(viewController, animated: true, completion: {
                call.resolve()
            })
        }
    }
    
    /**
     Closes the active window.
     
     - Parameters:
        - call: The call object.
     */
    @objc func close(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            if let viewController = self?.browserImplementation.viewController {
                self?.bridge?.dismissVC(animated: true) {
                    call.resolve()
                    self?.browserImplementation.cleanup()
                }
            } else {
                call.reject("No active window to close!")
            }
        }
    }
    
    /**
     Returns the presentation style based on the provided string.
     
     - Parameters:
        - style: The presentation style string.
     
     - Returns: The UIModalPresentationStyle.
     */
    private func presentationStyle(for style: String?) -> UIModalPresentationStyle {
        return style == "popover" ? .popover : .fullScreen
    }
}

// MARK: - BrowserEvent Extension

private extension BrowserEvent {
    /// Returns the listener event based on the browser event.
    var listenerEvent: String {
        switch self {
        case .loaded: return "browserPageLoaded"
        case .finished: return "browserFinished"
        }
    }
}
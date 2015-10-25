//
//  ViewController.swift
//  ARHockeyMallet
//
//  Created by Aiden De Loryn on 6/10/2015.
//  Copyright © 2015 Aiden De Loryn. All rights reserved.
//

import UIKit
import CoreMotion

class ViewController: UIViewController, UITextFieldDelegate {
	@IBOutlet weak var urlTextField: UITextField!
	@IBOutlet weak var updateSpeedSlider: UISlider!
	@IBOutlet weak var upsLabel: UILabel!
	
	let motionManager = CMMotionManager()
	let deviceID = UIDevice.currentDevice().identifierForVendor!.UUIDString
	
	var url = ""
	var updateSpeed: Float = 1.0
	var running = false
	var colourString = ""
	var tableTopMode = true

	override func viewDidLoad() {
		super.viewDidLoad()
		// Do any additional setup after loading the view, typically from a nib.
		urlTextField.delegate = self
		loadDefaults()
		upsLabel.text = "\(Int(1 / updateSpeed)) updates per sec."
	}
	
	func textFieldShouldReturn(textField: UITextField) -> Bool {
		self.view.endEditing(true)
		return false
	}

	override func didReceiveMemoryWarning() {
		super.didReceiveMemoryWarning()
		// Dispose of any resources that can be recreated.
	}
	
	func outputAccelerationData(acceleration: CMAcceleration) {
		if running {
			let x: Double
			let y: Double
			
			if tableTopMode {
				switch colourString {
				case "Blue":
					x = acceleration.y
					y = acceleration.x * -1
				case "Red":
					x = acceleration.y * -1
					y = acceleration.x
				case "Green":
					x = acceleration.x * -1
					y = acceleration.y * -1
				default:
					x = acceleration.x
					y = acceleration.y
				}
			} else {
				x = acceleration.x
				y = acceleration.y
			}
			
			
			sendHTTPRequest("http://" + url + ":8080/sendData/", queryString: "id=\(deviceID)&x=\(x)&y=\(y)")
		}
	}
	
	func sendHTTPRequest(url: String, queryString: String) {
		let myUrl = NSURL(string: "\(url)?\(queryString)")
		let request = NSMutableURLRequest(URL: myUrl!)
		request.HTTPMethod = "GET"
		
		let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
			data, response, error in
			
			if error != nil {
				print("error=\(error)")
				return
			}
			
			// print out the response object
//			print("***** Response = \(response)")
			
			// print out response body
			let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
//			print("***** Response data = \(responseString)")
			
			dispatch_async(dispatch_get_main_queue(), {
				self.colourString = ((responseString?.componentsSeparatedByString("=")[1]))!
				
				var bgColour: UIColor
				switch self.colourString {
					case "Blue":
						bgColour = UIColor.blueColor()
					case "Red":
						bgColour = UIColor.redColor()
					case "Green":
						bgColour = UIColor.greenColor()
					case "Yellow":
						bgColour = UIColor.yellowColor()
					default:
						bgColour = UIColor.whiteColor()
				}
				
				self.view.backgroundColor = bgColour
			})
		}
		
		task.resume()

	}
	
	func loadDefaults() {
		let defaults = NSUserDefaults.standardUserDefaults()
		if let defaultURL = defaults.stringForKey("url") {
			url = defaultURL
			urlTextField.text = defaultURL
		}
		
		var defaultUpdateSpeed = defaults.floatForKey("updateSpeed")
		if defaultUpdateSpeed == 0.0 {
			defaultUpdateSpeed = 0.075
		}
		
		updateSpeed = defaultUpdateSpeed
		updateSpeedSlider.value = defaultUpdateSpeed
	}
	
	func saveDefaults() {
		let defaults = NSUserDefaults.standardUserDefaults()
		defaults.setObject(url, forKey: "url")
		defaults.setObject(updateSpeed, forKey: "updateSpeed")
	}
	
	@IBAction func activationSwitch(sender: UISwitch) {
		if sender.on {
			running = true
			
			url = urlTextField.text!
			updateSpeed = updateSpeedSlider.value
			motionManager.accelerometerUpdateInterval = NSTimeInterval(updateSpeed)
			
			motionManager.startAccelerometerUpdatesToQueue(NSOperationQueue.currentQueue()!, withHandler: { (accelerometerData: CMAccelerometerData?, error: NSError?) -> Void in
				self.outputAccelerationData(accelerometerData!.acceleration)
				if(error != nil) {
					print("\(error)")
				}
				
			})
		} else {
			running = false
			motionManager.stopAccelerometerUpdates()
		}
		
		saveDefaults()
	}
	
	@IBAction func tableTopSwitch(sender: UISwitch) {
		if sender.on {
			tableTopMode = true
		} else {
			tableTopMode = false
		}
	}
	
	@IBAction func changeUpdateInterval(sender: UISlider) {
		updateSpeed = sender.value
		motionManager.accelerometerUpdateInterval = NSTimeInterval(updateSpeed)
		upsLabel.text = "\(Int(1 / updateSpeed)) updates per sec."
		saveDefaults()
	}
}


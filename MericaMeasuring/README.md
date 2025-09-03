MericaMeasuring by Jake Onkka
The app is intended to measure the levelness/angle of surfaces using a physical android device.
There are no dependencies or extra work to setup besides needing an android device to test.

Project History:

Initially I wanted to make an app for measuring the distance/length of objects and display the result in a silly form of measurement.
I had wanted to use the phone's camera and to do this I attempted to use ARCore and Sceneform.
These api's would allow me to add an AR fragment and use AR objects to place markers and measure the distance of physical space.
This however turned out to be challenging, Google has deprecated Sceneform in 2021 and all guides I tried to follow for setup were outdated.
So I wasn't able to even build the app with these dependencies, even with an alternative Sceneview version I found on GitHub.

Then I attempted to make use of the GPS to measure two points by subtracting the difference from point A with point B, but the inaccuracy of this was way too high for small scale stuff.
Lastly I tried using the accelerometer to measure the acceleration over time from moving from point A to point B, which I could then calculate distance.
This as well proved super difficult for any precise measurements as the sensors were way too wobbly and "move" even when the phone is stationary.

Then with my newfound knowledge about using the accelerometer sensors I decided to make a leveling app.

How it works:

By calculating the angles of the phone's xz and xy axis with the Earth's gravity vector to get the corresponding angles to see if something is level.

The app has a mainactivity that handles button presses and makes calls to anglecontroller which has the sensors and deals with measuring the current angles.
Both of these classes send their respective data to angleview which displays to the screen.

The app has 4 buttons to press and this chooses what angle you want to measure for, 0, 30, 45, 60.
It displays the current angles no matter what but the visual cue that it is "level" will change depending on which button is selected.
This is intended to make it easier to quickly identify if something is say at a 30 degree angle as you can just tell quickly if the screen turns green instead of reading the angles.
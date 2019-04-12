Existing scripts and their uses

Attitude Tests
	-Attitude Analysis: Interprets and prints all attitude data in real time.
	-Periodic-AttitudeTagged-Memory: Prints attitude data, takes a photo, and stamps photo periodically.  This method
constantly saves attitude data to variables in real time.  A photo is taken and these variables are printed when the 
appropriate time has elasped.  This method prints consistently.
	-Periodic-AttitudeTagged-NoMemory: Prints attitude data, takes a photo, and stamps photo periodically.  This 
analysis method waits for the appropriate time to elapse before reading data.  Once the attitude data starts to get 
read, the script takes a picture and then finishes reading the data.  This method should result in the least error 
between image taken and data read.
	
EditExifHeaders
	-Geotag: This script contains test code to edit exif headers.  Reading and writing GPS data from/to a photo.

GPS Tests
	-GPSAnalysis: Interprets and prints all GPS data in real time.

Photo Taking
	-MultiplePhotoScript: Takes multiple photos periodically
	-PhotoScript: Previews the camera brifely and takes a single photo
	-ViewPreview: Just previews the camera, forever.  Or until script is cancelled.

ReadSerial
	-SerialAnalysis: Prints everything raw from the flight controller.
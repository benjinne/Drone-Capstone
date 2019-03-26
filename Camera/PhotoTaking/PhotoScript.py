#takes a single image after a specified delay
from picamera import PiCamera
from time import sleep

camera = PiCamera()

#if camera is upside down (strip is on top side):
#camera.rotation = 180

camera.start_preview()
sleep(5)  #value is in seconds

#this will overwrite
camera.capture('image.jpg')

camera.stop_preview()
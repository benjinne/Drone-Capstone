#takes multiple images based on a timer.
from picamera import PiCamera
from time import sleep

camera = PiCamera()

#if camera is upside down (strip is on top side):
camera.rotation = 180

camera.start_preview()
sleep(10)  #initial delay

count = 1
for i in range(10):  #value in range is the number of images taken
    camera.capture('image' + str(count) + '.jpg')  #this can overwrite existing data
    count = count + 1
    sleep(2)  #time between images

camera.stop_preview()
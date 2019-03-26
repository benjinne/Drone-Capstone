from picamera import PiCamera
from time import sleep

camera = PiCamera()

#if camera is upside down (strip is on top side):
camera.rotation = 180

camera.start_preview()
sleep(10)

count = 1
for i in range(50):
    camera.capture('/home/pi/Camera/image' + str(count) + '.jpg')
    count = count + 1
    sleep(2)

camera.stop_preview()
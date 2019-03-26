
#PREVIEW ONLY WORKS ON HDMI OUTPUT

from picamera import PiCamera
from time import sleep

camera = PiCamera()

#if camera is upside down (strip is on top side):
#camera.rotation = 180

camera.start_preview()

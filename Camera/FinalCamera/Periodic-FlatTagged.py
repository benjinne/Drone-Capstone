#this script takes images periodically, and modifies exif headers with dummy data.
#Longitude = pitch, Latitude = roll, Altitude = count
#saved as degrees, minutes and seconds are set to 0
import time
import serial
import piexif
from PIL import Image
from picamera import PiCamera

ser = serial.Serial(
        port='//dev/ttyS0',
        baudrate = 19200,
        parity=serial.PARITY_NONE,
        stopbits=serial.STOPBITS_ONE,
        bytesize=serial.EIGHTBITS,
        #timeout=1
)
quality = 1 #if 1, permitted to take picture.  if 0, controller data was not accurate by sum-bit
pitch = 0
roll = 0
count = 1  #used to label images
camera = PiCamera()

start = time.time()
while 1:
    x=ser.read()
    
    #updates data
    if x == b'$':
        x=ser.read()
        if x == b'T':
            x=ser.read()
            if x == b'A':
                quality = 1 #check if quality is good, may be unnecessary
                x=ser.read()
                y = x
                x=ser.read()
                z = int.from_bytes(x, byteorder='big')
                if z == 255:
                    y = int.from_bytes(y, byteorder='big', signed=True)
                    pitch = str(y)
                else:
                    pitch = str(y[0])
                
                #roll
                x=ser.read()
                y = x
                x=ser.read()
                z = int.from_bytes(x, byteorder='big')
                if z == 255:
                    y = int.from_bytes(y, byteorder='big', signed=True)
                    roll = str(y)
                else:
                    roll = str(y[0])
                
    end = time.time()
    if end - start > 5 and quality == 1:
        print(end - start)
        #taking picture
        imageName = 'Periodic-FlatTaggedImages//image' + str(count) + 'lat=' + roll + 'long=' + pitch + '.jpg'
        camera.capture(imageName)
        
        #modifying exif GPS data
        img = Image.open(imageName)
        exif_dict = piexif.load(img.info['exif'])
        
        #exif_dict['GPS'][piexif.GPSIFD.GPSLongitude] = (int(pitch), 1)
        #exif_dict['GPS'][piexif.GPSIFD.GPSLatitude] = (int(roll), 1)
        exif_dict['GPS'][piexif.GPSIFD.GPSAltitude] = (count, 1)
        exif_dict['GPS'][piexif.GPSIFD.GPSLatitude] = [((int)(round(abs(int(roll)),6) * 1000000),1000000 ),(0, 1),(0, 1)]
        exif_dict['GPS'][piexif.GPSIFD.GPSLongitude] = [((int)(round(abs(int(pitch)),6) * 1000000),1000000 ),(0, 1),(0, 1)]
        
        #gps_ifd = {
            #piexif.GPSIFD.GPSVersionID: (2, 0, 0, 0),
            #piexif.GPSIFD.GPSAltitudeRef: 1,
            #piexif.GPSIFD.GPSAltitude: count,
            #piexif.GPSIFD.GPSLatitudeRef: "S",
            #piexif.GPSIFD.GPSLatitude: (int(roll),0,0),
            #piexif.GPSIFD.GPSLongitudeRef: "W",
            #piexif.GPSIFD.GPSLongitude: (int(pitch),0,0),
        #}
        
        #exif_dict = {"GPS": gps_ifd}
        exif_bytes = piexif.dump(exif_dict)
        piexif.insert(exif_bytes, imageName)
        #exif_bytes = piexif.dump(exif_dict)
        #piexif.insert(exif_bytes, imageName)
        #img.save(imageName, "jpeg", exif=exif_bytes)
        print("image taken")
        count = count + 1
        
        start = time.time()
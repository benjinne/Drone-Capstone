#this script practices analysis of controller data.  Reads, interprets, and prints what is input
import serial
from geopy import distance
from picamera import PiCamera

#for oled:
import Adafruit_SSD1306
from PIL import Image
from PIL import ImageDraw
from PIL import ImageFont

def D2M(degrees):
    negative = False
    if degrees < 0:
        negative = True
        degrees = degrees * -1
    deg = degrees - (degrees%1)
    toMin= degrees%1 * 60
    min = toMin - (toMin%1)
    #sec = (toMin)%1 * 60
    sec = round((toMin)%1 * 60, 8)
    
    if negative == True:
        deg = deg * -1
    
    #print(deg)
    #print(min)
    #print(sec)
    
    return (deg,min,sec)

def twos_complement(value,bits):
    if value & (1 << (bits-1)):
        value -= 1 << bits
    return value

ser = serial.Serial(
        port='//dev/ttyS0',
        baudrate = 19200,
        parity=serial.PARITY_NONE,
        stopbits=serial.STOPBITS_ONE,
        bytesize=serial.EIGHTBITS,
        #timeout=1
)

##########################setupOLED
RST = None     # on the PiOLED this pin isnt used
disp = Adafruit_SSD1306.SSD1306_128_32(rst=RST)
disp.begin()
disp.clear()
disp.display()
width = disp.width
height = disp.height
image = Image.new('1', (width, height))
draw = ImageDraw.Draw(image)
draw.rectangle((0,0,width,height), outline=0, fill=0)
padding = -2
top = padding
bottom = height-padding
font = ImageFont.load_default()
################################



#initializing all necessary variables to 0
AFrame, pitch, roll, heading, GFrame, lat, lon, groundSpeed, altitude, sats, satsFix, points, distanceTraveled, point1, point2, imgNum = (0,)*16

distanceThreshold = 10 #meters

f = open("data.txt", "a")
camera = PiCamera()

while 1:
    x=ser.read()

    if x == b'$':
        x=ser.read()
        if x == b'T':
            x=ser.read()
            if x == b'A':
                #print('attitudeFrame')
                AFrame  = [ord(x) for x in ser.read(6)]
                pitch   = twos_complement(AFrame[0] + (AFrame[1] << 8), 16)
                roll    = twos_complement(AFrame[2] + (AFrame[3] << 8), 16)
                heading = twos_complement(AFrame[4] + (AFrame[5] << 8), 16)
                
                print 'pitch:   ', pitch
                print 'roll:    ', roll
                print 'heading: ', heading
            elif x == b'G':
                #print('gpsFrame')
                GFrame        = [ord(x) for x in ser.read(14)]
                lat           = twos_complement(GFrame[0] + (GFrame[1] << 8) + (GFrame[2] << 16) + (GFrame[3] << 24), 32)/float(10000000)
                lon           = twos_complement(GFrame[4] + (GFrame[5] << 8) + (GFrame[6] << 16) + (GFrame[7] << 24), 32)/float(10000000)
                groundSpeed   = GFrame[8]
                altitude      = twos_complement(GFrame[9] + (GFrame[10] << 8) + (GFrame[11] << 16) + (GFrame[12] << 24), 32)
                satsFix       = GFrame[13] & 0b00000011 #bits 0-1
                sats          = GFrame[13] >> 2
                
                #print 'lat           :', lat
                #print D2M(lat)
                #print 'lon:          :', lon
                #print D2M(lon)
                #print 'groundSpeed   :', groundSpeed
                #print 'altitude      :', altitude
                #print 'sats          :', sats
                
                if points == 0:
                    point1 = (lat, lon)
                    points += 1
                else:
                    point2 = (lat, lon)
                    distanceTraveled += distance.distance(point1, point2).m #meters
                    #print 'traveled: ', distanceTraveled, ' meters'
                    point1 = point2
					
                    #update OLED
                    draw.rectangle((0,0,width,height), outline=0, fill=0)
                    
                    draw.text((0, top),       "Sats : " + str(sats),  font=font, fill=255)
                    draw.text((0, top+8),     "Dist : " + str(distanceTraveled), font=font, fill=255)
                    draw.text((0, top+16),    "Fix  : " + str(satsFix),  font=font, fill=255)
                    draw.text((0, top+25),    "Pitch: " + str(pitch),  font=font, fill=255)
                    disp.image(image)
                    disp.display()
					
                    if distanceTraveled >= distanceThreshold:
                        distanceTraveled = 0
                        #take picture
                        imgNum += 1
                        imgName = 'IMG_' + str(imgNum) + '.jpg'
                        camera.capture(imgName)
                        #update data file with coordinates and altitude as shown here: https://support.pix4d.com/hc/en-us/articles/202558539-Input-Files#labelA1_1
                        f.write(imgName + ',' + str(lat) + ','+ str(lon) + ','+ str(altitude) + '\n')
                        
                        
                        
                        

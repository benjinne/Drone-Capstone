#this script practices analysis of controller data.  Reads, interprets, and prints what is input
import time
import serial
from geopy import distance

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

start = time.time()

#initializing all necessary variables to 0
AFrame, pitch, roll, heading, GFrame, lat, lon, groundSpeed, altitude, sats, points, distanceTraveled, point1, point2 = (0,)*14

distanceThreshold = 10 #meters

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
                
                #print 'pitch:   ', pitch
                #print 'roll:    ', roll
                #print 'heading: ', heading
            elif x == b'G':
                print('gpsFrame')
                GFrame        = [ord(x) for x in ser.read(14)]
                lat           = twos_complement(GFrame[0] + (GFrame[1] << 8) + (GFrame[2] << 16) + (GFrame[3] << 32), 32)/10000000
                lon           = twos_complement(GFrame[4] + (GFrame[5] << 8) + (GFrame[6] << 16) + (GFrame[7] << 32), 32)/10000000
                groundSpeed   = GFrame[8]
                altitude      = twos_complement(GFrame[9] + (GFrame[10] << 8) + (GFrame[11] << 16) + (GFrame[12] << 32), 32)
                sats          = GFrame[13]
                
                print 'lat           :', lat
                #print D2M(lat)
                print 'lon:          :', lon
                #print D2M(lon)
                #print 'groundSpeed   :', groundSpeed
                #print 'altitude      :', altitude
                #print 'sats          :', sats
                
                points += 1
                if points == 1:
                    point1 = (lat, lon)
                if points == 2:
                    points = 0
                    point2 = (lat, lon)
                    distanceTraveled = distanceTraveled + distance.distance(point1, point2).m #meters
                    print 'traveled: ', distanceTraveled, ' meters'
                    if distanceTraveled >= distanceThreshold:
                        distance = 0
                        #take picture
                    
                    
                    
                    

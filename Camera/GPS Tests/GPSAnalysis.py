#this script practices analysis of controller data.  Reads, interprets, and prints what is input
import time
import serial

def DegreesToStuff(degrees):
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
    
    print(deg)
    print(min)
    print(sec)
    
    return (deg,min,sec)

def twos_complement(hexstr,bits):
    value = int(hexstr,16)
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
while 1:
    x=ser.read()

    if x == b'$':
        x=ser.read()
        if x == b'T':
            x=ser.read()
            if x == b'G':
                print("new")
                #Latitude
                Lat1=ser.read().hex()
                Lat2=ser.read().hex()
                Lat3=ser.read().hex()
                Lat4=ser.read().hex()
                Lat = twos_complement(Lat4 + Lat3 + Lat2 + Lat1,32)/10000000
                print(Lat)
                
                #Longitude
                Long1=ser.read().hex()
                Long2=ser.read().hex()
                Long3=ser.read().hex()
                Long4=ser.read().hex()
                Long = Long4 + Long3+ Long2 + Long1
                print(twos_complement(Long, 32)/10000000)
                
                ser.read() #ignoring ground speed
                
                #Altitude
                x=ser.read()
                print(x)
                Alt1=int.from_bytes(x, byteorder='big')
                print("alt 1: " + str(Alt1))
                x=ser.read()
                print(x)
                Alt2=int.from_bytes(x, byteorder='big')
                print("alt 2: " + str(Alt2))
                x=ser.read()
                print(x)
                Alt3=int.from_bytes(x, byteorder='big')
                print("alt 3: " + str(Alt3))
                x=ser.read()
                print(x)
                Alt4=int.from_bytes(x, byteorder='big')
                print("alt 4: " + str(Alt4))
    
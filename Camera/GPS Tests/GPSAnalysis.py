#this script practices analysis of controller data.  Reads, interprets, and prints what is input
import time
import serial

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
                print(D2M(Lat))
                
                #Longitude
                Long1=ser.read().hex()
                Long2=ser.read().hex()
                Long3=ser.read().hex()
                Long4=ser.read().hex()
                Long = twos_complement(Long4 + Long3+ Long2 + Long1, 32)/10000000
                print(Long)
                print(D2M(Long))
                
                x = ser.read() #ignoring ground speed
                #print(x.hex())
                
                #Altitude
                Alt1=ser.read().hex()
                print(Alt1)
                Alt2=ser.read().hex()
                print(Alt2)
                Alt3=ser.read().hex()
                print(Alt3)
                Alt4=ser.read().hex()
                print(Alt4)
                Alt = twos_complement(Alt4 + Alt3 + Alt2 + Alt1, 32)/100
                print(Alt)
                
                x = ser.read() #ignoring ground speed
                #print(x.hex())
    
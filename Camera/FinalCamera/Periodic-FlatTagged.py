#this script takes images periodically, and modifies exif headers with dummy data.
import time
import serial

ser = serial.Serial(
        port='//dev/ttyS0',
        baudrate = 19200,
        parity=serial.PARITY_NONE,
        stopbits=serial.STOPBITS_ONE,
        bytesize=serial.EIGHTBITS,
        #timeout=1
)
active = 1  #if we want to take a picture from the safebit (data is valid)

start = time.time()
while 1:
    x=ser.read()
    #print(x)
    if x == b'$':
        print("true")
    
    end = time.time()
    if end - start > 5 and active == 1:
        print(end - start)
        start = time.time()
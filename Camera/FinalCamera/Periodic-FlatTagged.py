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
altitude = 0
longitude = 0
latitude = 0
start = time.time()
while 1:
        #x=ser.read()
        #print(x)
    end = time.time()
    if end - start > 5:
        print(end - start)
        start = time.time()
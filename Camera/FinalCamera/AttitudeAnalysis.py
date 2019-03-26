#this script practices analysis of controller data.  Reads, interprets, and prints what is input
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

start = time.time()
while 1:
    x=ser.read()

    if x == b'$':
        x=ser.read()
        if x == b'T':
            x=ser.read()
            if x == b'A':
                print("new")
                #pitch
                x=ser.read()
                y = x
                x=ser.read()
                z = int.from_bytes(x, byteorder='big')
                if z == 255:
                    y = int.from_bytes(y, byteorder='big', signed=True)
                    print("pitch: " + str(y))
                else:
                    print("pitch: " + str(y[0]))
                
                #roll
                x=ser.read()
                y = x
                x=ser.read()
                z = int.from_bytes(x, byteorder='big')
                if z == 255:
                    y = int.from_bytes(y, byteorder='big', signed=True)
                    print("roll: " + str(y))
                else:
                    print("roll: " + str(y[0]))
    
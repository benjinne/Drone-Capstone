#!/usr/bin/env python
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

while 1:
        x=ser.read()
        print(x)
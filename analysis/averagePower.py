import csv

with open('C:/Users/Benjamin/Downloads/LOG00083.01.csv', mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    line_count = 0
    watts = 0
    for row in csv_reader:
        volts = float(row[" vbatLatest (V)"])
        amps  = float(row[" amperageLatest (A)"])
        i     = int(row["loopIteration"])
        if (i > 6860) and (i < 334130):
            watts = watts + (volts * amps)
            line_count = line_count + 1
    
    average_watts = str(watts/line_count)
    print("average watts: " + average_watts)


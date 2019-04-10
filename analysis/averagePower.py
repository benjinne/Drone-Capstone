import csv

path = '' #enter file path here

with open(path, mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    line_count = 0
    volts = 0
    amps = 0
    for row in csv_reader:
        mode  = row[" navState"]
        if (mode == "   7"): #signifies position hold is activated 
            volts = volts + int(row[" vbat"])
            amps = amps + int(row[" amperage"])
            line_count = line_count + 1
    divisor = line_count*line_count*10000
    avgWatts = str((volts*amps)/divisor)
    print("average watts: " + avgWatts)
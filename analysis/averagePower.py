import csv

path = '' #enter file path here

with open(path, mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    line_count = 0
    watts = 0
    for row in csv_reader:
        volts = float(row[" vbat"])
        amps  = float(row[" amperage"])
        mode  = row[" navState"]
        if (mode == "   7"): #signifies position hold is activated
            watts = watts + (volts * amps)
            line_count = line_count + 1
    average_watts = str((watts/line_count)/10000)
    print("average watts: " + average_watts)
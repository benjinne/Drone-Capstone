import piexif
from PIL import Image

fname = "long1image2.jpg"

img = Image.open(fname)
exif_dict = piexif.load(img.info['exif'])

altitude = exif_dict['GPS'][piexif.GPSIFD.GPSLatitude]
print(altitude)

#EDITING______

#exif_dict['GPS'][piexif.GPSIFD.GPSLongitude] = (1400, 1)
#exif_bytes = piexif.dump(exif_dict)
#img.save('long1%s' % fname, "jpeg", exif=exif_bytes)



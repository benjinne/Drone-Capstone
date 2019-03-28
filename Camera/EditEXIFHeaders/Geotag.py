import piexif
from PIL import Image

fname = "long1image2.jpg"

img = Image.open(fname)
exif_dict = piexif.load(img.info['exif'])

altitude = exif_dict['GPS'][piexif.GPSIFD.GPSLatitude]
print(altitude)

#EDITING______

#exif_dict['GPS'][piexif.GPSIFD.GPSLongitude] = (1400, 1)
#exif_dict['GPS'][piexif.GPSIFD.GPSLatitude] = [((int)(round(100),6) * 1000000),1000000 ),(200, 1),(300, 1)]
#exif_dict['GPS'][piexif.GPSIFD.GPSLongitude] = [((int)(round(400),6) * 1000000),1000000 ),(500, 1),(600, 1)]
#exif_bytes = piexif.dump(exif_dict)
#img.save('long1%s' % fname, "jpeg", exif=exif_bytes)



package plantCountingPixel;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PlantCounting {
	//analyzes an image pixel by pixel.  Looks for green pixels (within tolerance) and marks these pixels.
	//Marks all nearby pixels at the same time.  If enough pixels are counted at once, add 1 to the plant count.

	static BufferedImage image;
	static final double tolerance = 0.5; //not the plant if red or blue is greater than half the green
	static final int minimumGreen = 120; //must have at least this much green to be a plant

	public static void main(String[] args) throws IOException{
		image = ImageIO.read(PlantCounting.class.getResource("testImage.png"));
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] red = new int[height][width];
		int[][] green = new int[height][width];
		int[][] blue = new int[height][width];

		int[][] marker = new int[height][width];
		ArrayList<Pixel> path = new ArrayList<Pixel>();
		int markCount = 0;
		int plantCount = 0;
		
		//collecting image data
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				red[y][x] = (image.getRGB(x, y)>>16) & 0xff;
				green[y][x] = (image.getRGB(x, y)>>8) & 0xff;
				blue[y][x] = (image.getRGB(x, y)>>0) & 0xff;
				marker[y][x] = 0;	//unmarked
			}
		}

		//analyzing data
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//conditions for detecting appropriate green of a plant
				if(marker[y][x] == 0 && green[y][x] > minimumGreen && red[y][x] < green[y][x]*tolerance && blue[y][x] < green[y][x]*tolerance){
					marker[y][x] = 1;
					path.add(new Pixel(x,y));
					
					//pathing through all nearby green
					int iterator = 0;
					while(path.size() > iterator){
						markCount++;
						int pathx = path.get(iterator).x;
						int pathy = path.get(iterator).y;
						
						//checking bordering pixels
						if(pathy - 1 > 0){
							if(marker[pathy-1][pathx] == 0 && green[pathy-1][pathx] > minimumGreen && red[pathy-1][pathx] < green[pathy-1][pathx]*tolerance && blue[pathy-1][pathx] < green[pathy-1][pathx]*tolerance){
								path.add(new Pixel(pathx, pathy-1));	//adding to path
								marker[pathy-1][pathx] = 1;
							}
						}
						if(pathy + 1 < height){
							if(marker[pathy+1][pathx] == 0 && green[pathy+1][pathx] > minimumGreen && red[pathy+1][pathx] < green[pathy+1][pathx]*tolerance && blue[pathy+1][pathx] < green[pathy+1][pathx]*tolerance){
								path.add(new Pixel(pathx, pathy+1));
								marker[pathy+1][pathx] = 1;
							}
						}
						if(pathy < width - 1){
							if(marker[pathy][pathx-1] == 0 && green[pathy][pathx-1] > minimumGreen && red[pathy][pathx-1] < green[pathy][pathx-1]*tolerance && blue[pathy][pathx-1] < green[pathy][pathx-1]*tolerance){
								path.add(new Pixel(pathx-1, pathy));
								marker[pathy][pathx-1] = 1;
							}
						}
						if(pathy < width + 1){
							if(marker[pathy][pathx+1] == 0 && green[pathy][pathx+1] > minimumGreen && red[pathy][pathx+1] < green[pathy][pathx+1]*tolerance && blue[pathy][pathx+1] < green[pathy][pathx+1]*tolerance){
								path.add(new Pixel(pathx+1, pathy));
								marker[pathy][pathx+1] = 1;
							}
						}
						
						iterator++;
					}
					
					if(markCount > 5){
						plantCount++;
						markCount = 0;
					}
				}
			}
		}
		
		System.out.println("Plant Count: " + plantCount);
	}
}

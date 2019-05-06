package plantCountingPixel;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import pumpkinCountingPixel.Pixel;

public class PlantCountingPixel {
	//analyzes an image pixel by pixel.  Looks for green pixels (within tolerance) and marks these pixels.
	//Marks all nearby pixels at the same time.  If enough pixels are counted at once (pixelTreshold), add 1 
	//to the plant count.
	
	//result.png shows results.  Red pixels are marked pixels (fit color requirement)
	//white pixels were red pixels that were grouped enough to count as a crop (enough red to satisfy pixelThreshold)

	static BufferedImage image;
	static final double tolerance = 0.5; //not the plant if red or blue is greater than half the green
	static final int minimumGreen = 120; //must have at least this much green to be a plant
	
	private static Map<Integer,ArrayList<Pixel>> groups;

	static final int pixelThreshold = 5;
	
	public static void main(String[] args) throws IOException{
		image = ImageIO.read(PlantCountingPixel.class.getResource("testImage.png"));
		final int width = image.getWidth();
		final int height = image.getHeight();
//		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] red = new int[height][width];
		int[][] green = new int[height][width];
		int[][] blue = new int[height][width];
		
		groups = new HashMap<Integer,ArrayList<Pixel>>();

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
						if(pathy - 1 >= 0){
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
						if(pathx - 1 >= 0){
							if(marker[pathy][pathx-1] == 0 && green[pathy][pathx-1] > minimumGreen && red[pathy][pathx-1] < green[pathy][pathx-1]*tolerance && blue[pathy][pathx-1] < green[pathy][pathx-1]*tolerance){
								path.add(new Pixel(pathx-1, pathy));
								marker[pathy][pathx-1] = 1;
							}
						}
						if(pathx + 1 < width){
							if(marker[pathy][pathx+1] == 0 && green[pathy][pathx+1] > minimumGreen && red[pathy][pathx+1] < green[pathy][pathx+1]*tolerance && blue[pathy][pathx+1] < green[pathy][pathx+1]*tolerance){
								path.add(new Pixel(pathx+1, pathy));
								marker[pathy][pathx+1] = 1;
							}
						}
						
						iterator++;
					}
					
					if(markCount > pixelThreshold){
						plantCount++;
						groups.put(groups.size(), path);
					}
					markCount = 0;
					path = new ArrayList<Pixel>();
				}
			}
		}
		
		System.out.println("Plant Count: " + plantCount);
		
		//printing pixel results
				BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				File file = new File("src/plantCountingPixel/result.png");
		        
		        Files.deleteIfExists(file.toPath());
		        
				//writing map
		        int count = 0;
		        for(int y = 0; y < height; y++){
					for(int x = 0; x < width; x++){
						if(marker[y][x] == 1){
							imageResult.setRGB(x, y, Color.red.getRGB());
							count++;
						}
						else{
							imageResult.setRGB(x, y, Color.black.getRGB());
						}
					}
				}
				System.out.println(count + "\n");
		        
				for(int i = 0; i < groups.size(); i++){
					ArrayList<Pixel> list = groups.get(i);
					System.out.println(list.size());
					for(int k = 0; k < list.size(); k++){
						imageResult.setRGB(list.get(k).x, list.get(k).y, Color.white.getRGB());
					}
				}

				//writing file
				try {
					ImageIO.write(imageResult, "png", file);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				
				
	}
}

package pumpkinCountingAdvanced;

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

public class PumpkinCountingAverage {
	//this algorithm does the byPixel analysis first.  Then it obtains the average number of pixels per group.
		//This average is considered to be about the size of a typical pumpkin.
	//All groups are divided by this average.  Clusters of pumpkins that connect will be divided, and approximated.
		//Round down the division.
	//This algorithm depends on having many groups of independent pumpkins, to get an accurate average.
	static BufferedImage image;

	//	static final int minimumRed = 217;
	//	static final int maximumRed = 255;
	static final int minimumRed = 150;
	static final int maximumRed = 255;
	//	static final int minimumGreen = 100;
	//	static final int maximumGreen = 170;
	static final int minimumGreen = 118 - 30;
	static final int maximumGreen = 118 + 50;
	static final int minimumBlue = 0;
	static final int maximumBlue = 120;

	static int[][] red;
	static int[][] green;
	static int[][] blue;

	static final int pixelThreshold = 10;

	static int width;
	static int height;
	static Pixel[][] pixelResults;
	static int plantCount;

	static Map<Integer,ArrayList<Pixel>> groups;

	public static void main(String[] args) throws IOException{
		image = ImageIO.read(PumpkinCountingAverage.class.getResource("Pumpkins3.jpg"));
		//actual count:
		//Pumpkins1 = 42, 39 not including ones cut off the screen
		//Pumpkins2 = ???
		//Pumpkins3 = 745, 730 not including cut off screen (personally counted)
		width = image.getWidth();
		height = image.getHeight();
		pixelResults = new Pixel[height][width];

		groups = new HashMap<Integer,ArrayList<Pixel>>();

		red = new int[height][width];
		green = new int[height][width];
		blue = new int[height][width];

		plantCount = 0;

		ArrayList<Pixel> path = new ArrayList<Pixel>();
		int markCount = 0;

		//collecting image data
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				red[y][x] = (image.getRGB(x, y)>>16) & 0xff;
				green[y][x] = (image.getRGB(x, y)>>8) & 0xff;
				blue[y][x] = (image.getRGB(x, y)>>0) & 0xff;
				pixelResults[y][x] = new Pixel(x,y);
			}
		}

		//analyzing data
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//conditions for detecting appropriate green of a plant
				if(pixelResults[y][x].color == false && green[y][x] > minimumGreen && green[y][x] < maximumGreen &&
						red[y][x] > minimumRed && red[y][x] < maximumRed && 
						blue[y][x] > minimumBlue && blue[y][x] < maximumBlue){
					pixelResults[y][x].color = true;
					path.add(pixelResults[y][x]);	//adding to path

					//pathing through all nearby green
					int iterator = 0;
					while(path.size() > iterator){
						markCount++;
						int pathx = path.get(iterator).x;
						int pathy = path.get(iterator).y;

						//checking bordering pixels
						if(pathy - 1 >= 0){
							if(pixelResults[pathy-1][pathx].color == false && green[pathy-1][pathx] > minimumGreen && green[pathy-1][pathx] < maximumGreen &&
									red[pathy-1][pathx] > minimumRed && red[pathy-1][pathx] < maximumRed && 
									blue[pathy-1][pathx] > minimumBlue && blue[pathy-1][pathx] < maximumBlue){
								path.add(pixelResults[pathy-1][pathx]);	//adding to path
								pixelResults[pathy-1][pathx].color = true;
							}
						}
						if(pathy + 1 < height){
							if(pixelResults[pathy+1][pathx].color == false && green[pathy+1][pathx] > minimumGreen && green[pathy+1][pathx] < maximumGreen &&
									red[pathy+1][pathx] > minimumRed && red[pathy+1][pathx] < maximumRed && 
									blue[pathy+1][pathx] > minimumBlue && blue[pathy+1][pathx] < maximumBlue){
								path.add(pixelResults[pathy+1][pathx]);
								pixelResults[pathy+1][pathx].color = true;
							}
						}
						if(pathx - 1 >= 0){
							if(pixelResults[pathy][pathx-1].color == false && green[pathy][pathx-1] > minimumGreen && green[pathy][pathx-1] < maximumGreen &&
									red[pathy][pathx-1] > minimumRed && red[pathy][pathx-1] < maximumRed && 
									blue[pathy][pathx-1] > minimumBlue && blue[pathy][pathx-1] < maximumBlue){
								path.add(pixelResults[pathy][pathx-1]);
								pixelResults[pathy][pathx-1].color = true;
							}
						}
						if(pathx + 1 < width){
							if(pixelResults[pathy][pathx+1].color == false && green[pathy][pathx+1] > minimumGreen && green[pathy][pathx+1] < maximumGreen &&
									red[pathy][pathx+1] > minimumRed && red[pathy][pathx+1] < maximumRed && 
									blue[pathy][pathx+1] > minimumBlue && blue[pathy][pathx+1] < maximumBlue){
								path.add(pixelResults[pathy][pathx+1]);
								pixelResults[pathy][pathx+1].color = true;
							}
						}

						iterator++;
					}

					if(markCount >= pixelThreshold){
						plantCount++;
						groups.put(groups.size(), path);

						//marking these as grouped
						for(iterator = 0; iterator < path.size(); iterator++){
							pixelResults[path.get(iterator).y][path.get(iterator).x].grouped = true;
						}
					}
					markCount = 0;
					path = new ArrayList<Pixel>();
				}

			}
		}

		System.out.println("Plant Count: " + plantCount);
		
		averagePixelDivider();

		//creating results image
		writeResultImage();

	}

	private static void averagePixelDivider(){
		double average = 0;
		for(int i = 0; i < groups.size(); i++){
			average = average + groups.get(i).size();
		}
		average = average/groups.size();
		
		int newCount = 0;
		for(int i = 0; i < groups.size(); i++){
			newCount = newCount + (int)Math.floor(groups.get(i).size()/average);
			if(groups.get(i).size()/average < 1){
				newCount++;
			}
			else if(groups.get(i).size()/average >= 2){
				for(int k = 0; k < groups.get(i).size(); k++){
					pixelResults[groups.get(i).get(k).y][groups.get(i).get(k).x].specialGrouped = true;
				}
			}
		}
		
		System.out.println("New plant Count: " + newCount);
	}
	
	private static void writeResultImage() throws IOException{
		//printing pixel results
		BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		File file = new File("src/pumpkinCountingAdvanced/result.png");

		Files.deleteIfExists(file.toPath());

		//writing map
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				if(pixelResults[y][x].specialGrouped){
					imageResult.setRGB(x, y, Color.green.getRGB());
				}
				else if(pixelResults[y][x].grouped){
					imageResult.setRGB(x, y, Color.white.getRGB());
				}
				else if(pixelResults[y][x].color){
					imageResult.setRGB(x, y, Color.red.getRGB());
				}
				else{
					imageResult.setRGB(x, y, Color.black.getRGB());
				}
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

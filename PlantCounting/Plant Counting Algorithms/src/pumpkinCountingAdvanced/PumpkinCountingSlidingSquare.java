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

public class PumpkinCountingSlidingSquare {
	//This algorithm needs to visually detect a pumpkin, or a chunk of pixels (marked or grouped, preferable marked)
	//then bound the pixels in a box. There are requirements for placing a box.
		//must contain a minimum of some number of pixels
		//Grouped pixels take priority
	//Squares are used for their simplicity.  However, ellipses would provided more accurate boundaries for pumpkins.
	
	//this method has the potential to detect groupings even when they were not grouped by bordering
		//this is currently a problem for the byPixel algorithm
	
	//Challenges:
		//detecting groups of pixels
		//dividing large groupings of pixels
		//determining where to place a bounding box where it doesn't knock out the potential of placing an extra box
			//on the same grouping
		//If anyone out there is familiar with this type of computation, by all means, take this baby over.
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
	static ArrayList<Rectangle> squares;

	static final int squareSize = 5;
	static final int minimumPixels = Math.round(Math.round(pixelThreshold*0.8));	//80% of pixelThreshold

	public static void main(String[] args) throws IOException{
		image = ImageIO.read(PumpkinCountingSlidingSquare.class.getResource("Pumpkins3.jpg"));
		//actual count:
		//Pumpkins1 = 42, 39 not including ones cut off the screen
		//Pumpkins2 = ???
		//Pumpkins3 = 745, 730 not including cut off screen
		width = image.getWidth();
		height = image.getHeight();
		pixelResults = new Pixel[height][width];

		groups = new HashMap<Integer,ArrayList<Pixel>>();
		squares = new ArrayList<Rectangle>();

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

		//grouping pixels into squares
				slidingBoxDetection();
				System.out.println("New plant Count: " + squares.size());

		//creating results image
		writeResultImage();

	}

	private static void slidingBoxDetection(){	//unfinished
		Rectangle target = new Rectangle();
		target.setBounds(0, 0, squareSize, squareSize);

		for(int x = 0; x < height-squareSize; x++){
			for(int y = 0; y < width-squareSize; y++){
				target.setBounds(x, y, squareSize, squareSize);
				boolean overlap = false;

				int count = 0;
				for(int i = x; i < x+squareSize; i++){
					for(int k = y; k < y+squareSize; k++){
						if(pixelResults[x][y].color){
							count++;
						}
					}
				}
				if(count > minimumPixels){
					squares.add(target);
					y = y + squareSize-1;
					if(y+1 < width-squareSize){
						x = x + squareSize-1;
					}
				}
			}
		}
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

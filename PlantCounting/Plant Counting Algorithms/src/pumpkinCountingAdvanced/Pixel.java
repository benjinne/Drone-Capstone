package pumpkinCountingAdvanced;

public class Pixel {
	
	public int x;
	public int y;
	public boolean color;		//false when not the right color, true when pumpkin color
	public boolean grouped;		//true when it has been involved in a group that counts as a pumpkin
	public boolean specialGrouped;
	
	public Pixel(int xx, int yy){
		x = xx;
		y = yy;
		color = false;
		grouped = false;
		specialGrouped = false;
	}
}

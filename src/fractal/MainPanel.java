package fractal;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MainPanel extends JPanel{
	public int[] dankness;
	public int width;
	public int length;
	public int maxTimes;
	
	public MainPanel(int[] dankness, int width, int length, int maxTimes) {
		this.dankness = dankness;
		this.width = width;
		this.length = length;
		this.maxTimes = maxTimes;
	}
	protected void paintComponent(Graphics g){
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < length; j++) {
				g.setColor(new Color(255 - dankness[i * length + j]*255/maxTimes, 255 - dankness[i * length + j]*255/maxTimes, 255 - dankness[i * length + j]*255/maxTimes));
				g.fillRect(i, j, 1, 1);
			}
		}
		//System.out.println("hiiiiiiiii");
	}
}

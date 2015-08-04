import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class DisplayPanel extends JPanel{
	
//	
	BufferedImage bi;
	public DisplayPanel() {
		super();
		setLayout(null);
		setBounds(0, 0, DanceBot.width, DanceBot.height);
		setVisible(true);
		setPreferredSize(new Dimension(DanceBot.width, DanceBot.height));
	}

	public void setImage(BufferedImage i){
		this.bi=i;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(bi!=null){
			g.setColor(new Color(255,255,255));
			g.drawImage(bi, 0, 0, null);
		}
		else{
		g.setColor(new Color(255,255,0,255));
		g.fillRect(0, 0, 500, 300);
		}
	}

}

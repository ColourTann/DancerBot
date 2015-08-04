import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;


public class DanceBot {
	static Robot r;
	static Clip clip=null;
	static JFrame frame;
	static DisplayPanel dp;
	static int width=960, height=540;
	static int posX = 480, posY = 341;
	static Rectangle rect = new Rectangle (posX, posY, width, height);

	public static void main(String[] args) {
		dp=new DisplayPanel();
		frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(width+frame.getInsets().left+frame.getInsets().right, height+frame.getInsets().top+frame.getInsets().bottom);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setLocation(2000, 50);
		frame.getContentPane().add(dp);


		setupSound();


		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		synchronized (r) {
			robotStuff();	
		}

	}


	static int[] keys = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN};
	static float speed=0;
	static long calibration;
	static int prevX;
	static float[] calibrations= new float[10];
	static int calibrationIndex=0;
	static boolean calibrated;
	static synchronized void robotStuff() {


		while(true){
			try {
				r.wait(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			BufferedImage image = r.createScreenCapture(rect);
			dp.setImage(image);
			dp.repaint();
			long t=System.currentTimeMillis();
			for(int x=width/2;x>=0;x--){
				image.getRGB(x, 1);
			}
			System.out.println(t-System.currentTimeMillis());
			if(true)return;
			//get bar location//
			int location=0;
			for(int x=width/2;x>=0;x--){
				int rgb = image.getRGB(x, 502);
				Color c = new Color(rgb);
				if(c.getBlue()==255||c.getBlue()==189){
					location=x;
					break;
				}
			}


			//calibration//
			if(!calibrated){
				if(speed==0){
					if(calibration==0){
						calibration=System.currentTimeMillis();
						prevX=location;
					}
					else{
						long dt = System.currentTimeMillis()-calibration;
						long dx = location-prevX;
						float seconds = dt/1000f;
						speed=(location-prevX)/seconds;
						if(speed<=50){
							speed=0;
							calibration=0;
							continue;
						}

						System.out.println(dx+":"+dt+":"+speed);
						calibrations[calibrationIndex]=speed;
						calibrationIndex++;
						calibration=0; speed=0;
						if(calibrationIndex==calibrations.length){
							calibrated=true;
							speed=0;
							for(float f:calibrations){
								speed+=f;
							}
							speed/=calibrations.length;
							System.out.println("FINSIHED CALIBRATING\nSPEED: "+speed);
						}
					}
					continue;
				}
			}
			
			float pixelsToGo=width/2-location;
			System.out.println(pixelsToGo+":"+speed);
			System.out.println("waiting: "+(int)(pixelsToGo*speed)/1000);
			try {
				r.wait((int)(pixelsToGo*speed)/100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int key = keys[(int) (Math.random()*4)];
			r.keyPress(key);
			r.keyRelease(key);
			
			try {
				r.wait(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			//			if(found){
			//				int key = keys[(int) (Math.random()*4)];
			//				r.keyPress(key);
			//				r.keyRelease(key);
			//				//				playSound();
			//				try {
			//					r.wait(50);
			//				} catch (InterruptedException e) {
			//					e.printStackTrace();
			//				}
			//			}



		}
		
	}

	private static void setupSound() {

	}


	public static void playSound(){
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File("D:/audio/sniper.wav").getAbsoluteFile());
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		try {
			clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		try {
			clip.open(audioInputStream);
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
		clip.start();
	}
}

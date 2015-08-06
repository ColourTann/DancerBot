package dance;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
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
import javax.swing.RepaintManager;


public class DanceBot {
	static Robot r;
	static Clip clip=null;
	static JFrame frame;
	public static DisplayPanel dp;
	static int width=960, height=540;
	static int posX = 480, posY = 341;
	static Rectangle rect = new Rectangle (posX, posY, width, height);

	enum Character{Bard, Cadence}
	static Character myChar=Character.Cadence;
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

	static int prevX;


	static synchronized void robotStuff() {
		//
		//		BufferedImage image = capture();
		//		dp.setImage(image);
		//		dp.map.setupPath();
		if(myChar==Character.Cadence){
			speed=calibrate();
		}

		while(true){
			BufferedImage image = capture();
			dp.setImage(image);
			if(dp.map.playerTile!=null)break;
		}


		while(true){
			roboCycle();
		}


	}
	
	
	static long time = System.currentTimeMillis();
	static long previousWait=0;
	private static void roboCycle() {
		String s ="";
		try {
			r.wait(1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		BufferedImage image = capture();
		dp.setImage(image);
		s+=("Set Image "+getTime()+"||");
		int key = dp.map.chooseDirection();
		s+="Choose direction "+getTime()+"||";
		dp.map.repaint();
		s+="Paint "+getTime()+"||";
		long toWait=100;
		if(myChar==Character.Cadence){
			int location=getBarPosition(image);
			
			float pixelsToGo=width/2-location;
			toWait=(long) (pixelsToGo/speed-80);
			if(location==-99){
				toWait=previousWait;
			}
		}
		previousWait=toWait;


		try {
			if(toWait>0)r.wait(Math.max(1, toWait-20));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		s+="waiting "+getTime()+"||";
		r.keyPress(key);
		r.keyRelease(key);
		if(dp.map.keyPress(key)){
			robotStuff();

			return;
		}
		dp.map.setupPath();
		s+="Path "+getTime()+"||";
		try {	
			r.wait((long) (250));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.println(s);
	}

	static long getTime(){
		long current = System.currentTimeMillis();
		long result= current-time;
		time=current;
		return result;
	}

	private static float calibrate() {
		float[] calibrations= new float[20];
		int calibrationIndex=0;
		long calibrationTime;
		int location=0;
		int prevX=0;
		calibrationTime=System.currentTimeMillis();
		while(true){
			
			prevX=location;
			location = getBarPosition(capture());
			long dt = System.currentTimeMillis()-calibrationTime;
			calibrationTime=System.currentTimeMillis();
			long dx = location-prevX;
			float seconds = dt/1000f;
			speed=dx/seconds;
			if(speed<=50||speed>=1500){
				speed=0;
				continue;
			}
			calibrations[calibrationIndex]=speed;
			calibrationIndex++;
			speed=0;
			if(calibrationIndex==calibrations.length){
				speed=0;
				for(float f:calibrations){
					speed+=f;
				}
				System.out.println(speed+":"+calibrations.length);
				speed/=calibrations.length;
				speed/=1000f;
				System.out.println("FINSIHED CALIBRATING\nSPEED: "+speed);
				return speed;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static BufferedImage capture(){
		return r.createScreenCapture(rect);
	}

	static int getBarPosition(BufferedImage image){
		for(int x=width/2;x>=0;x--){
			int rgb = image.getRGB(x, 502);
			Color c = new Color(rgb);
			if(c.getBlue()==255||c.getBlue()==189){
				return x;
			}
		}
		return -99;
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

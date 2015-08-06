package dance;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import dance.Tile.TileType;




public class Map extends JPanel{

	static int mapTileSize=4;
	Tile[][] tiles = new Tile [map.width/mapTileSize][map.height/mapTileSize];

	Tile playerTile;
	public static int screenTileSize=48;
	public static int tilesAcross=34;
	public static int tilesDown=36;

	static Rectangle map = new Rectangle(802, 321, mapTileSize*tilesAcross, mapTileSize*tilesDown);

	static int left = DanceBot.width/2-screenTileSize/2-screenTileSize*(int)(tilesAcross/2);
	static int right =DanceBot.width/2+screenTileSize*tilesAcross/2+screenTileSize/2;

	static int bot = DanceBot.height/2-screenTileSize*(int)(tilesDown/2);
	static int top = DanceBot.height/2+screenTileSize/2+screenTileSize*(int)(tilesDown/2);

	static int visibleTilesAcross=18;
	static int visibleTilesDown=10;;

	public static Map self;
	public Map() {
		super();
		self=this;
		setOpaque(false);
		setLayout(null);
		setBounds(0, 0, DanceBot.width, DanceBot.height);
		for(int x=0;x<tiles.length;x++){
			for(int y=0;y<tiles[0].length;y++){
				tiles[x][y]=new Tile(x,y);
			}
		}
	}
	public static BufferedImage currentImage;
	public void updateMap(BufferedImage image){
		currentImage=image;
		for(int x=0;x<map.width;x+=mapTileSize){
			for(int y=0;y<map.height;y+=mapTileSize){
				int pixel = image.getRGB(map.x+x+1, map.y+y+1);
				tiles[x/mapTileSize][y/mapTileSize].color(pixel);
			}
		}
	}

	public int getTileColor(Tile t, int offsetX, int offsetY){
		if(playerTile==null)return 0;
		Point p = getTilePosition(t);
		p.x+=offsetX; p.y+=offsetY;
		p.y++;
		if(p.x<0||p.x>DanceBot.width||p.y<0||p.y>DanceBot.height)return 0;
		return(currentImage.getRGB(p.x,p.y));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(playerTile!=null){
			for(int x=-visibleTilesAcross/2;x<=visibleTilesAcross/2;x++){
				for(int y=-visibleTilesDown/2;y<visibleTilesDown/2;y++){
					Tile t = getTile(playerTile.x+x, playerTile.y+y);
					if(t==null)continue;
					Point drawAt = getTilePosition(t);
					//					t.drawBig(g, left+(x+tilesAcross/2)*screenTileSize, bot+(y+tilesDown/2)*screenTileSize);
					t.drawBig(g, drawAt.x, drawAt.y);
				}
			}
		}
	}

	static Point result = new Point();
	public Point getTilePosition(Tile t){
		int dx = t.x-playerTile.x;
		int dy = t.y-playerTile.y;
		result.x=left+(dx+tilesAcross/2)*screenTileSize;
		result.y=bot+(dy+tilesDown/2)*screenTileSize;


		return result;
	}

	public void centerOnPlayer(Tile tile) {
		playerTile=tile;
	}

	public boolean keyPress(int key) {
		int dx=0;
		int dy=0;
		switch(key){
		case KeyEvent.VK_LEFT:
			dx=-1;
			break;
		case KeyEvent.VK_RIGHT:
			dx=1;
			break;
		case KeyEvent.VK_UP:
			dy=-1;
			break;
		case KeyEvent.VK_DOWN:
			dy=1;
			break;
		}
		Tile t = getTile(playerTile.x+dx, playerTile.y+dy);
		if(t.canMoveTo()){
			t.visit();
			if(t.type==TileType.Exit) return true;
			playerTile=t;
		}
		return false;

	}

	public Tile getTile(int x, int y){
		if(x>=tiles.length||x<0||y>=tiles[0].length||y<0)return null;
		return tiles[x][y];

	}

	public void path(Tile t, int distance){
		t.resetPathing();
		if(distance>20)return;
		if(t.distance<distance)return;
		t.setDistance(distance);
		if(!t.canMoveFrom())return;
		if(t!=playerTile&&t.isNextToPlayer()&&t.threatened)return;
		for(int x=-1;x<=1;x++){
			for(int y=-1;y<=1;y++){
				if(Math.abs(x)+Math.abs(y)!=1)continue;
				Tile adjacent = getTile(t.x+x, t.y+y);
				if(adjacent==null)continue;
				if(!adjacent.canMoveTo())adjacent.setDistance(Math.min(adjacent.distance, distance+1));
				if(adjacent.canMoveTo()) path(adjacent,distance+1);
			}
		}
	}

	private void clearPath() {
		for(int x=0;x<tiles.length;x++){
			for(int y=0;y<tiles[0].length;y++){
				tiles[x][y].clearPath();
			}
		}
	}

	public int pathToBestTile(){
		if(playerTile==null){
			return KeyEvent.VK_LEFT;
		}

		Tile best = getBestTile(false);
		ArrayList<Tile> path = playerTile.setupPathTo(best); 
		Tile next = path.get(path.size()-2);
		int dx= next.x-playerTile.x;
		int dy=next.y-playerTile.y;

		if(dx==1)return KeyEvent.VK_RIGHT;
		if(dx==-1)return KeyEvent.VK_LEFT;
		if(dy==1)return KeyEvent.VK_DOWN;
		if(dy==-1)return KeyEvent.VK_UP;
		System.out.println("PATHING ERROR");
		return KeyEvent.VK_LEFT;
	}

	public Tile getBestTile(boolean wordy){
		Tile best = null;
		float bestValue=-9999;
		for(int x=0;x<tiles.length;x++){
			for(int y=0;y<tiles[0].length;y++){
				Tile t= getTile(x, y);
				if(t.distance>100)continue;
				int value = t.getValue();
				if(wordy)System.out.println(t+":"+t.getValue());
				if(t==null||t==playerTile)continue;
				if(t.type==TileType.Blank)continue;
				
				if(t.getValue()>bestValue||(t.getValue()==bestValue&&Math.random()>.5)){
					best=t;
					bestValue=t.getValue();
				}
			}
		}
		if(best==null&&!wordy)getBestTile(true);
		if(wordy) System.out.println("chosen "+best+": "+best.getValue());
		return best;
	}

	public int chooseDirection() {
		setupPath();
		if(playerTile.visited>5&&Math.random()>.75){
			clearVisited();
			return new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN}[(int) (Math.random()*4)];
		}
		
		return pathToBestTile();
	}

	private void clearVisited() {
		for(int x=0;x<tiles.length;x++){
			for(int y=0;y<tiles[0].length;y++){
				Tile t= getTile(x, y);
				if(t!=null)t.visited=0;
			}
		}
	}

	public void setupPath() {
		clearPath();
		for(int x=0; x<tiles.length; x++){
			for(int y=0; y<tiles.length; y++){
				tiles[x][y].tick();

			}	
		}
		if(playerTile!=null)path(playerTile, 0);
	}
}

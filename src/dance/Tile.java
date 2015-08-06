package dance;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;


public class Tile {
	public enum TileType{Blank, Wall0, Wall1, Ground, Player, Bedrock, Door, Trap, Wall2, Water, Enemy, Altar, Treasure, Exit, ShopWall, TreasureChest}
	static HashMap<Integer, TileType> minimapColors = new HashMap<>();
	static HashMap<Integer, EnemyType> enemyCols = new HashMap<>();
	static HashMap<Integer, Wall0Type> wallCols = new HashMap<>();
	static{
		minimapColors.put(-16777216, TileType.Blank);
		minimapColors.put(-6590168, TileType.Wall0);
		minimapColors.put(-8761819, TileType.Wall1);
		minimapColors.put(-2321579, TileType.Wall2);
		minimapColors.put(-9129742, TileType.Water);
		minimapColors.put(-3489879, TileType.Ground);
		minimapColors.put(-16776961, TileType.Player);
		minimapColors.put(-9539986, TileType.Bedrock);
		minimapColors.put(-16711828, TileType.Door);
		minimapColors.put(-3680001, TileType.Trap);
		minimapColors.put(-2022144, TileType.Enemy);
		minimapColors.put(-1, TileType.Altar);
		minimapColors.put(-7168, TileType.Treasure);
		minimapColors.put(0xff8400ff>>8, TileType.TreasureChest);
		minimapColors.put(0xc59700ff>>8, TileType.ShopWall);
		minimapColors.put(0xf100ffff>>8, TileType.Exit);


		enemyCols.put(0xc89647ff>>8, EnemyType.Barrel);
		enemyCols.put(0xe3b24dff>>8, EnemyType.Barrel);

//		wallCols.put(0x000000ff>>8, Wall0Type.Metal);
		wallCols.put(0x303028ff>>8, Wall0Type.Metal);
		wallCols.put(0xff444338, Wall0Type.Metal);
	}

	public enum EnemyType{Barrel};
	EnemyType enemyType;

	public enum Wall0Type{Metal};
	Wall0Type wallType;

	static HashMap<TileType, Color> typeCols = new HashMap<>();

	static{
		int alpha= 100;
		typeCols.put(TileType.Blank, transp(Color.BLACK,alpha));
		typeCols.put(TileType.Bedrock, transp(Color.GRAY, alpha));
		Color brown = new Color(255,150,0,alpha);
		typeCols.put(TileType.Wall0, brown);
		typeCols.put(TileType.Wall1, brown);
		typeCols.put(TileType.Wall2, brown);
		typeCols.put(TileType.Ground, transp(Color.green, alpha));
		typeCols.put(TileType.Water, transp(Color.blue, alpha));
		typeCols.put(TileType.Player, transp(Color.pink, alpha));
		typeCols.put(TileType.Door, transp(Color.orange, alpha));
		typeCols.put(TileType.Enemy, transp(Color.red, alpha));
		typeCols.put(TileType.Trap, transp(Color.magenta, alpha));
		typeCols.put(TileType.Altar, transp(Color.white, alpha));
		typeCols.put(TileType.Treasure, transp(Color.yellow, alpha));
	}





	static Color transp(Color c, int alpha){
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	int x, y;
	TileType type=null;
	public boolean visited;
	public Tile(int x, int y){
		this.x=x;this.y=y;
	}

	public void visit(){
		visited=true;
	}
	int distance;
	public void setDistance(int distance){
		this.distance=distance;
	}

	

	public void color(int color){
		TileType newType=minimapColors.get(color);
		if(newType==null)return;
		if(type!=null&&newType==TileType.Blank)return;
		this.type=newType;
		switch(this.type){
		case Altar:
			break;
		case Bedrock:
			break;
		case Blank:
			break;
		case Door:
			break;
		case Enemy:
			if(enemyType==null){
				enemyType =enemyCols.get(Map.self.getCenter(this, 0, 0));
			}


			break;
		case Ground:
			break;
		case Player:
			DanceBot.dp.map.centerOnPlayer(this);
			break;
		case Trap:
			break;
		case Wall0:
			if(wallType==null){
				wallType =wallCols.get(Map.self.getCenter(this,-4,-4));
//				System.out.println(x+":"+y+":"+Integer.toHexString(Map.self.getCenter(this,-4, -4)));
//				System.out.println(0xff444338+":"+(Map.self.getCenter(this,-4,-4)));
				System.out.println(wallType);
				
			}
			break;
		case Wall1:
			break;
		case Wall2:
			break;
		case Water:
			break;
		default:

			break;

		}
	}

	public void drawBig(Graphics g, int x, int y){
		g.setColor(typeCols.get(type));		
		g.fillRect(x, y, Map.screenTileSize, Map.screenTileSize);
		if(type==TileType.Blank||type==null)return;
		g.setColor(Color.WHITE);
		//		g.drawString("val: "+value, x, y+Map.mapTileSize/2+20);
		g.drawString(toString(), x, y+Map.mapTileSize/2+20);
		//		g.drawString("dist: "+distance, x, y+Map.mapTileSize/2-40);
				g.drawString(this.x+":"+this.y, x, y+Map.mapTileSize/2-50);
		if(visited){
			g.setColor(new Color(0,0,0,200));
			g.fillRect(x, y, Map.screenTileSize, Map.screenTileSize);
		}
		if(special){
			g.setColor(new Color(200,200,0,200));
			g.drawRect(x, y, Map.screenTileSize, Map.screenTileSize);
		}
	}

	public String toString(){
		return type.toString();
	}

	public boolean moveIn() {
		switch(type){
		case Altar:
		case Bedrock:
		case Blank:
		case Door:
		case Enemy:
		case Wall0:
		case Wall1:
		case Wall2:
			return false;
		case Ground:
		case Player:
		case Trap:
		case Treasure:
		case Water:
			return true;
		default:
			System.out.println(type+" move in not defined");
			return false;
		}
	}

	public void clearPath() {
		distance=999;
		//		value=0;
	}

	public boolean canMoveFrom() {
		if(type==null)return false;
		switch(type){

		case Ground:
		case Player:
		case Trap:
		case Treasure:
			return true;
		default:
			return false;

		}
	}
	float value;
	public int getValue(){
		int total = -distance;
		if(type==null){
			return-99990;
		}
		switch(type){
		case Altar:
			total=-1999;
		case ShopWall:
			total=-1999;
			break;
		case TreasureChest:
			total=-1999;
			break;
		case Bedrock:
			total=-1999;
		case Blank:
			total=-1999;
		case Door:
			total += 20;
			break;
		case Exit:
			total += 4;
			break;
		case Enemy:
			if(enemyType!=null){
				switch(enemyType){
				case Barrel:
					total=-10000;
				default:
					break;

				}
			}
			total+=20;
			break;
		case Ground:
			for(int x=-1;x<=1;x++){
				for(int y=-1;y<=1;y++){
					if(Math.abs(x)+Math.abs(y)!=1)continue;
					Tile adjacent = Map.self.getTile(this.x+x, this.y+y);
					if(adjacent==null)continue;
					if(adjacent.type==TileType.Blank){
						total+=10;
						break;
					}
				}
			}
			total+=0;
			break;
		case Player:
			total-=5;
			break;
		case Trap:
			total-=500;
			break;
		case Treasure:
			total-=500;
			break;
		case Wall0:
			total-=100;
			if(wallType!=null){
				switch(wallType){
				case Metal:
					if(!visited)total+=1999;
				default:
					break;

				}
			}
			break;
		case Wall1:
			return -999;
		case Wall2:
			return -999;
		case Water:
			return -999;
		default:
			break;
		}

		if(type!=TileType.Enemy&&visited)total-=500;
		return total;		
	}

	public boolean canMoveTo() {
		if(type==null)return false;
		switch(type){
		case Ground:
		case Player:
		case Treasure:
			return true;
		default:
			return false;

		}
	}

	static Tile previousTarget;
	public ArrayList<Tile> setupPathTo(Tile t){
		if(previousTarget!=null)previousTarget.resetPathing();
		previousTarget=t;
		resetPathing();
		t.resetPathing();
		for(Tile r:living){
			r.resetPathing();
		}
		for(Tile r:dead){
			r.resetPathing();
		}
		dead.clear();
		living.clear();
		living.add(this);
		t.resetPathing();
		return pathTo(t);
	}

	void resetPathing(){
		previous=null;
		pathDistance=0;
		stepsTaken=0;
		special=false;
	}

	static ArrayList<Tile> dead = new ArrayList<>();
	static ArrayList<Tile> living = new ArrayList<>();
	Tile previous;

	int pathDistance;
	int stepsTaken=0;
	public boolean special;
	public ArrayList<Tile> pathTo(Tile target){
		while(true){
			Tile pathFrom = living.remove(0);
			dead.add(pathFrom);
			for(int x=-1;x<=1;x++){
				for(int y=-1;y<=1;y++){
					if(Math.abs(x)+Math.abs(y)!=1)continue;
					Tile potential= Map.self.getTile(pathFrom.x+x, pathFrom.y+y);
					if(potential==null) continue;
					if(dead.contains(potential))continue;
					potential.previous=pathFrom;
					potential.stepsTaken=pathFrom.stepsTaken+1;
					if(potential==target){
						return unwind(target);
					}
					if(!potential.canMoveFrom()||!potential.canMoveTo())continue;
					potential.pathDistance =potential.getTileDistance(target);
					potential.stepsTaken=pathFrom.stepsTaken+1;
					int index=living.size();
					for(int i=0;i<living.size();i++){
						Tile compare=living.get(i);
						if(potential.pathDistance+potential.stepsTaken<compare.pathDistance+compare.stepsTaken){
							index = i;
							break;
						}
					}
					dead.add(potential);
					living.add(index, potential);
				}
			}
		}
	}

	int getTileDistance(Tile other){
		return Math.abs(other.x-this.x)+Math.abs(other.y-this.y);
	}

	int getCrowDistance(Tile other){
		int dx = other.x-this.x; int dy=other.y-this.y;
		return dx*dx+dy*dy;
	}

	public ArrayList<Tile> unwind(Tile solution){

		ArrayList<Tile> result = new ArrayList<>();
		result.add(solution);
		Tile t = solution;
		while(t.previous!=null){
			t.special=true;
			result.add(t.previous);
			t=t.previous;
		}
		solution.resetPathing();
		solution.special=true;
		return result;
	}

}

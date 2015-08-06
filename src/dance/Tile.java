package dance;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;


public class Tile {
	public enum TileType{Blank, Wall0, Wall1, Ground, Player, Bedrock, Door, Trap, Wall2, Water, Enemy, Altar, Treasure, Exit, ShopWall, TreasureChest, ShopKeeper}
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
		minimapColors.put(0xffff8400, TileType.TreasureChest);
		minimapColors.put(0xffc59700, TileType.ShopWall);
		minimapColors.put(0xff459361, TileType.ShopKeeper);
		minimapColors.put(0xfff100ff, TileType.Exit);


		enemyCols.put(0xc89647ff>>8, EnemyType.Barrel);
		enemyCols.put(0xe3b24dff>>8, EnemyType.Barrel);

		//		wallCols.put(0x000000ff>>8, Wall0Type.Metal);
		wallCols.put(0xff303028, Wall0Type.Metal);
		//		wallCols.put(0xff444338, Wall0Type.Metal);
		//		wallCols.put(0xff171512, Wall0Type.Metal);
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
		typeCols.put(TileType.TreasureChest, transp(Color.pink, alpha));
		typeCols.put(TileType.ShopWall, transp(Color.gray, alpha));
		typeCols.put(TileType.ShopKeeper, transp(Color.gray, alpha));
	}





	static Color transp(Color c, int alpha){
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	int x, y;
	TileType type=null;
	public int visited;
	public Tile(int x, int y){
		this.x=x;this.y=y;
	}

	public void visit(){
		if(monster!=null)monster.attack();
		visited++;
	}
	int distance;
	public void setDistance(int distance){
		this.distance=distance;
	}


	Monster monster;
	public void color(int color){
		TileType newType=minimapColors.get(color);
		if(newType==null)return;
		if(type!=null&&newType==TileType.Blank)return;
		if(newType!=TileType.Enemy)monster=null;
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
			this.type=TileType.Ground;
			if(monster==null)monster = new Monster(x,y);
			else monster.noMove();
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
				int col = (Map.self.getTileColor(this,0,-0));
				wallType =wallCols.get(Map.self.getTileColor(this,0,-0));				
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
		//		if(this.x==18&&this.y==19)return;
		g.setColor(typeCols.get(type));		
		if(type==null)return;
		if(monster!=null)g.setColor(new Color(255,0,0,50));
		g.fillRect(x, y, Map.screenTileSize, Map.screenTileSize);
		if(type==TileType.Blank||type==null)return;
		g.setColor(Color.WHITE);
//						g.drawString(""+getValue(), x, y+Map.mapTileSize/2+20);
		//		if(type!=TileType.Ground)g.drawString(toString(), x, y+Map.mapTileSize/2+20);
		//		g.drawString("dist: "+distance, x, y+Map.mapTileSize/2-40);
//		g.drawString(""+threatened, x, y+Map.mapTileSize/2+40);
//		g.drawString(this.x+":"+this.y, x, y+Map.mapTileSize/2+40);
		if(visited>0){
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
		if(monster!=null)return false;
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
	boolean threatened;
	public void clearPath() {
		distance=999;
		threatened=false;
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
	
	public int getValue(){
		int total = -distance;
		if(type==null){
			return-99990;
		}

		if(monster!=null) total+=monster.getValue();
		
		switch(type){
		case Altar:
		case ShopWall:
		case ShopKeeper:
		case TreasureChest:
		case Bedrock:
			total=-1999;
			break;
		case Door:
			total += 20;
			break;
		case Exit:
			total += 4;
			break;
		case Ground:
			boolean moreTiles=false;
			for(int x=-1;x<=1;x++){
				for(int y=-1;y<=1;y++){
					if(Math.abs(x)+Math.abs(y)!=1)continue;
					Tile adjacent = Map.self.getTile(this.x+x, this.y+y);
					if(adjacent==null)continue;
					if(adjacent.type==TileType.Blank){
						moreTiles=true;
						break;
					}
				}
			}
			total+=moreTiles?50:-5000;
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
//			if(Map.self.playerTile.visited>4){
//				return total+=9999999;
//			}
			total-=100;
			int within1=0;
			int within2=0;
			for(int x=-2;x<=2;x++){
				for(int y=-2;y<=2;y++){

					int dist = Math.abs(x)+Math.abs(y);
					if(dist<=2){
						Tile t = Map.self.getTile(this.x+x, this.y+y);
						if(t!=null && (t.type==TileType.Ground||t.type==TileType.Player)){
							if(!(x==0||y==0)){
								within1=888;
							}
							if(dist==1)within1++;
							if(dist==2)within2++;
						}
					}

				}
			}
			if(within1==1&&within2==1)total+=110;

			//			if(wallType!=null){
			//				switch(wallType){
			//				case Metal:
			//					if(!visited)total+=1999;
			//				default:
			//					break;
			//
			//				}
			//			}
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
					if(potential.threatened&&potential.isNextToPlayer())continue;
					potential.pathDistance =potential.getTileDistance(target);
					potential.stepsTaken=pathFrom.stepsTaken+1;
					int index=living.size();
					for(int i=0;i<living.size();i++){
						Tile compare=living.get(i);
						int diff=(compare.pathDistance+compare.stepsTaken)-(potential.pathDistance+potential.stepsTaken);
						if(diff>0||(diff==0&&Math.random()>.5)){
//						if(potential.pathDistance+potential.stepsTaken<compare.pathDistance+compare.stepsTaken){
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

	public void threaten() {
		threatened=true;
	}

	public void tick() {
		if(monster!=null)monster.threatenSquares();
	}

	public boolean isNextToPlayer() {
		Tile player = Map.self.playerTile;
		int dx=player.x-x;
		int dy=player.y-y;
		
		return Math.abs(dx)+Math.abs(dy)==1;

	}

}

package dance;

public class Monster {
	int damageTaken;
	int turnsWaited;
	int x,y;
	public Monster(int x, int y) {
		this.x=x; this.y=y;
	}
	
	public void noMove(){
		turnsWaited++;
	}
	
	public int getValue() {
		if(damageTaken>3)return-999;
//		if(turnsWaited==0)return 99999;
		return 9999;
	}
	
	
	public void attack(){
		damageTaken++;	
	}
	
	public void threatenSquares(){
		if(turnsWaited<2){
			for(int x=-1;x<=1;x++){
				for(int y=-1;y<=1;y++){
					if(Math.abs(x)+Math.abs(y)!=1)continue;
					Tile adjacent = Map.self.getTile(this.x+x, this.y+y);
					if(adjacent==null)continue;
					adjacent.threaten();
				}
			}
		}
	}

}

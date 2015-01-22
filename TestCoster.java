import junit.framework.TestCase;
import edu.uwm.cs351.HexBoard;
import edu.uwm.cs351.HexCoordinate;
import edu.uwm.cs351.HexPath;
import edu.uwm.cs351.HexPathCoster;
import edu.uwm.cs351.HexTile;
import edu.uwm.cs351.Terrain;


public class TestCoster extends TestCase {

	private HexBoard board;
	private HexPathCoster coster;
	
	private HexCoordinate c(int a, int b) { return new HexCoordinate(a,b); }
	
	private void p(int a, int b, Terrain t) {
		HexCoordinate c = c(a,b);
		board.put(c,new HexTile(t,c));
		coster = new HexPathCoster(board);
	}
	
	@Override
	protected void setUp() {
		board = new HexBoard();
		p(0,0,Terrain.INACCESSIBLE);
		p(1,0,Terrain.LAND);
		p(2,0,Terrain.WATER);
		p(3,0,Terrain.CITY);
		p(1,1,Terrain.DESERT);
		p(2,1,Terrain.FOREST);
		p(3,1,Terrain.MOUNTAIN);
		p(1,2,Terrain.CITY);
		p(2,2,Terrain.WATER);
		p(3,2,Terrain.INACCESSIBLE);
		
		coster.setCost(Terrain.LAND, 1);
		coster.setCost(Terrain.WATER, 4);
		coster.setCost(Terrain.CITY, 16);
		coster.setCost(Terrain.DESERT, 64);
		coster.setCost(Terrain.FOREST, 256);
		coster.setCost(Terrain.MOUNTAIN, 1024);
		coster.setCost(Terrain.INACCESSIBLE, (1 << 30) - 64);
	}
	
	public void test0() {
		HexPath p = new HexPath(c(0,0));
		assertEquals(0,coster.getCost(p));
	}
	
	public void test1() {
		HexPath p0 = new HexPath(c(0,0));
		HexPath p1 = new HexPath(p0,c(1,0));
		
		assertEquals((1<<30)-64+1,coster.getCost(p1));
	}
	
	public void test2() {
		HexPath p0 = new HexPath(c(1,1));
		HexPath p1 = new HexPath(p0,c(2,1));
		HexPath p2 = new HexPath(p1,c(3,1));
		
		assertEquals(64+2*256+1024,coster.getCost(p2));
	}
	
	public void testOverflow() {
		HexPath p0 = new HexPath(c(1,0));
		HexPath p1 = new HexPath(p0,c(0,0));
		HexPath p2 = new HexPath(p1,c(1,1));
		HexPath p3 = new HexPath(p2,c(2,1));
		HexPath p4 = new HexPath(p3,c(3,2));
		HexPath p5 = new HexPath(p4,c(3,1));
		
		assertEquals((1<<30)-64+(1<<30)-64+1+64,coster.getCost(p2));
		assertEquals(Integer.MAX_VALUE,coster.getCost(p3));
		assertEquals(Integer.MAX_VALUE,coster.getCost(p4));
		assertEquals(Integer.MAX_VALUE,coster.getCost(p5));
	}
	
	public void testOffBoard() {
		HexPath p0 = new HexPath(c(3,0));
		HexPath p1 = new HexPath(p0,c(3,1));
		HexPath p2 = new HexPath(p1,c(4,1));
		
		assertEquals(1040,coster.getCost(p1));
		assertEquals(Integer.MAX_VALUE,coster.getCost(p2));
	}
	
	public void testDefault() {
		coster = new HexPathCoster(board);
		
		HexPath p0 = new HexPath(c(0,0));
		HexPath p1 = new HexPath(p0,c(1,0));
		
		assertEquals(Integer.MAX_VALUE,coster.getCost(p1));
		
		p1 = new HexPath(c(1,0));
		HexPath p2 = new HexPath(p1,c(2,0));
		HexPath p3 = new HexPath(p2,c(3,0));
		HexPath p4 = new HexPath(p3,c(3,1));
		HexPath p5 = new HexPath(p4,c(2,1));
		HexPath p6 = new HexPath(p5,c(1,1));
		
		assertEquals(0,coster.getCost(p1));
		assertEquals(2,coster.getCost(p2));
		assertEquals(4,coster.getCost(p3));
		assertEquals(6,coster.getCost(p4));
		assertEquals(8,coster.getCost(p5));
		assertEquals(10,coster.getCost(p6));
	}
}

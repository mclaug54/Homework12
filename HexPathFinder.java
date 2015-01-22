import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.uwm.cs351.FormatException;
import edu.uwm.cs351.HexBoard;
import edu.uwm.cs351.HexCoordinate;
import edu.uwm.cs351.HexPath;
import edu.uwm.cs351.HexPathCoster;
import edu.uwm.cs351.HexTile;
import edu.uwm.cs351.Search;
import edu.uwm.cs351.Terrain;
import edu.uwm.cs351.util.FIFOWorklist;
import edu.uwm.cs351.util.LIFOWorklist;
import edu.uwm.cs351.util.PriorityWorklist;
import edu.uwm.cs351.util.Worklist;

/**
 * Render files of hex tiles on the screen.
 * The main program should be executed with a series of
 * files  Each file is opened in turn and the hex tiles aread from it.
 * If no files are given to the main program, it read hex tiles
 * from standard input.
 */
public class HexPathFinder extends JFrame {
	/**
	 * Eclipse wants this
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		final HexBoard board = new HexBoard();
		final HexPathCoster defaultCoster = new HexPathCoster(board);
		defaultCoster.setCost(Terrain.CITY, 3);
		// defaultCoster.setCost(Terrain.DESERT, 1);
		defaultCoster.setCost(Terrain.FOREST, 3);
		defaultCoster.setCost(Terrain.MOUNTAIN, 5);
		defaultCoster.setCost(Terrain.WATER, 10);
		if (args.length != 4) {
			System.out.println("Set Run>Run Configurations>Arguments>Program Arguments to have four arguments:");
			System.out.println("\ta filename (file of hextiles)");
			System.out.println("\tone of FIFO or LIFO or Priority");
			System.out.println("\ta starting hex coordinate");
			System.out.println("\tand ending hex coordinate");
			System.exit(1);
		}
		Worklist<HexPath> worklist;
		HexCoordinate start, end;
		try {
			readSeq(board, new BufferedReader(new FileReader(args[0])));
			if (args[1].equals("FIFO")) worklist = new FIFOWorklist<HexPath>();
			else if (args[1].equals("LIFO")) worklist = new LIFOWorklist<HexPath>();
			else if (args[1].equals("Priority")) worklist = new PriorityWorklist<HexPath>(defaultCoster);
			else {
				System.out.println("Unknown worklist type: " + args[1]);
				System.exit(1);
				return;
			}
			start = HexCoordinate.fromString(args[2]);
			end = HexCoordinate.fromString(args[3]);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		} catch (FormatException e) {
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		}
		final Search search = new Search(worklist);
		final HexPath path = search.find(start, end, board);
		if (path == null) {
			System.out.println("No path found.");
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				HexPathFinder x = new HexPathFinder(board,search,path);
				x.setSize(500, 300);
				x.setVisible(true);
				x.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}
		});
	}
	
	private static void readSeq(Map<HexCoordinate,HexTile> b, BufferedReader r) throws IOException {
		String input;
		while ((input = r.readLine()) != null) {
			try {
				HexTile tile = HexTile.fromString(input);
				b.put(tile.getLocation(),tile);
			} catch (FormatException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("serial")
	public HexPathFinder(final Map<HexCoordinate,HexTile> b, final Search search, final HexPath path) {
		this.setContentPane(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (HexTile tile : b.values()) {
					tile.draw(g);
				}
				if (path != null) path.draw(g);
				else search.markVisited(g);
			}
		});
	}
}

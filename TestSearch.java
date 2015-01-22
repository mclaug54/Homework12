import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import junit.framework.TestCase;
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


public class TestSearch extends TestCase {

	HexBoard b;
	HexPath p;
	HexPathCoster c;
	HexCoordinate[] a;
	
	Search fifoSearch, lifoSearch, prioritySearch;
	
	private HexCoordinate h(int a, int b) {
		return new HexCoordinate(a,b);
	}
	
	private void p(int a, int b, Terrain t) {
		this.b.put(h(a,b),new HexTile(t,h(a,b)));
	}
	
	protected void setUp() {
		b = new HexBoard();
		c = new HexPathCoster(b);
		p(0,0,Terrain.MOUNTAIN);
		p(0,0,Terrain.MOUNTAIN);
		p(1,0,Terrain.MOUNTAIN);
		p(2,0,Terrain.MOUNTAIN);
		p(3,0,Terrain.MOUNTAIN);
		p(4,0,Terrain.MOUNTAIN);
		p(5,0,Terrain.MOUNTAIN);
		p(6,0,Terrain.MOUNTAIN);
		p(1,1,Terrain.FOREST);
		p(3,1,Terrain.FOREST);
		p(4,1,Terrain.FOREST);
		p(6,1,Terrain.FOREST);
		p(2,2,Terrain.LAND);
		p(3,2,Terrain.LAND);
		p(4,2,Terrain.CITY);
		p(5,2,Terrain.LAND);
		p(6,2,Terrain.LAND);
		p(3,3,Terrain.LAND);
		p(4,3,Terrain.WATER);
		p(5,3,Terrain.WATER);
		p(6,3,Terrain.LAND);
		p(4,4,Terrain.DESERT);
		p(5,4,Terrain.WATER);
		p(6,4,Terrain.DESERT);
		p(5,5,Terrain.DESERT);
		p(6,5,Terrain.DESERT);
		p(6,6,Terrain.CITY);
		c.setCost(Terrain.FOREST,2);
		c.setCost(Terrain.CITY,3);
		c.setCost(Terrain.MOUNTAIN,4);
		c.setCost(Terrain.WATER,5);
		
		lifoSearch = new Search(new LIFOWorklist<HexPath>());
		fifoSearch = new Search(new FIFOWorklist<HexPath>());
		prioritySearch = new Search(new PriorityWorklist<HexPath>(c));
	}
	
	private JRadioButtonMenuItem showLIFO, showFIFO, showPrio;
	
	private class Panel extends JPanel implements ActionListener {
		/**
		 * Keep Eclipse Happy
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.fillRect(0, 0, getWidth(), getHeight());
			for (HexTile h : b.values()) {
				h.draw(g);
			}
			if (showLIFO.isSelected()) {
				lifoSearch.markVisited(g);
			}
			if (showFIFO.isSelected()) {
				fifoSearch.markVisited(g);
			}
			if (showPrio.isSelected()) {
				prioritySearch.markVisited(g);
			}
			if (p != null) {
				p.draw(g);
			}
		}

		public void actionPerformed(ActionEvent arg0) {
			repaint();
		}		
	}
	
	public void showResults() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame("Test Board");
				JMenuBar mb = new JMenuBar();
				f.setJMenuBar(mb);
				Panel panel = new Panel();
				f.getContentPane().add(panel,BorderLayout.CENTER);
				f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);			
				ButtonGroup g = new ButtonGroup();
				showLIFO = new JRadioButtonMenuItem("LIFO"); g.add(showLIFO);
				showFIFO = new JRadioButtonMenuItem("FIFO"); g.add(showFIFO);
				showPrio = new JRadioButtonMenuItem("Priority"); g.add(showPrio);
				showLIFO.addActionListener(panel);
				showFIFO.addActionListener(panel);
				showPrio.addActionListener(panel);
				JMenu viewMenu = new JMenu("View");
				mb.add(viewMenu);
				viewMenu.add(showLIFO);
				viewMenu.add(showFIFO);
				viewMenu.add(showPrio);
				f.setSize(new Dimension(300,400));
				f.setVisible(true);
			}
		});
	}
	
	public static void main(String[] args) {
		final TestSearch t = new TestSearch();
		t.setUp();
		try {
			t.test0();
			t.test1();
			t.test2();
			t.testBig();
		} catch (junit.framework.AssertionFailedError ex) {
			ex.printStackTrace();
		}
		t.showResults();
	}
	
	public void test0() {
		p = lifoSearch.find(h(6,6), h(6,6), b);
		assertEquals(1,p.size());
	}
	
	public void test1() {
		p = lifoSearch.find(h(6,6), h(5,5), b);
		if (p.size() != 14) assertEquals(2,p.size());
		p = lifoSearch.find(h(6,6), h(6,5), b);
		if (p.size() != 14) assertEquals(2,p.size());
		
		p = fifoSearch.find(h(6,6), h(5,5), b);
		assertEquals(2,p.size());
		p = fifoSearch.find(h(6,6), h(6,5), b);
		assertEquals(2,p.size());

		p = prioritySearch.find(h(6,6), h(5,5), b);
		assertEquals(2,p.size());
		p = prioritySearch.find(h(6,6), h(6,5), b);
		assertEquals(2,p.size());
	}
	
	public void test2() {
		p = lifoSearch.find(h(6,6), h(5,4), b);
		assertEquals(13,p.size());
		
		p = fifoSearch.find(h(6,6), h(5,4), b);
		assertEquals(3,p.size());
		
		p = prioritySearch.find(h(6,6), h(5,4), b);
		assertEquals(3,p.size());
	}
	
	public void testBig() {
		p = lifoSearch.find(h(6,6), h(4,2), b);
		assertEquals(17,p.size());
		
		p = fifoSearch.find(h(6,6), h(4,2), b);
		assertEquals(5,p.size());
		
		p = prioritySearch.find(h(6,6), h(4,2), b);
		assertEquals(6,p.size());
	}
}

import edu.uwm.cs351.util.PriorityWorklist;
import edu.uwm.cs351.util.Worklist;

import java.util.Comparator;


public class TestPriorityWorklist extends AbstractTestWorklist<String> {

	private Worklist<Integer> worklist;
	
	private Integer i(int i) { return new Integer(i); }
	
	@Override
	protected void initWorklist() {
		w = new PriorityWorklist<String>(String.CASE_INSENSITIVE_ORDER);
		v1 = "Hello";
		v2 = "goodbye";
		v3 = "null";
		v4 = "";
		v5 = "Foo";
		worklist = new PriorityWorklist<Integer>(new Comparator<Integer>() {
			public int compare(Integer i1, Integer i2) {
				return i1 - i2;
			}
		}); // for local tests
	}

	public void testPriority() {
		worklist.add(10);
		worklist.add(0);
		worklist.add(42);
		worklist.add(-100);
		worklist.add(19);
		
		assertEquals(i(-100),worklist.next());
		assertEquals(i(0),worklist.next());
		
		worklist.add(80);
		
		assertEquals(i(10),worklist.next());
		assertEquals(i(19),worklist.next());
		
		worklist.add(99);
		
		assertEquals(i(42),worklist.next());
		assertEquals(i(80),worklist.next());
		
		worklist.add(2);
		
		assertEquals(i(2),worklist.next());
		assertEquals(i(99),worklist.next());
	}
}

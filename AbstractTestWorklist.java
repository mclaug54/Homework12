import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;
import edu.uwm.cs351.util.Worklist;


/**
 * An abstract class with tests of a worklist
 * that doesn't care what discipline it uses.
 * @param <T>
 */
public abstract class AbstractTestWorklist<T> extends TestCase {

	protected Worklist<T> w;
	protected T v1, v2, v3, v4, v5;
	
	protected abstract void initWorklist();
	
	protected final void setUp() {
		// we don't need assertions to be on...
		/*try {
			assert w.equals("Hello");
			throw new IllegalStateException("assertions must be enabled to run this test.\nAdd -ea to VM Arguments in Run Configurations > Arguments");
		} catch (NullPointerException ex) {
			// OK!
		}*/
		initWorklist();
	}
	
	public void test0() {
		assertFalse(w.hasNext());
	}
	
	public void test1() {
		w.add(v1);
		assertTrue(w.hasNext());
		assertSame(v1,w.next());
		assertFalse(w.hasNext());
		
		w.add(v2);
		assertTrue(w.hasNext());
		assertSame(v2,w.next());
		assertFalse(w.hasNext());
	}
	
	public void test2() {
		w.add(v3);
		w.add(v4);
		assertTrue(w.hasNext());
		T va = w.next();
		assertTrue(w.hasNext());
		T vb = w.next();
		assertFalse(w.hasNext());
		
		if (va == v3) {
			assertSame(v4,vb);
		} else {
			assertSame(v4,va);
			assertSame(v3,vb);
		}
	}
	
	public void test3() {
		w.add(v1);
		w.add(v2);
		
		T x1 = w.next();
		
		w.add(v3);
		assertTrue(w.hasNext());
		
		T x2 = w.next();
		
		assertTrue(w.hasNext());
		
		T x3 = w.next();
			
		if (x1 == v1) {
			if (x2 == v2) {
				assertSame(v3,x3);
			} else {
				assertSame(v3,x2);
				assertSame(v2,x3);
			}
		} else {
			assertSame(v2,x1);
			if (x2 == v1) {
				assertSame(v3,x3);
			} else {
				assertSame(v3,x2);
				assertSame(v1,x3);
			}
		}
	}
	
	public void test8() {
		Set<T> mirror = new HashSet<T>();
		w.add(v1); mirror.add(v1);
		w.add(v2); mirror.add(v2);
		w.add(v3); mirror.add(v3);
		w.add(v4); mirror.add(v4);
		w.add(v5); mirror.add(v5);
		
		assertTrue(w.hasNext());
		T x1 = w.next();
		assertTrue("#1="+x1,mirror.remove(x1));
		
		assertTrue(w.hasNext());
		T x2 = w.next();
		assertTrue("#2="+x2,mirror.remove(x2));
		
		w.add(x1);
		mirror.add(x1);
		
		assertTrue(w.hasNext());
		T x3 = w.next();
		assertTrue("#3="+x3,mirror.remove(x3));
		
		assertTrue(w.hasNext());
		T x4 = w.next();
		assertTrue("#4="+x4,mirror.remove(x4));
		
		w.add(x3);
		mirror.add(x3);
		
		assertTrue(w.hasNext());
		T x5 = w.next();
		assertTrue("#5="+x5,mirror.remove(x5));
		
		assertTrue(w.hasNext());
		T x6 = w.next();
		assertTrue("#6="+x6,mirror.remove(x6));
		
		w.add(x5);
		mirror.add(x5);
		
		assertTrue(w.hasNext());
		T x7 = w.next();
		assertTrue("#7="+x7,mirror.remove(x7));
		
		assertTrue(w.hasNext());
		T x8 = w.next();
		assertTrue("#8="+x8,mirror.remove(x8));
		
		assertFalse(w.hasNext());
	}
	
	public void testBadNext1() {
		try {
			w.next();
			assertFalse("next should have thrown an exception",true);
		} catch (RuntimeException ex) {
			assertTrue("next() threw wrong exception: " + ex, ex instanceof NoSuchElementException);
		}
	}
	
	public void testBadNext2() {
		w.add(v1);
		w.next();
		
		try {
			w.next();
			assertFalse("next should have thrown an exception",true);
		} catch (RuntimeException ex) {
			assertTrue("next() threw wrong exception: " + ex, ex instanceof NoSuchElementException);
		}
	}
	
	public void testBadRemove() {
		w.add(v1);
		w.next();
		
		try {
			w.remove();
			assertFalse("remove should have thrown an exception",true);
		} catch (RuntimeException ex) {
			assertTrue("remove() threw wrong exception: " + ex, ex instanceof UnsupportedOperationException);
		}
	}
}

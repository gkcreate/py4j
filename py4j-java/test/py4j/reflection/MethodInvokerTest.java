package py4j.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;

import p1.Cat;

public class MethodInvokerTest {

	@Test
	public void testInvoke() {
		Cat cat = new Cat();
		try {
			Method m = Cat.class.getMethod("meow10", float.class);
			MethodInvoker invoker = new MethodInvoker(m,
					new TypeConverter[] { new TypeConverter(
							TypeConverter.DOUBLE_TO_FLOAT) }, 0);
			invoker.invoke(cat, new Object[] { new Double(2.0) });

			m = Cat.class.getMethod("meow11", new Class[0]);
			invoker = new MethodInvoker(m, null, 0);
			invoker.invoke(cat, new Object[0]);

			m = Cat.class.getMethod("meow10", float.class);
			invoker = new MethodInvoker(m, null, 0);
			invoker.invoke(cat, new Object[] { new Float(1.1f) });
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testVoid() {
		try {
			Method m = Cat.class.getMethod("meow10", float.class);
			MethodInvoker invoker = new MethodInvoker(m,
					new TypeConverter[] { new TypeConverter(
							TypeConverter.DOUBLE_TO_FLOAT) }, 0);
			assertTrue(invoker.isVoid());

			m = Cat.class.getMethod("meow12", new Class[0]);
			invoker = new MethodInvoker(m, null, 0);
			assertFalse(invoker.isVoid());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testBuildInvokerFloat() {
		try {
			Method m = Cat.class.getMethod("meow10", float.class);
			MethodInvoker invoker1 = MethodInvoker.buildInvoker(m,
					new Class[] { float.class });
			assertEquals(0, invoker1.getCost());
			invoker1 = MethodInvoker.buildInvoker(m,
					new Class[] { Float.class });
			assertEquals(0, invoker1.getCost());
			assertNull(invoker1.getConverters());
			invoker1 = MethodInvoker.buildInvoker(m,
					new Class[] { Double.class });
			assertEquals(1, invoker1.getCost());
			assertNotNull(invoker1.getConverters());

			Cat cat = new Cat();
			assertNull(invoker1.invoke(cat, new Object[] { 2.0 }));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testBuildInvokerAll() {
		try {
			Cat cat = new Cat();
			Method m = Cat.class.getMethod("meow13", long.class, int.class,
					short.class, byte.class, double.class, Float.class,
					boolean.class, String.class, char.class);
		
			MethodInvoker invoker = MethodInvoker.buildInvoker(m, new Class[] {long.class, int.class,
					short.class, byte.class, double.class, float.class,
					boolean.class, String.class, char.class});
			assertEquals(0, invoker.getCost());
			assertNull(invoker.getConverters());
			
			// Distance greater than 0, but no conversion required.
			invoker = MethodInvoker.buildInvoker(m, new Class[] {int.class, byte.class,
					short.class, byte.class, Float.class, float.class,
					Boolean.class, String.class, Character.class});
			assertEquals(4, invoker.getCost());
			assertNull(invoker.getConverters());
			
			// Invalid.
			invoker = MethodInvoker.buildInvoker(m, new Class[] {double.class, byte.class,
					short.class, byte.class, Float.class, float.class,
					Boolean.class, String.class, Character.class});
			assertEquals(-1, invoker.getCost());
			assertNull(invoker.getConverters());
			
			// Need char conversion
			invoker = MethodInvoker.buildInvoker(m, new Class[] {long.class, int.class,
					short.class, byte.class, double.class, float.class,
					boolean.class, String.class, String.class});
			assertEquals(1, invoker.getCost());
			assertEquals(9, invoker.getConverters().length);
			assertEquals(10, invoker.invoke(cat, new Object[] {1l, 2, (short)3, (byte)4, 1.2, 1.2f, true, "a", "a"}));
			
			// Need short, byte conversion
			invoker = MethodInvoker.buildInvoker(m, new Class[] {long.class, int.class,
					Integer.class, int.class, double.class, float.class,
					boolean.class, String.class, char.class});
			assertEquals(3, invoker.getCost());
			assertEquals(9, invoker.getConverters().length);
			assertEquals(10, invoker.invoke(cat, new Object[] {1l, 2, 3, 4, 1.2, 1.2f, true, "a", 'a'}));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testBuildInvokerObject() {
		try {
			TestInvoker tInvoker = new TestInvoker();
			Method m = TestInvoker.class.getMethod("m1", A.class, V.class, I0.class, J0.class);
		
			MethodInvoker invoker = MethodInvoker.buildInvoker(m, new Class[] {A.class, V.class, I0.class, J0.class});
			assertEquals(0, invoker.getCost());
			assertNull(invoker.getConverters());
			assertNull(invoker.invoke(tInvoker, new Object[] {new A(), new V(), new I0() {}, new J0() {}}));
			
			invoker = MethodInvoker.buildInvoker(m, new Class[] {B.class, W.class, I2.class, J0.class});
			assertEquals(4, invoker.getCost());
			assertNull(invoker.getConverters());
			assertNull(invoker.invoke(tInvoker, new Object[] {new B(), new W(), new I2() {}, new J0() {}}));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}

class TestInvoker {
	public void m1(A a, V v, I0 i0, J0 j0) {
		
	}
}
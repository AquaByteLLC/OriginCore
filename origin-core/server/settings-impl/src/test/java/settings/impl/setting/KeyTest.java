package settings.impl.setting;

import org.junit.jupiter.api.Test;
import settings.impl.setting.key.GKey;
import settings.impl.setting.key.LKey;
import settings.setting.key.GlobalKey;
import settings.setting.key.LocalKey;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author vadim
 */
class KeyTest {

	@Test
	void GKey() {
		assertThrows(IllegalArgumentException.class, () -> {
			new GKey("a.1", "mal-formed", "key");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new GKey("a-2", "mal_formed", "key:");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new GKey("a_3", "mal formed", "key");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new GKey("a_4", "malformed", "key!");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new GKey("a_5", null, "key");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new GKey("", "empty", "key");
		});
		assertDoesNotThrow(() -> {
			new GKey("a", "valid", "key:tail");
		});
	}

	@Test
	void LKey() {
		assertThrows(IllegalArgumentException.class, () -> {
			new LKey("mal formed");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new LKey("mal.formed");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new LKey("mal:formed");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new LKey("malformed!");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new LKey(null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new LKey("");
		});
		assertDoesNotThrow(() -> {
			new LKey("key");
		});
	}

	@Test
	void of() {
		GlobalKey g = GKey.of("this.key.is.cool");
		assertEquals("this.key.is.cool", g.full());

		LocalKey l = LKey.of("local");
		assertEquals("local", l.identifier());

		GlobalKey w = GKey.of("key.with:tail");
		assertEquals("key.with:tail", w.full());
	}

	@Test
	void full() {
		GlobalKey key = new GKey("another", "epic", "key");
		assertEquals("another.epic.key", key.full());

		GlobalKey tail = key.withTail(LKey.of("with_tail"));
		assertEquals("another.epic.key:with_tail", tail.full());
	}

	@Test
	void parts() {
		GlobalKey key = GKey.of("qaz.qux.qit");
		assertArrayEquals(new String[] { "qaz", "qux", "qit" }, key.parts());
	}

	@Test
	void getParent() {
		GlobalKey parent = GKey.of("parent");
		GlobalKey child  = GKey.of("parent.child");

		assertEquals(parent, child.getParent());
	}

	@Test
	void isRootKey() {
		GlobalKey root = GKey.of("root");
		GlobalKey leaf = GKey.of("a.leaf");
		GlobalKey node = GKey.of("some.other.node");

		assertTrue(root.isRootKey());
		assertFalse(leaf.isRootKey());
		assertFalse(node.isRootKey());
	}

	@Test
	void hasTail() {
		GlobalKey tail = new GKey("root").withTail(LKey.of("tail"));
		assertTrue(tail.hasTail());
	}

	@Test
	void getTail() {
		GlobalKey key  = new GKey("parent", "key");
		LocalKey  tail = LKey.of("tail");
		GlobalKey with = key.withTail(tail);

		assertEquals(tail, with.getTail());
	}

	@Test
	void append() {
		GlobalKey test = GKey.of("a.b.c.d:e");

		GlobalKey a = new GKey("a");
		GlobalKey b = new GKey("b");
		GlobalKey c = new GKey("c");
		GlobalKey d = new GKey("d");
		LocalKey e = new LKey("e");

		assertEquals(test, a.append(b, c, d).withTail(e));
		assertEquals(test, a.append(b, c, d.withTail(e)));

		assertEquals(test, a.append(b).append(c).append(d).withTail(e));
		assertEquals(test, a.append(b).append(c).append(d.withTail(e)));

		assertEquals(test, a.append(b.append(c.append(d).withTail(e))));
		assertEquals(test, a.append(b.append(c.append(d))).withTail(e));
	}

	@Test
	void withTail() {
		GlobalKey orig  = new GKey("parent", "key");
		GlobalKey copy  = new GKey("parent", "key");

		LocalKey  tail = LKey.of("tail");
		GlobalKey with = copy.withTail(tail);

		assertEquals(orig, copy);
		assertEquals(tail, with.getTail());
		assertNotEquals(orig, with);
	}

}
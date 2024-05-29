package com.company.inventory.junit.repaso;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AssertTest {

	@Test
	public void assertEqualsTest() {
		assertEquals(1, 1);
	}

	@Test
	public void assertNotEqualsTest() {
		assertNotEquals(1, 2);

	}

	@Test
	public void assertTrueTest() {
		assertTrue(1 == 1);
	}

	@Test
	public void assertFalseTest() {
		assertFalse(1 != 1);
	}

	@Test
	public void assertArrayTest() {
		String[] arr1 = { "" };
		String[] arr2 = { "" };

		assertArrayEquals(arr1, arr2);

	}
}

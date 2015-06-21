package net.arenx.candidate2016.jdo;

import javax.jdo.PersistenceManager;

public class PersistenceManagerThreadLoccal {
	private static ThreadLocal<PersistenceManager> pm = new ThreadLocal<PersistenceManager>();

	public static PersistenceManager get() {
		return pm.get();
	}

	public static void set(PersistenceManager pm) {
		PersistenceManagerThreadLoccal.pm.set(pm);
	}
}

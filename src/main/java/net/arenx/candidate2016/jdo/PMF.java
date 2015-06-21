package net.arenx.candidate2016.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class PMF {

	private static  PersistenceManagerFactory pmf=JDOHelper.getPersistenceManagerFactory("transactions-optional");
	public static PersistenceManagerFactory get(){
		return pmf;
	}
}

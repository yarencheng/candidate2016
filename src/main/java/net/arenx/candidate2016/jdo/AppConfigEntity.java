package net.arenx.candidate2016.jdo;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class AppConfigEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String key;
	
	@Persistent
	private String value;
	
	public enum Key{
		FREE_TICKET_ISSUE_INTERVAL,
		LOCATION_LAYER_LEVEL_VERSION
	}
	
	public static String get(Key key){
		PersistenceManager pm = PersistenceManagerThreadLoccal.get();
		try {
			return pm.getObjectById(AppConfigEntity.class, key.toString()).value;
		} catch (JDOObjectNotFoundException e) {
			return null;
		}
	}
	
	public static void set(Key key,String value){
		PersistenceManager pm = PersistenceManagerThreadLoccal.get();
		AppConfigEntity config=null;
		try {
			config = pm.getObjectById(AppConfigEntity.class, key.toString());
			config.value=value;
		} catch (JDOObjectNotFoundException e) {
			config=new AppConfigEntity();
			config.key=key.toString();
			config.value=value;			
		}
		pm.makePersistent(config);
	}
}

package net.arenx.candidate2016.jdo;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.Validate;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class OsEntity {

	private OsEntity() {
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String name;
	
	/**
	 * If not empty, the name of this OS is an aliases
	 */
	@Unowned
	@Persistent
	private OsEntity realOs;

	public OsEntity getRealOs() {
		return realOs;
	}

	public void setRealOs(OsEntity realOs) {
		this.realOs = realOs;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Get OS by name.
	 * @param name The OS name. If it's an aliases, an OS with real name is returned.
	 * @param createIfNew If true, create and return a new OS when OS didn't exist.
	 * @return Matched OS or null if device was not found.
	 */
	public static OsEntity getOs(String name,boolean createIfNew){
		Validate.notBlank(name,"name is blank");
		PersistenceManager pm=PersistenceManagerThreadLoccal.get();
		OsEntity osEntity = null;
		try{
			osEntity=pm.getObjectById(OsEntity.class, name);
		}catch(JDOObjectNotFoundException e){
			if(createIfNew){
				Validate.isTrue(name.length()<100, "length is more than 100.");
				osEntity=new OsEntity();
				osEntity.name=name;
				pm.makePersistent(osEntity);
			}else{
				return null;
			}
		}
		while(osEntity.getRealOs()!=null){
			osEntity=osEntity.getRealOs();
		}
		return osEntity;
	}
	
}

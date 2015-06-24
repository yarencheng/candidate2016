package net.arenx.candidate2016.jdo;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class LocationEntity {

	private LocationEntity(){
		
	}
	
	/**
	 * ID = "name-level"
	 * e.g. "中山區-2" 
	 */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@JsonProperty()
	private String id;
	
	@Persistent
	@JsonProperty()
	private String name;
	
	@Persistent
	@JsonProperty()
	private Integer level;
	
	@Unowned
	@Persistent
	@JsonProperty()
	private List<LocationEntity> subLevels;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getLevel() {
		return level;
	}

	public List<LocationEntity> getSubLevels() {
		return subLevels;
	}
	
	public static LocationEntity getLocation(String name,int level){
		PersistenceManager pm = PersistenceManagerThreadLoccal.get();
		try {
			return pm.getObjectById(LocationEntity.class, name+"-"+level);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}
	}
}

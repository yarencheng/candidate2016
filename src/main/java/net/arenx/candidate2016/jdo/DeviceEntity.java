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
public class DeviceEntity {

	private DeviceEntity() {
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String name;
	
	/**
	 * If not empty, the name of this device is an aliases
	 */
	@Unowned
	@Persistent
	private DeviceEntity realDevice;

	public DeviceEntity getRealDevice() {
		return realDevice;
	}

	public void setRealDevice(DeviceEntity realDevice) {
		this.realDevice = realDevice;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Get device by name.
	 * @param name The device's name. If it's an aliases, a device with real name is returned.
	 * @param createIfNew If true, create and return a new device when device didn't exist.
	 * @return Matched device or null if no device was found.
	 */
	public static DeviceEntity getDevice(String name,boolean createIfNew){
		Validate.notBlank(name,"name is blank");
		PersistenceManager pm=PersistenceManagerThreadLoccal.get();
		DeviceEntity deviceEntity = null;
		try{
			deviceEntity=pm.getObjectById(DeviceEntity.class, name);
		}catch(JDOObjectNotFoundException e){
			if(createIfNew){
				Validate.isTrue(name.length()<100, "length is more than 100.");
				deviceEntity=new DeviceEntity();
				deviceEntity.name=name;
				pm.makePersistent(deviceEntity);
			}else{
				return null;
			}
		}
		while(deviceEntity.getRealDevice()!=null){
			deviceEntity=deviceEntity.getRealDevice();
		}
		return deviceEntity;
	}
	
}

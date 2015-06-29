package net.arenx.candidate2016.jdo;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;
import com.google.appengine.datanucleus.annotations.Unowned;

import net.arenx.candidate2016.appengine.enums.Sex;

@PersistenceCapable
public class UserEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String appengineId;

	@Persistent
	private String email;
	
	@Persistent
	private Sex sex;
	
	@Persistent
	private Integer age;
	
	@Persistent
	private Boolean isAdmin;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAppengineId() {
		return appengineId;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	private UserEntity(User appengineUser) {
		this.appengineId = appengineUser.getUserId();
		this.email = appengineUser.getEmail();
		TicketEntity.createFreeTicket(this);
	}

	public static UserEntity getUser(User appengineUser, boolean createIfNew) {
		PersistenceManager pm = PersistenceManagerThreadLoccal.get();
		UserEntity user = null;
		try {
			user = pm.getObjectById(UserEntity.class, appengineUser.getUserId());
		} catch (JDOObjectNotFoundException e) {
			if (createIfNew) {
				user = new UserEntity(appengineUser);
				pm.makePersistent(user);
			}
		}
		return user;
	}
}

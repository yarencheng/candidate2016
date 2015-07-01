package net.arenx.candidate2016.jdo;

import java.util.Arrays;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

@PersistenceCapable
public class CandidateEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@JsonProperty()
	private Long id;
	
	@Persistent
	@JsonProperty()
	private String info;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getId() {
		return id;
	}
	
	public static CandidateEntity getCandidate(Long id){
		PersistenceManager pm = PersistenceManagerThreadLoccal.get();
		try {
			return pm.getObjectById(CandidateEntity.class, id);
		} catch (JDOObjectNotFoundException e) {
			return null;
		}
	}
	
	public static List<CandidateEntity> getAllandidates(){
		PersistenceManager pm = PersistenceManagerThreadLoccal.get();
		try {
			Query query=pm.newQuery(CandidateEntity.class);
			List<CandidateEntity>candidateEntities=(List<CandidateEntity>) query.execute();
			return candidateEntities;
		} catch (JDOObjectNotFoundException e) {
			return Arrays.asList();
		}
	}
}

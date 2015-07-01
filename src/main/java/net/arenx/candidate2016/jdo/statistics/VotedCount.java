package net.arenx.candidate2016.jdo.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import net.arenx.candidate2016.appengine.api.UserApi;
import net.arenx.candidate2016.appengine.enums.Sex;
import net.arenx.candidate2016.appengine.enums.TicketType;
import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.DeviceEntity;
import net.arenx.candidate2016.jdo.LocationEntity;
import net.arenx.candidate2016.jdo.OsEntity;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.TicketEntity;
import net.arenx.candidate2016.jdo.UserEntity;
import net.arenx.candidate2016.jdo.VoteEntity;

public class VotedCount {
	
	private static final Logger logger = Logger.getLogger(VotedCount.class.getName());
	
	private VotedCount() {

	}
	
	public enum  Field {
		CANDIDATE(CandidateEntity.class,true),
		SEX(Sex.class,true), 
		AGE(Integer.class,true), 
		LOCATION_LAYER1(LocationEntity.class,true), 
		LOCATION_LAYER2(LocationEntity.class,true), 
		OS(OsEntity.class,true), 
		DEVICE(DeviceEntity.class,true),
		DATE(Date.class,false);
		
		private  Class<?> type;
		private boolean asAndCondition = false;

		private Field(Class<?> type, boolean asAndCondition) {
			Validate.isTrue(ClassUtils.isAssignable(type, Serializable.class)||type.isAnnotationPresent(PersistenceCapable.class),"only allow Serializable or PersistenceCapable");
			this.type = type;
			this.asAndCondition=asAndCondition;
		}
	}
	
	private Map<Field,Object> andConditionMap=new HashMap<Field,Object>();
	private Date leftTimeInclusiveBound;
	private Date rightTimeExclusiveBound;
	
	public static VotedCount init(CandidateEntity candidateEntity) {
		Validate.notNull(candidateEntity,"candidateEntity");
		VotedCount votedCount=new VotedCount();
		votedCount.and(Field.CANDIDATE, candidateEntity);
		return votedCount;
	}
	
	public VotedCount and(Field field,Object value){
		Validate.notNull(field,"field");
		Validate.notNull(value,"value");
		Validate.isTrue(field.asAndCondition,"%s can't used as an AND condition",field);
		Validate.isInstanceOf(field.type, value,"type of %s should be %s but get %s",field,field.type,value.getClass());
		if(value.getClass().isAnnotationPresent(PersistenceCapable.class)){
			Validate.notNull(getEntityKey(value),"key of value is null");
		}
		andConditionMap.put(field, value);
		return this;
	}
	
	public VotedCount between(Date leftInclusive, Date rightExclusive){
		if(leftInclusive!=null&&rightExclusive!=null){
			Validate.isTrue(leftInclusive.before(rightExclusive),"leftInclusive should be smaller than rightExclusive");
		}
		leftTimeInclusiveBound=leftInclusive;
		rightTimeExclusiveBound=rightExclusive;
		return this;
	}
		
	public long execute(){
		PersistenceManager pm=PersistenceManagerThreadLoccal.get();
		Query query = pm.newQuery(VoteEntity.class);
		StringBuilder filter = new StringBuilder();
		StringBuilder parameter = new StringBuilder();
		List<Object>outputParameterList=new ArrayList<Object>();
		
		if(andConditionMap.size()>0){
			Iterator<Entry<Field,Object>> i=andConditionMap.entrySet().iterator();
			while(i.hasNext()){
				Entry<Field,Object> e=i.next();
				Field field=e.getKey();
				switch(field){
				case CANDIDATE:
					filter.append("candidate");
					parameter.append(CandidateEntity.class.getName());
					break;
				case AGE:
					filter.append("statisticsData.age");
					parameter.append(Integer.class.getName());
					break;
				case DEVICE:
					filter.append("statisticsData.device");
					parameter.append(DeviceEntity.class.getName());
					break;
				case LOCATION_LAYER1:
					filter.append("statisticsData.locationLayer1");
					parameter.append(LocationEntity.class.getName());
					break;
				case LOCATION_LAYER2:
					filter.append("statisticsData.locationLayer2");
					parameter.append(LocationEntity.class.getName());
					break;
				case OS:
					filter.append("statisticsData.os");
					parameter.append(OsEntity.class.getName());
					break;
				case SEX:
					filter.append("statisticsData.sex");
					parameter.append(Sex.class.getName());
					break;
				case DATE:
				default:
					logger.warning("field "+field+" is not implemented");
					continue;
				
				}
				outputParameterList.add(e.getValue());
				filter.append(" == "+field.name());
				parameter.append(" "+field.name());
				if(i.hasNext()){
					filter.append(" && ");
					parameter.append(", ");
				}
			}
		}
		
		if(leftTimeInclusiveBound!=null){
			if(filter.length()>0){
				filter.append(" && ");
			}
			if(parameter.length()>0){
				parameter.append(", ");
			}
			filter.append("date >= leftTimeBound");
			parameter.append(Date.class.getName()+" leftTimeBound");
			outputParameterList.add(leftTimeInclusiveBound.getTime());
		}
		if(rightTimeExclusiveBound!=null){
			if(filter.length()>0){
				filter.append(" && ");
			}
			if(parameter.length()>0){
				parameter.append(", ");
			}
			filter.append("date < rightTimeBound");
			parameter.append(Date.class.getName()+" rightTimeBound");
			outputParameterList.add(rightTimeExclusiveBound);
		}
		
		query.setFilter(filter.toString());
		query.declareParameters(parameter.toString());
		query.setResult("sum(this.quota)");
		Long count=(Long) query.executeWithArray(outputParameterList.toArray());
		return count==null?0:count;
	}

	private static Object getEntityKey(Object entity) {
		for (java.lang.reflect.Field f : entity.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(PrimaryKey.class)) {
				f.setAccessible(true);
				try {
					return f.get(entity);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.warning("fail to get the key field");
					return null;
				}
			}
		}
		return null;
	}
	
}

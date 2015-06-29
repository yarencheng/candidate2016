package net.arenx.candidate2016.jdo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.Validate;

import net.arenx.candidate2016.appengine.enums.Sex;
import net.arenx.candidate2016.appengine.enums.TicketType;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class VoteEntity {

	private VoteEntity(UserEntity user,CandidateEntity candidate,Long quota,TicketType ticketType){
		Validate.notNull(user);
		Validate.notNull(candidate);
		Validate.notNull(quota);
		Validate.notNull(ticketType);
		this.date = new Date(System.currentTimeMillis());
		this.user=user;
		this.candidate=candidate;
		this.statisticsData=new StatisticsData();
		this.statisticsData.age=user.getAge();
		this.statisticsData.sex=user.getSex();
		this.ticketType=ticketType;
		this.quota=quota;
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private Date date;
	
	@Persistent
	private Long quota;
	
	@Persistent
	private TicketType ticketType;
		
	@Unowned
	@Persistent
	private UserEntity user;
	
	@Unowned
	@Persistent
	private CandidateEntity candidate;
	
	@Persistent
	@Embedded
	private StatisticsData statisticsData;
		
	public Long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public Long getQuota() {
		return quota;
	}

	public UserEntity getUser() {
		return user;
	}

	public CandidateEntity getCandidate() {
		return candidate;
	}

	public StatisticsData getStatisticsData() {
		return statisticsData;
	}

	@PersistenceCapable
    @EmbeddedOnly
    public static class StatisticsData {
				
        @Persistent
        private Double latitude;
        
        @Persistent
        private Double logitude;
        
        @Persistent
        private Sex sex;
        
        @Persistent
        private Integer age;
                
        /**
         * E.g. Taipei
         */
        @Unowned
        @Persistent
        private LocationEntity locationLayer1;
        
        /**
         * E.g. 中山區
         */
        @Unowned
        @Persistent
        private LocationEntity locationLayer2;
        
        @Unowned
        @Persistent
        private OsEntity os;
        
        @Unowned
        @Persistent 
        private DeviceEntity device;
        
		public Double getLatitude() {
			return latitude;
		}

		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public Double getLogitude() {
			return logitude;
		}

		public void setLogitude(Double logitude) {
			this.logitude = logitude;
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

		public LocationEntity getLocationLayer1() {
			return locationLayer1;
		}

		public void setLocationLayer1(LocationEntity locationLayer1) {
			this.locationLayer1 = locationLayer1;
		}

		public LocationEntity getLocationLayer2() {
			return locationLayer2;
		}

		public void setLocationLayer2(LocationEntity locationLayer2) {
			this.locationLayer2 = locationLayer2;
		}

		public OsEntity getOs() {
			return os;
		}

		public void setOsType(OsEntity os) {
			this.os = os;
		}

		public DeviceEntity getDevice() {
			return device;
		}

		public void setDevice(DeviceEntity device) {
			this.device = device;
		}		
    }
	
	public static class Builder{
		private VoteEntity voteEntity;
		private boolean isFinished = false;
		private Builder(UserEntity user,CandidateEntity candidate,Long quota,TicketType ticketType){
			voteEntity = new VoteEntity(user, candidate, quota, ticketType);
		}
		public static Builder initial(UserEntity user,CandidateEntity candidate,Long quota,TicketType ticketType){
			return new Builder( user, candidate, quota, ticketType);
		}
		public Builder setAge(Integer age){
			voteEntity.getStatisticsData().setAge(age);
			return this;
		}
		public Builder setSex(Sex sex){
			voteEntity.getStatisticsData().setSex(sex);
			return this;
		}
		public Builder setDevice(DeviceEntity device){
			voteEntity.getStatisticsData().setDevice(device);
			return this;
		}
		public Builder setLatitude(Double latitude){
			voteEntity.getStatisticsData().setLatitude(latitude);
			return this;
		}
		public Builder setLogitude(Double logitude){
			voteEntity.getStatisticsData().setLogitude(logitude);
			return this;
		}
		public Builder setLocationLayer1(LocationEntity locationLayer){
			voteEntity.getStatisticsData().setLocationLayer1(locationLayer);
			return this;
		}
		public Builder setLocationLayer2(LocationEntity locationLayer){
			voteEntity.getStatisticsData().setLocationLayer2(locationLayer);
			return this;
		}
		public Builder setOsType(OsEntity os){
			voteEntity.getStatisticsData().setOsType(os);
			return this;
		}
		public VoteEntity build(){
			if(isFinished){
				throw new IllegalStateException("this builder is allready finished");
			}
			PersistenceManager pm=PersistenceManagerThreadLoccal.get();
			pm.makePersistent(voteEntity);
			isFinished=true;
			return voteEntity;
		}
	}
	
	public static Long getAllVotedPaidTicket(UserEntity user){
		Validate.notNull(user);
		PersistenceManager pm=PersistenceManagerThreadLoccal.get();
		Query query = pm.newQuery(VoteEntity.class);
		query.setFilter("user == x && ticketType == y");
		query.declareParameters(UserEntity.class.getName()+" x, "+TicketType.class.getName()+" y");
		query.setResult("sum(this.quota)");
		Long quota = (Long) query.execute(user,TicketType.paid);
		return quota == null ? 0 : quota;
	}
	
	public static Date getLastDateOfVoteFreeTikcket(UserEntity user){
		Validate.notNull(user);
		PersistenceManager pm=PersistenceManagerThreadLoccal.get();
		Query query = pm.newQuery(VoteEntity.class);
		query.setFilter("user == x && ticketType == y");
		query.declareParameters(UserEntity.class.getName()+" x, "+TicketType.class.getName()+" y");
		query.setOrdering("date desc");
		query.setRange(0, 1);
		List<VoteEntity>voteEntityList = (List<VoteEntity>)query.execute(user,TicketType.free);
		if(voteEntityList.isEmpty()){
			return null;
		}else{
			return voteEntityList.get(0).date;
		}
	}
}

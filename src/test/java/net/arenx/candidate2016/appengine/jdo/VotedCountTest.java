package net.arenx.candidate2016.appengine.jdo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.appengine.enums.Sex;
import net.arenx.candidate2016.appengine.enums.TicketType;
import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.DeviceEntity;
import net.arenx.candidate2016.jdo.LocationEntity;
import net.arenx.candidate2016.jdo.OsEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.UserEntity;
import net.arenx.candidate2016.jdo.VoteEntity;
import net.arenx.candidate2016.jdo.statistics.VotedCount;
import net.arenx.candidate2016.jdo.statistics.VotedCount.Field;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VotedCountTest {

	private static final Logger logger = Logger.getLogger(VotedCount.class.getName());
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig()
			.setOAuthAuthDomain("test.com").setOAuthEmail("test@test.com").setOAuthUserId("testid")).setEnvIsAdmin(true).setEnvIsLoggedIn(true);

	PersistenceManager pm;
	UserEntity userEntity;

	ObjectMapper mapper=new ObjectMapper();
	List<CandidateEntity> candidateList=null;
	List<LocationEntity>locationL1List=null;
	List<LocationEntity>locationL2List=null;
	List<DeviceEntity>deviceList=null;
	List<OsEntity>osList=null;
	List<VoteEntity>voteList=null;

	@Before
	public void before() throws OAuthRequestException, JsonParseException, JsonMappingException, FileNotFoundException, IOException, InterruptedException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		helper.setUp();
		pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);

		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
		User user = oauthService.getCurrentUser();
		userEntity = UserEntity.getUser(user, true);
		pm.makePersistent(userEntity);

		// init candidate
		int candidateCount=RandomUtils.nextInt(1, 10);
		candidateList=new ArrayList<>();
		for (int i = 0; i < candidateCount; i++) {
			candidateList.add(new CandidateEntity());
		}
		pm.makePersistentAll(candidateList);
		
		// init location
		LocationEntity[] list=mapper.readValue(new FileInputStream("src/main/webapp/WEB-INF/location.json"), LocationEntity[].class);
		pm.makePersistentAll(list);
		locationL1List=new ArrayList<>();
		locationL2List=new ArrayList<>();
		for(LocationEntity l1:list){
			locationL1List.add(l1);
			for(LocationEntity l2:l1.getSubLevels()){
				locationL2List.add(l2);
			}
		}
		
		// init device
		int deviceCount=RandomUtils.nextInt(1, 10);
		deviceList=new ArrayList<>();
		for(int i=0;i<deviceCount;i++){
			deviceList.add(DeviceEntity.getDevice(RandomStringUtils.random(10), true));
		}
		
		// init os
		int osCount=RandomUtils.nextInt(1, 10);
		osList=new ArrayList<>();
		for(int i=0;i<deviceCount;i++){
			osList.add(OsEntity.getOs(RandomStringUtils.random(10), true));
		}
		
		// random vote
		int voteRound=RandomUtils.nextInt(50, 100);
		voteList=new ArrayList<>();
		for(int i=0;i<voteRound;i++){
			Date voteDate=new Date(System.currentTimeMillis()-RandomUtils.nextLong(0, 1000L*60L*60L*24L*365*2));
			VoteEntity voteEntity=VoteEntity.Builder.initial(userEntity, 
					candidateList.get(RandomUtils.nextInt(0, candidateList.size())), 
					RandomUtils.nextLong(0, 100)-50l, 
					TicketType.values()[RandomUtils.nextInt(0,TicketType.values().length )]
					)
					.setAge(RandomUtils.nextInt(0, 5)==0?null:RandomUtils.nextInt(0, 5))
					.setDevice(RandomUtils.nextInt(0, 5)==0?null:deviceList.get(RandomUtils.nextInt(0, deviceList.size())))
					.setLatitude(RandomUtils.nextInt(0, 5)==0?null:RandomUtils.nextDouble(0, 180)-90)
					.setLogitude(RandomUtils.nextInt(0, 5)==0?null:RandomUtils.nextDouble(0, 360)-180)
					.setLocationLayer1(RandomUtils.nextInt(0, 5)==0?null:locationL1List.get(RandomUtils.nextInt(0, locationL1List.size())))
					.setLocationLayer2(RandomUtils.nextInt(0, 5)==0?null:locationL2List.get(RandomUtils.nextInt(0, locationL2List.size())))
					.setOsType(RandomUtils.nextInt(0, 5)==0?null:osList.get(RandomUtils.nextInt(0, osList.size())))
					.setSex(RandomUtils.nextInt(0, 5)==0?null:Sex.values()[RandomUtils.nextInt(0, Sex.values().length)])
					.setDate(voteDate)
					.build();
			voteList.add(voteEntity);
			Thread.sleep(1);
		}
		pm.makePersistentAll(voteList);
		logger.info("voteList.size="+voteList.size());
	}

	@After
	public void after() {
		PersistenceManagerThreadLoccal.set(null);
		pm.close();
		helper.tearDown();
	}
	
	@Test
	public void get_count_of_candidate_witoutNayVote(){
		// setup
		CandidateEntity candidateEntity=new CandidateEntity();
		pm.makePersistent(candidateEntity);
		
		// action
		long actuallCount=VotedCount.init(candidateEntity).execute();
		
		// verify
		assertEquals(0, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())){
				expectedCount+=v.getQuota();
			}
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity).execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate_byAge(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		int age=RandomUtils.nextInt(0, 5);
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())==false){
				continue;
			}
			if(v.getStatisticsData().getAge()==null){
				continue;
			}
			if(v.getStatisticsData().getAge().equals(age)==false){
				continue;
			}
			expectedCount+=v.getQuota();
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity)
				.and(Field.AGE, age)
				.execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate_byDevice(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		DeviceEntity deviceEntity=deviceList.get(RandomUtils.nextInt(0, deviceList.size()));
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())==false){
				continue;
			}
			if(v.getStatisticsData().getDevice()==null){
				continue;
			}
			if(deviceEntity.getName().equals(v.getStatisticsData().getDevice().getName())==false){
				continue;
			}
			expectedCount+=v.getQuota();
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity)
				.and(Field.DEVICE, deviceEntity)
				.execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate_byLocation1(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		LocationEntity locationEntity1=locationL1List.get(RandomUtils.nextInt(0, locationL1List.size()));
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())==false){
				continue;
			}
			if(v.getStatisticsData().getLocationLayer1()==null){
				continue;
			}
			if(locationEntity1.getName().equals(v.getStatisticsData().getLocationLayer1().getName())==false){
				continue;
			}
			expectedCount+=v.getQuota();
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity)
				.and(Field.LOCATION_LAYER1, locationEntity1)
				.execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate_byLocation2(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		LocationEntity locationEntity2=locationL2List.get(RandomUtils.nextInt(0, locationL2List.size()));
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())==false){
				continue;
			}
			if(v.getStatisticsData().getLocationLayer2()==null){
				continue;
			}
			if(locationEntity2.getName().equals(v.getStatisticsData().getLocationLayer2().getName())==false){
				continue;
			}
			expectedCount+=v.getQuota();
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity)
				.and(Field.LOCATION_LAYER2, locationEntity2)
				.execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate_byOs(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		OsEntity osEntity=osList.get(RandomUtils.nextInt(0, osList.size()));
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())==false){
				continue;
			}
			if(v.getStatisticsData().getOs()==null){
				continue;
			}
			if(osEntity.getName().equals(v.getStatisticsData().getOs().getName())==false){
				continue;
			}
			expectedCount+=v.getQuota();
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity)
				.and(Field.OS, osEntity)
				.execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	public void get_count_of_candidate_bySex(){
		// setup
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		Sex sex=Sex.values()[RandomUtils.nextInt(0, Sex.values().length)];
		long expectedCount=0;
		for(VoteEntity v:voteList){
			if(v.getCandidate().getId().equals(candidateEntity.getId())==false){
				continue;
			}
			if(v.getStatisticsData().getSex()==null){
				continue;
			}
			if(sex.equals(v.getStatisticsData().getSex())==false){
				continue;
			}
			expectedCount+=v.getQuota();
		}
		
		// action
		long actuallCount=VotedCount.init(candidateEntity)
				.and(Field.SEX, sex)
				.execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
	@Test
	@Ignore("fail to filter by long(Date.class). it seems to be a bug of appengine unit test."
			+ "Please test this with integration test in the future")
	public void get_count_of_candidate_ByDate(){
		// setup
		long firstDate=voteList.get(0).getDate().getTime();
		long lastDate=voteList.get(0).getDate().getTime();
		for(VoteEntity v:voteList){
			if(firstDate>v.getDate().getTime()){
				firstDate=v.getDate().getTime();
			}
			if(lastDate<v.getDate().getTime()){
				lastDate=v.getDate().getTime();
			}
		}
		
		long leftBound=0;
		long rightBound=0;
		while(leftBound>=rightBound){
			leftBound=RandomUtils.nextLong(firstDate, lastDate+1);
			rightBound=RandomUtils.nextLong(firstDate, lastDate+1);
		}
		
		CandidateEntity candidateEntity=candidateList.get(RandomUtils.nextInt(0, candidateList.size()));
		
		long expectedCount = 0;
		for (VoteEntity v : voteList) {
			if (v.getCandidate().getId().equals(candidateEntity.getId())) {
				if (leftBound <= v.getDate().getTime() && v.getDate().getTime() < rightBound) {
					expectedCount += v.getQuota();
				}
			}
		}

		// action
		long actuallCount=VotedCount.init(candidateEntity).between(new Date(leftBound),new Date(rightBound)).execute();
		
		// verify
		assertEquals(expectedCount, actuallCount);
	}
	
}

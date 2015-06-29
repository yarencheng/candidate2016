package net.arenx.candidate2016.appengine.jdo;

import java.util.Date;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.appengine.enums.TicketType;
import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.TicketEntity;
import net.arenx.candidate2016.jdo.UserEntity;
import net.arenx.candidate2016.jdo.VoteEntity;

import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VoteEntityTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig()
			.setOAuthAuthDomain("test.com").setOAuthEmail("test@test.com").setOAuthUserId("testid")).setEnvIsAdmin(true).setEnvIsLoggedIn(true);

	PersistenceManager pm;
	UserEntity userEntity;
	
	CandidateEntity[] candidateEntities;

	@Before
	public void before() throws OAuthRequestException {
		helper.setUp();
		pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
		User user = oauthService.getCurrentUser();
		userEntity = UserEntity.getUser(user, true);
		pm.makePersistent(userEntity);
		
		candidateEntities=new CandidateEntity[10];
		for(int i=0;i<10;i++){
			candidateEntities[i]=new CandidateEntity();
		}
		pm.makePersistentAll(candidateEntities);
	}

	@After
	public void after() {
		PersistenceManagerThreadLoccal.set(null);
		pm.close();
		helper.tearDown();
	}
	
	@Test
	public void vote_paid_vote_none(){
		// setup
		CandidateEntity candidateEntity=candidateEntities[RandomUtils.nextInt(0, candidateEntities.length)];
		
		// action
		
		// verify
		long actualVotedTicket = VoteEntity.getAllVotedPaidTicket(userEntity);
		assertEquals(0, actualVotedTicket);
	}
	
	@Test
	public void vote_paid_vote_once(){
		// setup
		CandidateEntity candidateEntity=candidateEntities[RandomUtils.nextInt(0, candidateEntities.length)];
		
		// action
		long votedTicket = RandomUtils.nextLong(1, 100);
		VoteEntity voteEntity=VoteEntity.Builder.initial(userEntity, candidateEntity, votedTicket, TicketType.paid).build();
		
		// verify
		long actualVotedTicket = VoteEntity.getAllVotedPaidTicket(userEntity);
		assertEquals(votedTicket, actualVotedTicket);
	}
	
	@Test
	public void vote_paid_vote_twice(){
		// setup
		CandidateEntity candidateEntity=candidateEntities[RandomUtils.nextInt(0, candidateEntities.length)];
		long paidedTicket = RandomUtils.nextLong(1, 100);
		
		// action
		long votedTicket_1 = RandomUtils.nextLong(1, 100);
		long votedTicket_2 = RandomUtils.nextLong(1, 100);
		VoteEntity.Builder.initial(userEntity, candidateEntity, votedTicket_1, TicketType.paid).build();
		VoteEntity.Builder.initial(userEntity, candidateEntity, votedTicket_2, TicketType.paid).build();
		
		// verify
		long actualVotedTicket = VoteEntity.getAllVotedPaidTicket(userEntity);
		assertEquals(votedTicket_1+votedTicket_2, actualVotedTicket);
	}

	@Test
	public void vote_gree_vote_none() throws InterruptedException{
		// setup
		
		// action
		
		// verify
		Date voteTime = VoteEntity.getLastDateOfVoteFreeTikcket(userEntity);
		assertNull(voteTime);
	}
	
	@Test
	public void vote_gree_vote_once() throws InterruptedException{
		// setup
		CandidateEntity candidateEntity=candidateEntities[RandomUtils.nextInt(0, candidateEntities.length)];
		
		// action
		long votedTicket = RandomUtils.nextLong(1, 100);
		Date time1=new Date();
		Thread.sleep(2);
		VoteEntity.Builder.initial(userEntity, candidateEntity, votedTicket, TicketType.free).build();
		Thread.sleep(2);
		Date time2=new Date();
		
		// verify
		Date voteTime = VoteEntity.getLastDateOfVoteFreeTikcket(userEntity);
		assertTrue(time1.before(voteTime));
		assertTrue(voteTime.before(time2));
	}
	
	@Test
	public void vote_gree_vote_twice() throws InterruptedException{
		// setup
		CandidateEntity candidateEntity=candidateEntities[RandomUtils.nextInt(0, candidateEntities.length)];
		
		// action
		long votedTicket = RandomUtils.nextLong(1, 100);
		Date time1=new Date();
		Thread.sleep(2);
		VoteEntity.Builder.initial(userEntity, candidateEntity, votedTicket, TicketType.free).build();
		Thread.sleep(2);
		Date time2=new Date();
		Thread.sleep(2);
		VoteEntity.Builder.initial(userEntity, candidateEntity, votedTicket, TicketType.free).build();
		Date time3=new Date();
		
		// verify
		Date voteTime = VoteEntity.getLastDateOfVoteFreeTikcket(userEntity);
		assertTrue(time2.before(voteTime));
		assertTrue(voteTime.before(time3));
	}
	
	@Test
	public void vote_build_two_time() throws InterruptedException{
		// setup
		CandidateEntity candidateEntity=candidateEntities[RandomUtils.nextInt(0, candidateEntities.length)];
		
		// action
		VoteEntity.Builder builder = VoteEntity.Builder.initial(userEntity, candidateEntity, 1l, TicketType.free);
		builder.build();
		try {
			builder.build();
			fail();
		} catch (IllegalStateException e) {
			
		}
		
		// verify
	}
}

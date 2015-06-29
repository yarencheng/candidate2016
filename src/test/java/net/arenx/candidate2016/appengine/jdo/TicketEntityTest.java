package net.arenx.candidate2016.appengine.jdo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.appengine.enums.TicketType;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.TicketEntity;
import net.arenx.candidate2016.jdo.UserEntity;

import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class TicketEntityTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig()
			.setOAuthAuthDomain("test.com").setOAuthEmail("test@test.com").setOAuthUserId("testid")).setEnvIsAdmin(true).setEnvIsLoggedIn(true);

	PersistenceManager pm;

	@Before
	public void before() {
		helper.setUp();
		pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
	}

	@After
	public void after() {
		PersistenceManagerThreadLoccal.set(null);
		pm.close();
		helper.tearDown();
	}

	@Test
	public void createFreeTicket() throws OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
		User user = oauthService.getCurrentUser();
		UserEntity expectedUser = UserEntity.getUser(user, true);
		pm.makePersistent(expectedUser);

		// action
		long before=System.currentTimeMillis();
		TicketEntity ticketEntity = TicketEntity.createFreeTicket(expectedUser);
		long after=System.currentTimeMillis();

		// verify
		assertEquals(TicketType.free, ticketEntity.getTicketType());
		assertNotNull(ticketEntity.getId());
		assertEquals(expectedUser.getAppengineId(), ticketEntity.getOwner().getAppengineId());
		assertTrue(before<=ticketEntity.getIssuedDate().getTime()&&ticketEntity.getIssuedDate().getTime()<=after);
		assertEquals((Long)1l, ticketEntity.getQuota());
	}
	
	@Test
	public void createPaidTicket() throws OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
		User user = oauthService.getCurrentUser();
		UserEntity expectedUser = UserEntity.getUser(user, true);
		pm.makePersistent(expectedUser);

		// action
		long before=System.currentTimeMillis();
		long quota=RandomUtils.nextLong(0, 100);
		TicketEntity ticketEntity = TicketEntity.createPaidTicket(expectedUser,quota);
		long after=System.currentTimeMillis();

		// verify
		assertEquals(TicketType.paid, ticketEntity.getTicketType());
		assertNotNull(ticketEntity.getId());
		assertEquals(expectedUser.getAppengineId(), ticketEntity.getOwner().getAppengineId());
		assertTrue(before<=ticketEntity.getIssuedDate().getTime()&&ticketEntity.getIssuedDate().getTime()<=after);
		assertEquals((Long)quota, ticketEntity.getQuota());
	}
	
	@Test
	public void getAllPaidQuota_zero() throws OAuthRequestException{
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
		User user = oauthService.getCurrentUser();
		UserEntity expectedUser = UserEntity.getUser(user, true);
		pm.makePersistent(expectedUser);
		
		// action
		long quota=TicketEntity.getAllPaidQuota(expectedUser);
		
		// verify
		assertEquals(0, quota);
	}
	
	@Test
	public void getAllPaidQuota_not_zero() throws OAuthRequestException{
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
		User user = oauthService.getCurrentUser();
		UserEntity expectedUser = UserEntity.getUser(user, true);
		pm.makePersistent(expectedUser);
		long expectedQuota = RandomUtils.nextLong(0, 10)-5;
		TicketEntity.createPaidTicket(expectedUser,expectedQuota);
		
		// action
		long actuallQuota=TicketEntity.getAllPaidQuota(expectedUser);
		
		// verify
		assertEquals(expectedQuota, actuallQuota);
	}
}

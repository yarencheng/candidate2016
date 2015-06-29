package net.arenx.candidate2016.appengine.jdo;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.appengine.enums.Sex;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.UserEntity;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserEntityTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalUserServiceTestConfig()
				.setOAuthAuthDomain("test.com")
				.setOAuthEmail("test@test.com")
				.setOAuthUserId("testid")
			).setEnvIsAdmin(true).setEnvIsLoggedIn(true);

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
	public void getUser_exit() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();
	    UserEntity expectedUser = UserEntity.getUser(user, true);
		pm.makePersistent(expectedUser);

		// action
		UserEntity actuaEntity=UserEntity.getUser(user, false);

		// verify
		assertEquals(expectedUser.getAppengineId(), actuaEntity.getAppengineId());
	}
	
	@Test
	public void getUser_not_exit() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();

		// action
		UserEntity actuaEntity=UserEntity.getUser(user, false);

		// verify
		assertNull(actuaEntity);
	}
	
	@Test
	public void getUser_not_exit_and_create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();

		// action
		UserEntity actuaEntity=UserEntity.getUser(user, true);

		// verify
		assertEquals(user.getUserId(), actuaEntity.getAppengineId());
		assertEquals(user.getEmail(), actuaEntity.getEmail());
	}
	
	@Test
	public void set_getEmail() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();
	    UserEntity userEntity=UserEntity.getUser(user, true);

		// action
		String email=RandomStringUtils.random(10);
		userEntity.setEmail(email);

		// verify
		assertEquals(email, userEntity.getEmail());
	}
	
	@Test
	public void set_getSex() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();
	    UserEntity userEntity=UserEntity.getUser(user, true);

		// action
		Sex sex=Sex.values()[RandomUtils.nextInt(0, Sex.values().length)];
		userEntity.setSex(sex);

		// verify
		assertEquals(sex, userEntity.getSex());
	}
	
	@Test
	public void set_getAge() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();
	    UserEntity userEntity=UserEntity.getUser(user, true);

		// action
		Integer age=RandomUtils.nextInt(0, 100);
		userEntity.setAge(age);

		// verify
		assertEquals(age, userEntity.getAge());
	}
	
	@Test
	public void set_getIsAdmin() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, OAuthRequestException {
		// setup
		OAuthService oauthService = OAuthServiceFactory.getOAuthService();
	    User user=oauthService.getCurrentUser();
	    UserEntity userEntity=UserEntity.getUser(user, true);

		// action
		Boolean isAdmin=RandomUtils.nextInt(0, 1)==1;
		userEntity.setIsAdmin(isAdmin);

		// verify
		assertEquals(isAdmin, userEntity.getIsAdmin());
	}
}

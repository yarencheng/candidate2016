package net.arenx.candidate2016.appengine.jdo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.jdo.OsEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class OsEntityTest {

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
	public void getOs_not_exist(){
		// setup
		
		// action
		OsEntity osEntity=OsEntity.getOs("test", false);
		
		// verify
		assertNull(osEntity);
	}
	
	@Test
	public void getOs_not_exist_create(){
		// setup
		
		// action
		String name=RandomStringUtils.random(10);
		OsEntity osEntity=OsEntity.getOs(name, true);
		
		// verify
		assertEquals(name,osEntity.getName());
	}
	
	@Test
	public void getOs_not_exist_create_name_too_long(){
		// setup
		
		// action
		String name=RandomStringUtils.random(100);
		try{
			OsEntity osEntity=OsEntity.getOs(name, true);
			fail();
		}catch(IllegalArgumentException e){
			
		}
		
		// verify
	}
	
	@Test
	public void getOs_exist(){
		// setup
		String name=RandomStringUtils.random(10);
		OsEntity expectedOsEntity=OsEntity.getOs(name, true);
		
		// action
		OsEntity actuallOsEntity=OsEntity.getOs(name, false);
		
		// verify
		assertEquals(expectedOsEntity.getName(),actuallOsEntity.getName());
	}
	
	@Test
	public void getOs_exist_has_realOs(){
		// setup
		String realName=RandomStringUtils.random(10);
		OsEntity realOs=OsEntity.getOs(realName, true);
		String fakeName=RandomStringUtils.random(10);
		OsEntity fakeOs=OsEntity.getOs(fakeName, true);
		fakeOs.setRealOs(realOs);
		
		// action
		OsEntity actuallOsEntity=OsEntity.getOs(fakeName, false);
		
		// verify
		assertEquals(realOs.getName(),actuallOsEntity.getName());
	}
	
	@Test
	public void set_getRealOs(){
		// setup
		String realName=RandomStringUtils.random(10);
		OsEntity realOs=OsEntity.getOs(realName, true);
		String fakeName=RandomStringUtils.random(10);
		OsEntity fakeOs=OsEntity.getOs(fakeName, true);
		
		// action
		fakeOs.setRealOs(realOs);
		
		// verify
		assertEquals(realOs.getName(),fakeOs.getRealOs().getName());
	}
}

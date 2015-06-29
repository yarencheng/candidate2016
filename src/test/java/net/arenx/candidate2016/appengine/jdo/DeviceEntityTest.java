package net.arenx.candidate2016.appengine.jdo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.jdo.DeviceEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class DeviceEntityTest {

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
	public void getDevice_not_exist(){
		// setup
		
		// action
		DeviceEntity deviceEntity=DeviceEntity.getDevice("test", false);
		
		// verify
		assertNull(deviceEntity);
	}
	
	@Test
	public void getDevice_not_exist_create(){
		// setup
		
		// action
		String name=RandomStringUtils.random(10);
		DeviceEntity deviceEntity=DeviceEntity.getDevice(name, true);
		
		// verify
		assertEquals(name,deviceEntity.getName());
	}
	
	@Test
	public void getDevice_not_exist_create_name_too_long(){
		// setup
		
		// action
		String name=RandomStringUtils.random(100);
		try{
			DeviceEntity deviceEntity=DeviceEntity.getDevice(name, true);
			fail();
		}catch(IllegalArgumentException e){
			
		}
		
		// verify
	}
	
	@Test
	public void getDevice_exist(){
		// setup
		String name=RandomStringUtils.random(10);
		DeviceEntity expectedDeviceEntity=DeviceEntity.getDevice(name, true);
		
		// action
		DeviceEntity actuallDeviceEntity=DeviceEntity.getDevice(name, false);
		
		// verify
		assertEquals(expectedDeviceEntity.getName(),actuallDeviceEntity.getName());
	}
	
	@Test
	public void getDevice_exist_has_realOs(){
		// setup
		String realName=RandomStringUtils.random(10);
		DeviceEntity realDevice=DeviceEntity.getDevice(realName, true);
		String fakeName=RandomStringUtils.random(10);
		DeviceEntity fakeDevice=DeviceEntity.getDevice(fakeName, true);
		fakeDevice.setRealDevice(realDevice);
		
		// action
		DeviceEntity actuallDeviceEntity=DeviceEntity.getDevice(fakeName, false);
		
		// verify
		assertEquals(realDevice.getName(),actuallDeviceEntity.getName());
	}
	
	@Test
	public void set_getRealOs(){
		// setup
		String realName=RandomStringUtils.random(10);
		DeviceEntity realDevice=DeviceEntity.getDevice(realName, true);
		String fakeName=RandomStringUtils.random(10);
		DeviceEntity fakeOs=DeviceEntity.getDevice(fakeName, true);
		
		// action
		fakeOs.setRealDevice(realDevice);
		
		// verify
		assertEquals(realDevice.getName(),fakeOs.getRealDevice().getName());
	}
}

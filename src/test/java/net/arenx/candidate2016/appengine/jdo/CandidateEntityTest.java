package net.arenx.candidate2016.appengine.jdo;

import java.util.List;

import javax.jdo.PersistenceManager;

import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class CandidateEntityTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	
	PersistenceManager pm;
	
	@Before
	public void before(){
		helper.setUp();
		pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
	}
	
	@After
	public void after(){
		PersistenceManagerThreadLoccal.set(null);
		pm.close();
		helper.tearDown();
	}
	
	@Test
	public void getCandidate() throws InstantiationException, IllegalAccessException {
		// setup
		CandidateEntity expectCandidateEntity=CandidateEntity.class.newInstance();
		pm.makePersistent(expectCandidateEntity);
		
		// action
		CandidateEntity actuallCandidateEntity=CandidateEntity.getCandidate(expectCandidateEntity.getId());
		
		// verify
		assertEquals(expectCandidateEntity.getId(), actuallCandidateEntity.getId());
	}
	
	@Test
	public void getCandidate_null() throws InstantiationException, IllegalAccessException {
		// setup
		
		// action
		CandidateEntity actuallCandidateEntity=CandidateEntity.getCandidate(1l);
		
		// verify
		assertNull(actuallCandidateEntity);
	}
	
	@Test
	public void set_getInfo() throws InstantiationException, IllegalAccessException {
		// setup
		CandidateEntity candidateEntity=CandidateEntity.class.newInstance();
		String info=RandomStringUtils.random(10);
		
		// action
		candidateEntity.setInfo(info);
		
		// verify
		assertEquals(info, candidateEntity.getInfo());
	}
	
	@Test
	public void getAllandidates() throws InstantiationException, IllegalAccessException {
		// setup
		CandidateEntity[] expectCandidateEntities=new CandidateEntity[RandomUtils.nextInt(2, 10)];
		for(int i=0;i<expectCandidateEntities.length;i++){
			expectCandidateEntities[i]=CandidateEntity.class.newInstance(); 
		}
		pm.makePersistentAll(expectCandidateEntities);
		
		// action
		List<CandidateEntity> actuallCandidateEntiList=CandidateEntity.getAllandidates();
		
		// verify
		for(CandidateEntity expectCandidateEntity:expectCandidateEntities){
			boolean found = false;
			for(CandidateEntity actuallCandidateEntity:actuallCandidateEntiList){
				if(actuallCandidateEntity.getId()==expectCandidateEntity.getId()){
					found=true;
					break;
				}
			}
			assertTrue(found);
		}
	}
	
	@Test
	public void getAllandidates_empty() throws InstantiationException, IllegalAccessException {
		// setup
		
		// action
		List<CandidateEntity> actuallCandidateEntiList=CandidateEntity.getAllandidates();
		
		// verify
		assertTrue(actuallCandidateEntiList.isEmpty());
	}
}

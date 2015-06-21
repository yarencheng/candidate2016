package net.arenx.candidate2016.appengine.api;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import net.arenx.candidate2016.appengine.enums.TicketType;
import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.DeviceEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.TicketEntity;
import net.arenx.candidate2016.jdo.UserEntity;
import net.arenx.candidate2016.jdo.VoteEntity;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.AuthLevel;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.users.User;

@ApiReference(AbstractApi.class)
@Api()
public class TestApi {

	private static final Logger logger = Logger.getLogger(TestApi.class.getName());
	
	@ApiMethod(
			name = "test1",
			path="test1",
			httpMethod = HttpMethod.GET,
			authLevel=AuthLevel.OPTIONAL
			)
    public void test1(
    		User user
    		) {
		
		PersistenceManager pm=PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try{
			
			
		}finally{
			pm.close();
		}
		
    }
	
	@ApiMethod(
			name = "test2",
			path="test2",
			httpMethod = HttpMethod.GET,
			authLevel=AuthLevel.OPTIONAL
			)
    public void test2(
    		User user
    		) {
		
		PersistenceManager pm=PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try{
			
			
			Query q = pm.newQuery(VoteEntity.class,
                    "(statisticsData.locationLayer1 == 'l0')");

			List<VoteEntity> results = (List<VoteEntity>) q.execute();
			for(int i=0;i<results.size();i++){
				logger.warning("result = "+results.get(i));
			}
		}finally{
			pm.close();
		}
		
    }
	
}

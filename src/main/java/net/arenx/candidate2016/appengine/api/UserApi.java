package net.arenx.candidate2016.appengine.api;

import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

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
public class UserApi {

	private static final Logger logger = Logger.getLogger(UserApi.class.getName());

	@ApiMethod(name = "getCurrentUser", path = "user", httpMethod = HttpMethod.GET, authLevel = AuthLevel.REQUIRED)
	public UserInfoBean getCurrentUser(User user) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			UserEntity userEntity = UserEntity.getUser(user, true);
			UserInfoBean userInfoBean = new UserInfoBean();
			userInfoBean.remainingPaidTickets = TicketEntity.getAllPaidQuota(userEntity) - VoteEntity.getAllVotedPaidTicket(userEntity);
			userInfoBean.lastTimeVoteFreeTicket = VoteEntity.getLastDateOfVoteFreeTikcket(userEntity);
			return userInfoBean;
		} finally {
			pm.close();
		}
	}

}

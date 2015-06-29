package net.arenx.candidate2016.appengine.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.Validate;

import net.arenx.candidate2016.appengine.Candidate;
import net.arenx.candidate2016.jdo.AppConfigEntity;
import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.LocationEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.TicketEntity;
import net.arenx.candidate2016.jdo.UserEntity;
import net.arenx.candidate2016.jdo.VoteEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.AuthLevel;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;



@ApiReference(AbstractApi.class)
@Api()
public class AppConfigApi {

	private static final Logger logger = Logger.getLogger(AppConfigApi.class.getName());

	public static class Config {
		public String key;
		public String value;
	}

	@ApiMethod(name = "setAppConfig", path = "appConfig", httpMethod = HttpMethod.PUT, authLevel = AuthLevel.REQUIRED)
	public void setAppConfig(@Named("key") String key, @Named("value") String value, User user) throws OAuthRequestException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			UserEntity userEntity = UserEntity.getUser(user, true);
			if (userEntity.getIsAdmin() == null || userEntity.getIsAdmin() == false) {
				throw new OAuthRequestException("not admin");
			}
			AppConfigEntity.Key key_ = AppConfigEntity.Key.valueOf(key);
			Validate.notNull(key_, "invalid key");
			AppConfigEntity.set(key_, value);
		} finally {
			pm.close();
		}
	}

	@ApiMethod(name = "getAppConfig", path = "appConfig/{key}", httpMethod = HttpMethod.GET, authLevel = AuthLevel.REQUIRED)
	public Config getAppConfig(@Named("key") String key, User user) throws OAuthRequestException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			UserEntity userEntity = UserEntity.getUser(user, true);
			if (userEntity.getIsAdmin() == null || userEntity.getIsAdmin() == false) {
				throw new OAuthRequestException("not admin");
			}
			AppConfigEntity.Key key_ = AppConfigEntity.Key.valueOf(key);
			Validate.notNull(key_, "invalid key");
			Config config = new Config();
			config.key = key;
			config.value = AppConfigEntity.get(key_);
			return config;
		} finally {
			pm.close();
		}
	}

	@ApiMethod(name = "getAllAppConfig", path = "appConfig/all", httpMethod = HttpMethod.GET, authLevel = AuthLevel.REQUIRED)
	public Config[] getAllAppConfig(User user) throws OAuthRequestException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			UserEntity userEntity = UserEntity.getUser(user, true);
			if (userEntity.getIsAdmin() == null || userEntity.getIsAdmin() == false) {
				throw new OAuthRequestException("not admin");
			}
			Config[] config = new Config[AppConfigEntity.Key.values().length];
			for (int i = 0; i < config.length; i++) {
				config[i] = new Config();
				config[i].key = AppConfigEntity.Key.values()[i].toString();
				config[i].value = AppConfigEntity.get(AppConfigEntity.Key.values()[i]);
			}
			return config;
		} finally {
			pm.close();
		}
	}

	@ApiMethod(
			name = "importLocation",
			path="appConfig/importLocation",
			httpMethod = HttpMethod.POST,
			authLevel=AuthLevel.REQUIRED
			)
    public void importLocation(
    		User user,
    		ServletContext servletContext
    		) throws OAuthRequestException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			UserEntity userEntity = UserEntity.getUser(user, true);
			if(userEntity.getIsAdmin()==null||userEntity.getIsAdmin()==false){
				throw new OAuthRequestException("not admin");
			}
			
			ObjectMapper mapper=new ObjectMapper();
			LocationEntity[] list=mapper.readValue(servletContext.getResourceAsStream("/WEB-INF/location.json"), LocationEntity[].class);
			pm.makePersistentAll(list);
			
		} finally {
			pm.close();
		}
    }
	
	@ApiMethod(
			name = "importCandidate",
			path="appConfig/importCandidate",
			httpMethod = HttpMethod.POST,
			authLevel=AuthLevel.REQUIRED
			)
    public void importCandidate(
    		User user,
    		ServletContext servletContext
    		) throws OAuthRequestException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			UserEntity userEntity = UserEntity.getUser(user, true);
			if(userEntity.getIsAdmin()==null||userEntity.getIsAdmin()==false){
				throw new OAuthRequestException("not admin");
			}
			
			ObjectMapper mapper=new ObjectMapper();
			CandidateEntity[] list=mapper.readValue(servletContext.getResourceAsStream("/WEB-INF/candidate.json"), CandidateEntity[].class);
			pm.makePersistentAll(list);
			
		} finally {
			pm.close();
		}
    }
}

package net.arenx.candidate2016.appengine.api;

import net.arenx.candidate2016.appengine.Candidate;

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
public class CandidateApi {

	@ApiMethod(
			name = "getCandidate",
			path="candidate/{candidateId}",
			httpMethod = HttpMethod.GET,
			authLevel=AuthLevel.NONE
			)
    public Candidate getCandidate(
    		@Named("candidateId") Long candidateId
    		) {
		return null;
    }
	
	@ApiMethod(
			name = "getAllCandidates",
			path="candidate/all",
			httpMethod = HttpMethod.GET,
			authLevel=AuthLevel.NONE
			)
    public Candidate[] getAllCandidates() {
		return null;
    }
	
}

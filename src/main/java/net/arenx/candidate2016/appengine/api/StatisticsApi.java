package net.arenx.candidate2016.appengine.api;

import net.arenx.candidate2016.appengine.api.VoteStatus.Status;
import net.arenx.candidate2016.appengine.enums.Sex;

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
public class StatisticsApi {

	public static class SupportRate{
		public Long candidateId;
		public Long ticketCount;
		public Double percentage;
	}
	@ApiMethod(
			name = "statistics",
			path="statistics/supportRate",
			httpMethod = HttpMethod.GET,
			authLevel=AuthLevel.NONE
			)
    public VoteStatus vote(
    		@Named("sex") Sex sex
    		) {
		VoteStatus status=new VoteStatus();
		status.status=VoteStatus.Status.SUCCESS;
		return status;
    }
	
}

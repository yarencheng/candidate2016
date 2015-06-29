package net.arenx.candidate2016.appengine.api;

import java.util.Date;

import javax.jdo.PersistenceManager;

import org.apache.commons.lang3.Validate;

import net.arenx.candidate2016.appengine.VoteStatus;
import net.arenx.candidate2016.appengine.enums.Sex;
import net.arenx.candidate2016.appengine.enums.TicketType;
import net.arenx.candidate2016.jdo.AppConfigEntity;
import net.arenx.candidate2016.jdo.CandidateEntity;
import net.arenx.candidate2016.jdo.DeviceEntity;
import net.arenx.candidate2016.jdo.LocationEntity;
import net.arenx.candidate2016.jdo.OsEntity;
import net.arenx.candidate2016.jdo.PMF;
import net.arenx.candidate2016.jdo.PersistenceManagerThreadLoccal;
import net.arenx.candidate2016.jdo.TicketEntity;
import net.arenx.candidate2016.jdo.UserEntity;
import net.arenx.candidate2016.jdo.VoteEntity;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.AuthLevel;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.users.User;

@ApiReference(AbstractApi.class)
@Api()
public class VoteApi {

	@ApiMethod(
			name = "vote",
			path="vote/{candidateId}",
			httpMethod = HttpMethod.POST,
			authLevel=AuthLevel.REQUIRED
			)
    public VoteStatus vote(
    		@Named("candidateId") Long candidateId,
    		@Named("ticketCount") Integer ticketCount,
    		@Named("age") @Nullable Integer age,
    		@Named("Sex") @Nullable Sex sex,
    		@Named("longitude") @Nullable Double longitude,
    		@Named("latitude") @Nullable Double latitude,
    		@Named("locationLayer1") @Nullable String locationLayer1,
    		@Named("locationLayer2") @Nullable String locationLayer2,
    		@Named("osType") @Nullable String osType,
    		@Named("device") @Nullable String device,
    		User user
    		) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PersistenceManagerThreadLoccal.set(pm);
		try {
			Validate.isTrue(ticketCount>0,"ticketCount must more than 0");
			
			// get & check candidate
			CandidateEntity candidateEntity = CandidateEntity.getCandidate(candidateId);
			if(candidateEntity==null){
				VoteStatus voteStatus =new VoteStatus();
				voteStatus.status=VoteStatus.Status.FAILED_NO_SUCH_CANDIDATE;
				return voteStatus;
			}			
			
			// check ticket quota
			UserEntity userEntity = UserEntity.getUser(user, true);
			long remainingPaidTickets = TicketEntity.getAllPaidQuota(userEntity) - VoteEntity.getAllVotedPaidTicket(userEntity);
			Date lastTimeVoteFreeTicket = VoteEntity.getLastDateOfVoteFreeTikcket(userEntity);
			int remainingFreeTickets = 0;
			if(lastTimeVoteFreeTicket == null || 
					System.currentTimeMillis()-lastTimeVoteFreeTicket.getTime()>= Integer.parseInt(AppConfigEntity.get(AppConfigEntity.Key.FREE_TICKET_ISSUE_INTERVAL))){
				remainingFreeTickets = 1;
			}
			if(remainingPaidTickets + remainingFreeTickets < ticketCount){
				VoteStatus voteStatus =new VoteStatus();
				voteStatus.status=VoteStatus.Status.FAILED_NOT_ENOUGH_TICKET;
				return voteStatus;
			}
			
			long numberOfPaidTicket = remainingPaidTickets > ticketCount ? ticketCount : remainingPaidTickets;
			long numberOfFreeTicket = numberOfPaidTicket < ticketCount ? remainingFreeTickets : 0;
			
			// create vote
			if(numberOfPaidTicket>0){
				VoteEntity.Builder.initial(userEntity, candidateEntity, numberOfPaidTicket, TicketType.paid)
					.setAge(age!=null ? age : userEntity.getAge())
					.setDevice(device!=null ? DeviceEntity.getDevice(device, true) : null)
					.setLatitude(latitude!=null ? latitude : null)
					.setLogitude(longitude!=null ? longitude : null)
					.setLocationLayer1(LocationEntity.getLocation(locationLayer1, 1))
					.setLocationLayer2(LocationEntity.getLocation(locationLayer2, 2))
					.setOsType(osType!=null ? OsEntity.getOs(osType, true) : null)
					.setSex(sex)
					.build();				
			}
			if(numberOfFreeTicket>0){
				VoteEntity.Builder.initial(userEntity, candidateEntity, numberOfFreeTicket, TicketType.free)
				.setAge(age!=null ? age : userEntity.getAge())
				.setDevice(device!=null ? DeviceEntity.getDevice(device, true) : null)
				.setLatitude(latitude!=null ? latitude : null)
				.setLogitude(longitude!=null ? longitude : null)
				.setLocationLayer1(LocationEntity.getLocation(locationLayer1, 1))
				.setLocationLayer2(LocationEntity.getLocation(locationLayer2, 2))
				.setOsType(osType!=null ? OsEntity.getOs(osType, true) : null)
				.setSex(sex)
				.build();
			}
			
			// return success
			VoteStatus voteStatus =new VoteStatus();
			voteStatus.status=VoteStatus.Status.SUCCESS;
			return voteStatus;
		} finally {
			pm.close();
		}
    }
	
}

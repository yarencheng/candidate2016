package net.arenx.candidate2016.jdo;

import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.arenx.candidate2016.appengine.enums.TicketType;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class TicketEntity {

	private TicketEntity(){
	}
	
	public static TicketEntity createFreeTicket(UserEntity owner){
		Validate.notNull(owner);
		TicketEntity ticket=new TicketEntity();
		ticket.ticketType=TicketType.free;
		ticket.owner=owner;
		ticket.issuedDate=new Date(System.currentTimeMillis());
		ticket.quota=1L;
		PersistenceManagerThreadLoccal.get().makePersistent(ticket);
		return ticket;
	}
	
	public static TicketEntity createPaidTicket(UserEntity owner,Long quota){
		Validate.notNull(owner);
		Validate.notNull(quota);
		TicketEntity ticket=new TicketEntity();
		ticket.ticketType=TicketType.paid;
		ticket.owner=owner;
		ticket.issuedDate=new Date(System.currentTimeMillis());
		ticket.quota=quota;
		PersistenceManagerThreadLoccal.get().makePersistent(ticket);
		return ticket;
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private TicketType ticketType;
	
	@Persistent
	private Date issuedDate;
	
	@Unowned
	@Persistent
	private UserEntity owner;
	
	@Persistent
	private Long quota;
	
	public Long getId() {
		return id;
	}

	public TicketType getTicketType() {
		return ticketType;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}

	public UserEntity getOwner() {
		return owner;
	}

	public Long getQuota() {
		return quota;
	}

	public static Long getAllPaidQuota(UserEntity owner){
		Validate.notNull(owner);
		PersistenceManager pm=PersistenceManagerThreadLoccal.get();
		Query query = pm.newQuery(TicketEntity.class);
		query.setFilter("owner == x && ticketType == y");
		query.declareParameters(UserEntity.class.getName()+" x, "+TicketType.class.getName()+" y");
		query.setResult("sum(this.quota)");
		Long quota = (Long) query.execute(owner,TicketType.paid);
		return quota == null ? 0 : quota;
	}
	
}

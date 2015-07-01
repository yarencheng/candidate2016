package net.arenx.candidate2016.appengine.api;

public class VoteStatus{
	
	public enum Status{
		SUCCESS,
		FAILED_UNKNOWN,
		FAILED_NOT_ENOUGH_TICKET,
		FAILED_NO_SUCH_CANDIDATE
	}
	
	public Status status;
}
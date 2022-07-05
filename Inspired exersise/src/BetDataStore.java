import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public class BetDataStore
{
	private UUID userId;
	private UUID eventId;
	private UUID betId;
	private Market market;
	private Instant timePlaced;
	private BigInteger wager;
	private boolean hasPaidOut;
	
	public BetDataStore(UUID userId, UUID eventId, Market market, BigInteger wager) 
	{
		this.eventId = eventId;
		this.userId = userId;
		this.market = market;
		this.wager = wager;
		this.timePlaced = Instant.now();
		betId = UUID.randomUUID();
	}
	
	public BigInteger payout(UserDataStore user) throws MessageException 
	{
		if(hasPaidOut) throw new MessageException("This bet has already been paid out");
		if(!user.getId().equals(this.userId)) throw new MessageException("This is a bet placed by another user");
		
		EventDataStore eventData = DataBase.getEventData(eventId);
		hasPaidOut = true;
		return eventData.checkBet(this, user);
	}
	
	public boolean hasPaidOut() 
	{
		return this.hasPaidOut;
	}
	
	public UUID getUserId()
	{
		return userId;
	}
	
	public UUID getEventId()
	{
		return eventId;
	}

	public Instant getTimePlaced()
	{
		return timePlaced;
	}

	public Market getMarket()
	{
		return market;
	}

	public BigInteger getWager()
	{
		return wager;
	}

	public UUID getBetId()
	{
		return betId;
	}
	
}

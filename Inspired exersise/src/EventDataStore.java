import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

public class EventDataStore
{
	public final Instant openTime;
	public final Instant closeTime;
	public final Instant rollRngTime;
	public final String name;
	public final int[] participants;
	public final int typeOrdinal;
	private boolean hasRolledRng;
	/*
	 * results here are Markets that happened
	 */
	private HashSet<Market> results;
	private UUID id;
	
	public EventDataStore(EventType type, Instant openTime, String name, int[] participants) 
	{
		this.openTime = openTime;
		this.closeTime = openTime.plusMillis(type.bettingTime.toMillis());
		this.rollRngTime = this.closeTime.plusMillis(Config.closeBetTime);
		this.id = UUID.randomUUID();
		this.typeOrdinal = type.ordinal;
		this.name = name;
		this.participants = participants;
	}
	
	
	public void rollRng() 
	{
		EventType type = App.getEventTypeByOrdinal(typeOrdinal);
		results = type.rollEvents(participants);
		hasRolledRng = true;
	}
	
	/**
	 * 
	 * @param bet
	 * @return payout
	 * @throws MessageException
	 */
	public BigInteger checkBet(BetDataStore bet, UserDataStore user) throws MessageException 
	{
		Instant now = Instant.now();
		
		if(now.isBefore(rollRngTime)) throw new MessageException("Event has not concluded yet");
		
		if(results.contains(bet.getMarket())) 
		{
			EventType type = App.getEventTypeByOrdinal(typeOrdinal);
			double odds = type.getOdds(participants, bet.getMarket());
			BigDecimal oddsDecimal = new BigDecimal(odds);
			
			BigDecimal payout = oddsDecimal.multiply(new BigDecimal(bet.getWager()));
			payout = payout.round(new MathContext(100, RoundingMode.UP));
			
			BigInteger integerPayout = payout.toBigInteger();
			user.addWinnings(integerPayout);
			
			return integerPayout;
		}
		
		return BigInteger.ZERO;
	}

	public UUID getId()
	{
		return id;
	}
	
	public Market[] getMarkets() 
	{
		return App.getEventTypeByOrdinal(typeOrdinal).markets;
	}
	
	@Override
	public String toString() 
	{
		return this.name;
	}


	public boolean hasRolledRng()
	{
		return hasRolledRng;
	}
}

















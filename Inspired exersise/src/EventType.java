import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;


public abstract class EventType
{
	public final Market[] markets;
	public final Duration bettingTime;
	private static int ordinalTracker = 0;
	public final int ordinal;
	
	protected EventType(Market[] markets, Duration bettingTime) 
	{
		this.markets = markets;
		this.bettingTime = bettingTime;
		this.ordinal = ordinalTracker;
		ordinalTracker++;
	}
	
	public abstract EventDataStore generateEvent(Instant startTime);

	public static int getOrdinalCount()
	{
		return ordinalTracker;
	}
	
	public abstract HashSet<Market> rollEvents(int[] participants);
	
	
	/**
	 * 
	 * @param participants
	 * @param market
	 * @return return x where 1:x is the payout multiplier to a winning bet, e.g. on a 1:1.5, winning a 50p bet would return 75p for a 25p gain
	 * @throws MessageException 
	 */
	protected abstract double getOdds(int[] participants, Market market) throws MessageException;
}

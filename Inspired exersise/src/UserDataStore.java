import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

public class UserDataStore
{
	private BigInteger balance;
	private String name;
	private UUID id;
	
	public UserDataStore(BigInteger balance, String name) 
	{
		this.balance = balance; 
		this.name = name; 
		this.id = UUID.randomUUID(); 
	}
	
	public UUID getId() 
	{
		return this.id;
	}
	
	public void placeBet(BigInteger wager, UUID eventId, Market market) throws MessageException
	{
		if(wager.compareTo(balance) == 1)throw new MessageException("cannot wager more than your balance");
		
		if(DataBase.getEventData(eventId).closeTime.isBefore(Instant.now())) throw new MessageException("this event is not accepting any new bets");
		
		BetDataStore data = new BetDataStore(this.id, eventId, market, wager);
		this.balance = this.balance.subtract(wager);
		DataBase.storeBet(data);
		DataBase.userDataChanged();
	}
	
	public void checkBet(BetDataStore bet) throws MessageException
	{
		this.balance.add(bet.payout(this));
		DataBase.userDataChanged();
	}

	public String getName()
	{
		return name;
	}
	
	public BigInteger getBalance() 
	{
		return this.balance;
	}

	public void addWinnings(BigInteger integerPayout)
	{
		this.balance = this.balance.add(integerPayout);
		DataBase.userDataChanged();
	}
}

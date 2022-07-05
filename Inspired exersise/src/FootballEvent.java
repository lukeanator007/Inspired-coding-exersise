import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

public class FootballEvent extends EventType
{
	public FootballEvent() 
	{
		super(new Market[] {Market.HOME_WINS, Market.AWAY_WINS, Market.FOOLBALL_HOME_HATTRICK, Market.FOOTBALL_AWAY_HATTRICK, Market.FOOTBALL_ANY_HATTRICK}, Duration.ofMinutes(1));
		
	}

	@Override
	public EventDataStore generateEvent(Instant startTime)
	{
	    int homeTeam=(int)(Math.random()*Team.values().length);
	    int awayTeam=(int)(Math.random()*(Team.values().length)-1);
	    if(homeTeam<=awayTeam) awayTeam++;
		
	    String eventName = Team.values()[homeTeam].displayString + " hosts " + Team.values()[awayTeam].displayString;
		EventDataStore data = new EventDataStore(this, startTime, eventName, new int[] {homeTeam, awayTeam});
		
		return data;
	}
	
	public enum Team
	{
		MANCHESTER(50,10,2, "Manchester"), CHELSEA(100,-10,2, "Chealsea"), AFCBOURNEMOUTH(50,50,5, "A.F.C Bournemouth"), 
		ARSENAL(40,5,1, "Arsenal"), TOTTENHAM(70,15,3, "Tottenham"), ;
		
		public final int winningWeight;
		public final int homeAdvantageWeight;
		public final int hatTrickWeight;
		public final static int noHattrickWeight = 100;
		public final String displayString;
		
		private Team(int winningWeight, int homeAdvantageWeight, int hattrickWeight, String displayString)
		{
			this.winningWeight = winningWeight;
			this.homeAdvantageWeight = homeAdvantageWeight;
			this.hatTrickWeight = hattrickWeight;
			this.displayString = displayString;
		}
	}

	@Override
	public HashSet<Market> rollEvents(int[] participants)
	{
		HashSet<Market> results = new HashSet<Market>();
		Team homeTeam = Team.values()[participants[0]]; 
		Team awayTeam = Team.values()[participants[1]]; 
		
		int homeTeamWinWeight = homeTeam.homeAdvantageWeight + homeTeam.winningWeight;
		int totalWeight = homeTeamWinWeight + awayTeam.winningWeight; 
		
		int roll = (int)(Math.random()*totalWeight)+1;
		
		if(roll > homeTeamWinWeight) results.add(Market.AWAY_WINS);
		else results.add(Market.HOME_WINS);
		
		int hatTrickHomeRoll = (int)(Math.random()*(homeTeam.hatTrickWeight+Team.noHattrickWeight))+1;
		int hatTrickAwayRoll = (int)(Math.random()*(homeTeam.hatTrickWeight+Team.noHattrickWeight))+1;
		
		if(hatTrickHomeRoll <= homeTeam.hatTrickWeight) 
		{
			results.add(Market.FOOLBALL_HOME_HATTRICK);
			results.add(Market.FOOTBALL_ANY_HATTRICK);
		}
		if(hatTrickAwayRoll <= awayTeam.hatTrickWeight)
		{
			results.add(Market.FOOTBALL_AWAY_HATTRICK);
			results.add(Market.FOOTBALL_ANY_HATTRICK);
		}
		
		
		return results;
	}

	@Override
	protected double getOdds(int[] participants, Market market) throws MessageException
	{
		//payout formula = 1 + (totalWeight - eventWieght)/eventWeight for a 0 sum game
		
		Team homeTeam = Team.values()[participants[0]]; 
		Team awayTeam = Team.values()[participants[1]]; 
		
		double homeTeamWinWeight = homeTeam.homeAdvantageWeight + homeTeam.winningWeight;
		double awayTeamWinWeight = awayTeam.winningWeight;
		double totalWinWeight = homeTeamWinWeight + awayTeamWinWeight - Config.houseAdvantageWeight; 
		double totalHattrickWeight = 100 - Config.houseAdvantageWeight;
		
		double totalWeight;
		double eventWeight;
		
		
		//calculate chance to win, then add 1 to return investment   
		double value = 0;
		switch(market) 
		{
		case AWAY_WINS:
			totalWeight = totalWinWeight;
			eventWeight = awayTeamWinWeight;
			break;
		case HOME_WINS:
			totalWeight = totalWinWeight;
			eventWeight = homeTeamWinWeight;
			break;
		case FOOLBALL_HOME_HATTRICK:
			totalWeight = totalHattrickWeight;
			eventWeight = homeTeam.hatTrickWeight;
			break;
		case FOOTBALL_ANY_HATTRICK:
			totalWeight = totalHattrickWeight;
			eventWeight = awayTeam.hatTrickWeight + homeTeam.hatTrickWeight;
			break;
		case FOOTBALL_AWAY_HATTRICK:
			totalWeight = totalHattrickWeight;
			eventWeight = awayTeam.hatTrickWeight;
			break;
		default:
			throw new MessageException("Market not recognised");
		}
		value = (totalWeight - eventWeight)/eventWeight +1;
		return value;
		
	}
}



























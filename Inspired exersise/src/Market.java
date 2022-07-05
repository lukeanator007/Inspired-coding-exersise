
public enum Market
{
	HOME_WINS("Home Wins"), AWAY_WINS("Away Wins"), FOOTBALL_ANY_HATTRICK("Any hattrick"), FOOTBALL_AWAY_HATTRICK("Away team hattrick"),
	FOOLBALL_HOME_HATTRICK("Home team hattrick");
	
	public final String displayString;
	
	private Market(String displayString) 
	{
		this.displayString = displayString;
	}
	
	@Override
	public String toString() 
	{
		return displayString;
	}
}

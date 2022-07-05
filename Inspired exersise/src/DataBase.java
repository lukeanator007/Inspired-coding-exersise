import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class DataBase
{

	private static HashMap<UUID, BetDataStore> betData = new  HashMap<UUID, BetDataStore>();
	private static HashMap<UUID, EventDataStore> pastEventData = new  HashMap<UUID, EventDataStore>();
	private static HashMap<UUID, EventDataStore> currentEventData = new  HashMap<UUID, EventDataStore>();
	private static HashMap<UUID, UserDataStore> userData = new HashMap<UUID, UserDataStore>();
	private static boolean eventDataHasChanged = false;
	private static boolean userDataHasChanged = false;
	
	public static void storeBet(BetDataStore data) 
	{
		betData.put(data.getBetId(), data);
	}
	
	public static void storeEvent(EventDataStore data) 
	{
		currentEventData.put(data.getId(), data);
		eventDataHasChanged = true;
	}
	
	public static void userDataChanged() 
	{
		userDataHasChanged = true;
	}

	public static void userDataRecieved()
	{
		eventDataHasChanged = false;
	}
	
	public static void checkEventTimer() 
	{
		Instant currentTime = Instant.now();
		HashSet<UUID> toRemove = new HashSet<UUID>();
		for(UUID key : currentEventData.keySet()) 
		{
			EventDataStore data = currentEventData.get(key);
			if(data.rollRngTime.isBefore(currentTime) && !data.hasRolledRng()) 
			{
				toRemove.add(data.getId());
				data.rollRng();
				pastEventData.put(data.getId(), data);
				eventDataHasChanged = true;
			}
		}

		for(UUID id : toRemove) 
		{
			currentEventData.remove(id);
		}
	}
	
	public static HashSet<BetDataStore> searchBets(boolean all, boolean collectable, UUID userId) throws MessageException
	{
		HashSet<BetDataStore> ans = new HashSet<BetDataStore>();
		
		for(UUID key: DataBase.betData.keySet()) 
		{
			BetDataStore data = betData.get(key);
			if(userId.equals(data.getUserId())) 
			{
				if(all) ans.add(data);
				else if(collectable && !data.hasPaidOut()) 
				{
					EventDataStore event = DataBase.getEventData(data.getEventId());
					if(event.rollRngTime.isBefore(Instant.now())) ans.add(data);
				}
			}
		}
		
		return ans;
	} 
	
	public static BetDataStore getBet(UUID id) 
	{
		return betData.get(id);
	}
	
	public static EventDataStore getEventData(UUID id) throws MessageException 
	{
		EventDataStore data = currentEventData.get(id);
		if(data == null) data = pastEventData.get(id);
		if(data == null) throw new MessageException("Event does not exist");
		return data;
	}
	
	public static UUID createNewUser(BigInteger balance, String name) 
	{
		UserDataStore user = new UserDataStore(balance, name);
		userData.put(user.getId(), user);
		return user.getId();
	}

	public static UserDataStore getUser(UUID userId)
	{
		return userData.get(userId);
	}
	
	public static HashSet<EventDataStore> getCurrentEvents()
	{
		HashSet<EventDataStore> data = new HashSet<EventDataStore>();
		for(UUID key : currentEventData.keySet()) data.add(currentEventData.get(key));
		
		return data;
	}

	public static boolean hasEventDataChanged()
	{
		return eventDataHasChanged;
	}

	public static void eventDataRecieved()
	{
		eventDataHasChanged = false;
	}

	public static boolean hasUserDataChanged()
	{
		return userDataHasChanged;
	}
}



















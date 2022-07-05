import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

public class App
{
	
	private static EventType[] eventTypes;
	private static UUID currentUserId;

	private static boolean hasInitalisedEventTypes = false;
	public static void initEventTypes() 
	{
		if(hasInitalisedEventTypes) return;
		
		HashSet<EventType> tempStorage = new HashSet<EventType>();
		
		tempStorage.add(new FootballEvent());
		eventTypes = new EventType[EventType.getOrdinalCount()];
		for(EventType e : tempStorage) 
		{
			if(eventTypes[e.ordinal] != null) return;//throw
			
			eventTypes[e.ordinal] = e;
		}
		hasInitalisedEventTypes = true;
	}
	
	public static EventType getEventTypeByOrdinal(int ordinal) 
	{
		return eventTypes[ordinal];
	}
	
	public static void generateRandomEvent() 
	{
		int index = (int)(Math.random()*eventTypes.length);
		EventDataStore event = eventTypes[index].generateEvent(Instant.now());
		DataBase.storeEvent(event);
	}

	public static UUID getCurrentUserId()
	{
		return currentUserId;
	}

	public static void setCurrentUser(UserDataStore user)
	{
		App.currentUserId = user.getId();
	}
	
	public static void setCurrentUser(UUID userId)
	{
		App.currentUserId = userId;
	}
	
}

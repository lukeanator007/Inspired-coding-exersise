import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class Main
{

	public static void main(String[] args)
	{
		
		App.initEventTypes();
		
		UUID userId = DataBase.createNewUser(new BigInteger("10000"), "debug");
		
		App.setCurrentUser(userId);
		
		Ui ui = new Ui();
//		Login.loginUser();

	}

	
	
	public void createEvent(EventType type, Instant startTime) 
	{
		EventDataStore data = type.generateEvent(startTime);
		DataBase.storeEvent(data);
	}
	
}

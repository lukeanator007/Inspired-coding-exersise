import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import javax.swing.*;

public class Ui
{
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	JButton generateEvent = new JButton();
	
	JComboBox<Market> marketPicker = new JComboBox<Market>();
	JComboBox<EventDataStore> eventPicker = new JComboBox<EventDataStore>();
	Timer updateTimer = new Timer(100, new UpdateTimerAction());
	JLabel timeLabel = new JLabel();
	JLabel eventTimeLabel = new JLabel();
	JLabel oddsLabel = new JLabel();
	JLabel balanceLabel = new JLabel();
	JPanel betsPanel = new JPanel();
	JScrollPane scrollPane;
	
	ArrayList<BetDataStoreUi> betList = new ArrayList<BetDataStoreUi>();
	
	public Ui() 
	{
		betsPanel.setLayout(new BoxLayout(betsPanel, BoxLayout.Y_AXIS));

		scrollPane = new JScrollPane(betsPanel); 
		scrollPane.setBounds(250, 300, 600, 570);
        panel.add(scrollPane);
        
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(panel);
		panel.setLayout(null);
		
		frame.setTitle("Inspired coding exersise");
		
		generateEvent.setText("generate event");
		generateEvent.addActionListener(new GenerateEventAction());
		generateEvent.setBounds(10, 20, 150, 25);
		panel.add(generateEvent);
		
		JLabel marketPickerLabel = new JLabel("Bet on:");
		marketPickerLabel.setBounds(250, 50, 75, 25);
		panel.add(marketPickerLabel);
		
		marketPicker.setBounds(330, 50, 250, 25);
		marketPicker.addActionListener(new MarketPickerAction());
		panel.add(marketPicker);

		JLabel eventPickerLabel = new JLabel("Select event:");
		eventPickerLabel.setBounds(250, 20, 75, 25);
		panel.add(eventPickerLabel);

		JButton placeBetButton = new JButton("Place bet");
		placeBetButton.setBounds(250, 90, 150, 25);
		placeBetButton.addActionListener(new PlaceBetAction());
		panel.add(placeBetButton);
		
		JButton searchAllBetsButton = new JButton("Search all bets");
		searchAllBetsButton.setBounds(250, 235, 175, 25);
		searchAllBetsButton.addActionListener(new SearchAllBetsAction());
		panel.add(searchAllBetsButton);
		
		JButton searchCollectableBetsButton = new JButton("Search collectable bets");
		searchCollectableBetsButton.setBounds(250, 270, 175, 25);
		searchCollectableBetsButton.addActionListener(new SearchCollectableBetsAction());
		panel.add(searchCollectableBetsButton);
		
		JLabel placeBetAtLabel = new JLabel("at:");
		placeBetAtLabel.setBounds(410, 90, 15, 25);
		panel.add(placeBetAtLabel);

		oddsLabel.setBounds(430, 90, 75, 25);
		panel.add(oddsLabel);
		
		balanceLabel.setBounds(20, 800, 150, 25);
		panel.add(balanceLabel);
		
		eventPicker.addActionListener(new EventPickerAction());
		eventPicker.setBounds(330, 20, 250, 25);
		panel.add(eventPicker);
		
		eventTimeLabel.setBounds(585, 20, 250, 25);
		panel.add(eventTimeLabel);
		
		
		timeLabel.setBounds(900, 900, 75, 25);
		panel.add(timeLabel);
		
		updateUserData(true);
		frame.setVisible(true);
		updateTimer.start();
	}
	
	public void updateEventPicker() 
	{
		if(!DataBase.hasEventDataChanged()) return;
		
		DataBase.eventDataRecieved();
		
		EventDataStore current = (EventDataStore)eventPicker.getSelectedItem();
		eventPicker.removeAllItems();
		
		HashSet<EventDataStore> data = DataBase.getCurrentEvents();
		
		for(EventDataStore event : data) 
		{
			eventPicker.addItem(event);
		}
		
		if(data.contains(current)) eventPicker.setSelectedItem(current);
	}
	
	private void updateTime()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:m:s").withZone(ZoneId.systemDefault());
		timeLabel.setText(formatter.format(Instant.now()));
		EventDataStore event = (EventDataStore)this.eventPicker.getSelectedItem();
		if(event == null)eventTimeLabel.setText("");
		else 
		{
			DateTimeFormatter eventFormatter = DateTimeFormatter.ofPattern("m:s").withZone(ZoneId.systemDefault());
			eventTimeLabel.setText(eventFormatter.format(event.closeTime.minusMillis(Instant.now().toEpochMilli())));
		}
	}
	
	private void updateUserData(boolean force)
	{
		if(!force && !DataBase.hasUserDataChanged()) return;
		
		UserDataStore user = DataBase.getUser(App.getCurrentUserId());
		this.balanceLabel.setText("Balance: " + user.getBalance().toString() + "p");
		DataBase.userDataRecieved();
	}
	
	private void searchBets(boolean all, boolean collectable) 
	{
		HashSet<BetDataStore> bets;
		try
		{
			bets = DataBase.searchBets(all, collectable, App.getCurrentUserId());
		} catch (MessageException e)
		{
			JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		this.betList.clear();
		this.betsPanel.removeAll();
		this.panel.revalidate();
		this.panel.repaint();
		this.scrollPane.revalidate();
		this.scrollPane.repaint();
		
		int i = 0;
		for(BetDataStore bet : bets) 
		{
			try
			{
				betList.add(new BetDataStoreUi(bet));
				betsPanel.add(betList.get(i).generateBetPanel());
				i++;
			} catch (MessageException e)
			{
				JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		
	}
	
	private class UpdateTimerAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			updateTime();
			updateEventPicker();
			updateUserData(false);
			Instant now = Instant.now();
			for(BetDataStoreUi data : betList) 
			{
				data.update(now);
			}
			DataBase.checkEventTimer();
		}

	}
	
	private class GenerateEventAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			App.generateRandomEvent();
		}
	}
	
	private class EventPickerAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			eventChanged((EventDataStore)eventPicker.getSelectedItem());
		}
	}
	
	private class SearchAllBetsAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			searchBets(true, false);
		}
	}
	
	private class SearchCollectableBetsAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			searchBets(false, true);
		}
	}

	private class MarketPickerAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			marketChanged((Market)marketPicker.getSelectedItem());
		}
	}
	
	private class PlaceBetAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(App.getCurrentUserId() == null)
			{
				JOptionPane.showMessageDialog(frame, "please log in before placing a bet");
				return;
			}
			
			EventDataStore event = (EventDataStore) eventPicker.getSelectedItem();
			if(event == null) 
			{
				JOptionPane.showMessageDialog(frame, "Please select an event");
				return;
			}
			Market market = (Market) marketPicker.getSelectedItem();
			if(market == null) 
			{
				JOptionPane.showMessageDialog(frame, "Please select a market");
				return;
			}
			
			BigInteger wager;
			while(true) 
			{
				String input = JOptionPane.showInputDialog("enter wager (pence)");
				if(input == null) return;
				
				try 
				{
					wager = new BigInteger(input);
				}
				catch(NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(frame, "wager must be an integer");
					continue;
				}
				
				if(wager.compareTo(BigInteger.ZERO)!= 1) 
				{
					JOptionPane.showMessageDialog(frame, "must wager at least 1p");
					continue;
				}
				UserDataStore userData = DataBase.getUser(App.getCurrentUserId());
				
				try
				{
					userData.placeBet(wager, event.getId(), market);
					break;
				} catch (MessageException e1)
				{
					JOptionPane.showMessageDialog(frame, e1.getMessage());
					continue;
				}
				
			}
		}
	}
	
	public void eventChanged(EventDataStore event) 
	{
		if(event == null) return;
		
		Market[] newMarkets = event.getMarkets();
		marketPicker.removeAllItems();
		
		for(int i=0; i<newMarkets.length;i++) marketPicker.addItem(newMarkets[i]);
		
	}
	
	public void marketChanged(Market market) 
	{
		EventDataStore event = (EventDataStore)this.eventPicker.getSelectedItem();
		if(event == null || market == null) return;
		double odds;
		try
		{
			odds = App.getEventTypeByOrdinal(event.typeOrdinal).getOdds(event.participants, market);
		} catch (MessageException e)
		{
			JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		odds*=100;
		odds = Math.round(odds);
		odds/=100;
		oddsLabel.setText("1 : " + odds);
	}
	
	private class BetDataStoreUi
	{
		public final Instant rollRngTime;
		public final String name;
		public final UUID betId;

		private JButton collectButton = new JButton("Collect");
		JLabel statusLabel = new JLabel();	
		JLabel timerLabel = new JLabel();	
		
		BetDataStoreUi(BetDataStore data) throws MessageException
		{
			EventDataStore event = DataBase.getEventData(data.getEventId());
			this.rollRngTime = event.rollRngTime;
			this.name = "<html>"+event.name + "<br>"+data.getWager().toString()+"p on " + data.getMarket().displayString+"</html>";
			this.betId = data.getBetId();
		}
		
		public JPanel generateBetPanel() 
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(3,2));
			
			JLabel nameLabel = new JLabel(this.name);
			panel.add(nameLabel);
			
			panel.add(this.statusLabel);
			
			panel.add(this.timerLabel);
			
			this.collectButton.setEnabled(false);
			this.collectButton.addActionListener(new CollectButtonAction());
			panel.add(this.collectButton);
			
			return panel;
		}
		
		public void update(Instant now) 
		{
			if(now.isAfter(rollRngTime)) 
			{
				if(!DataBase.getBet(betId).hasPaidOut()) collectButton.setEnabled(true);
				else collectButton.setEnabled(false);
				this.timerLabel.setText("0:0");
			}
			else 
			{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("m:s").withZone(ZoneId.systemDefault());
				this.timerLabel.setText(formatter.format(this.rollRngTime.minusMillis(now.toEpochMilli())));
			}
			displayStatus(now);
		}
		
		private void displayStatus(Instant now)
		{
			if(DataBase.getBet(betId).hasPaidOut()) 
			{
				this.statusLabel.setText("already collected");
				return;
			}
			
			if(now.isBefore(this.rollRngTime)) this.statusLabel.setText("pending result");
			else this.statusLabel.setText("result ready");
			
		}
		
		private class CollectButtonAction implements ActionListener
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BetDataStore bet = DataBase.getBet(BetDataStoreUi.this.betId);
				UserDataStore user = DataBase.getUser(App.getCurrentUserId());
				
				try
				{
					BigInteger amount = bet.payout(user);
					if(amount.equals(BigInteger.ZERO)) JOptionPane.showMessageDialog(frame, "oh no, you lost!");
					else JOptionPane.showMessageDialog(frame, "you won " + amount.toString() + "p!");
					
				} catch (MessageException e1)
				{
					JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
		
	}
	
	
	
}




















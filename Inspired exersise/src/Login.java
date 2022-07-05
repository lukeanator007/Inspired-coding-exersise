import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login
{

	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	JTextField userNameTextField = new JTextField();
	JTextField balanceTextField = new JTextField();
	
	public static void loginUser() 
	{
		Login login = new Login();
		
	}
	
	private Login() 
	{
		frame.setSize(350, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(panel);
		panel.setLayout(null);

		JLabel userNameLabel = new JLabel("Username:");
		userNameLabel.setBounds(10, 20, 80, 25);
		panel.add(userNameLabel);

		JLabel BalanceLabel = new JLabel("Balance:");
		BalanceLabel.setBounds(10, 50, 80, 25);
		panel.add(BalanceLabel);
		
		JLabel pennceLabel = new JLabel("p");
		pennceLabel.setBounds(268, 50, 10, 25);
		panel.add(pennceLabel);
		
		userNameTextField.setBounds(100, 20, 165, 25);
		panel.add(userNameTextField);
		
		balanceTextField.setText("0");
		balanceTextField.setBounds(100, 51, 165, 25);
		panel.add(balanceTextField);
		
		JButton login = new JButton("login");
		login.setBounds(10, 100, 80, 25);
		login.addActionListener(new LoginListener());
		panel.add(login);
		
		frame.setVisible(true);
	}
	
	private class LoginListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			BigInteger balance;
			try 
			{
				balance = new BigInteger(balanceTextField.getText());
			}
			catch(NumberFormatException ex) 
			{
				JOptionPane.showMessageDialog(frame, "blance must be a number", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(balance.compareTo(BigInteger.ZERO) == -1) 
			{
				JOptionPane.showMessageDialog(frame, "blance must be positive", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String name = userNameTextField.getText();
			
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			
		}
		
	}
	
}































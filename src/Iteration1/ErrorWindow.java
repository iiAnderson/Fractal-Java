package Iteration1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The Class ErrorWindow, which is created when there is an error the user needs to know about, as a person using the display may not 
 * be able to see the console, so they need to be informed of the error using this
 */
public class ErrorWindow extends JFrame{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1229991483307015628L;

	/**
	 * Instantiates a new error window, creates all of the buttons and panels and adds them to the window
	 *
	 * @param s the String to be displayed by the error window
	 */
	public ErrorWindow(String s){
		setSize(450, 200);
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		JButton okButton = new JButton("OK");
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
			
		});
		
		JLabel label = new JLabel("ERROR:\n " + s);
		label.setFont(new Font("Arial", 0, 14));
		label.setHorizontalAlignment(SwingUtilities.CENTER);
		
		bottom.setPreferredSize(new Dimension(getWidth(), getHeight()/3));
		bottom.add(okButton);
		top.add(label);
		
		panel.add(label, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		setContentPane(panel);
		setVisible(true);
	}
	
	public ErrorWindow(){
		setSize(600, 200);
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		JButton okButton = new JButton("OK");
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
			
		});
		
		JLabel label = new JLabel("Julia Controls:");
		label.setFont(new Font("Arial", 0, 14));
		label.setHorizontalAlignment(SwingUtilities.CENTER);
		
		JLabel label1 = new JLabel("When Auto Julia is on, click on the panel to freeze it and click on it again to unfreeze");
		label1.setFont(new Font("Arial", 0, 14));
		label1.setHorizontalAlignment(SwingUtilities.CENTER);
		
		bottom.setPreferredSize(new Dimension(getWidth(), getHeight()/3));
		bottom.add(okButton);
		top.add(label);
		top.add(label1);
		
		panel.add(top, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		setContentPane(panel);
		setVisible(true);
	}
	
}

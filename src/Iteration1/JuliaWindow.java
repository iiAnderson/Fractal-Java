package Iteration1;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

/**
 * The Class JuliaWindow. This class Extends FractalWindow, so reduces the code that needs to be written in this class
 */
public class JuliaWindow extends FractalWindow {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7624597699425338068L;
	
	/**
	 * Instantiates a new julia window. Calling the super constructor for FractalWindow to build the window
	 *
	 * @param set the Fractal to draw
	 * @param c the Complex number
	 */
	public JuliaWindow(String set, Complex c) {
		super(set, c);
		setLocationRelativeTo(null);
		setSize(500, 500);
	}

	/**
	 * Creates the JuliaDisplay, attaches it to this class and then creates the mouseListener 
	 */
	public void fractalConstructor(Complex c){
		fractalPanel = new JuliaDisplay(c);
		FractalListener l = new FractalListener();
		fractalPanel.addMouseListener(l);
		fractalPanel.addMouseMotionListener(l);
	}
		
	/**
	 * When the window is closing the FractalWindow is notified by calling the juliaClosing method, so it can set the variable to null
	 * allowing a new window to be opened
	 */
	public void windowListener(){
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				FractalWindow.juliaClosing();
				dispose();
			}
		});
	}
	
	//These methods were in FractalWindow, but dont want to be added to JuliaWindow, so are left blank
	public void addMenuBar(JPanel panel){}
	
	public void getSavedComplexNumbers(){}
	
	public void addAutoJuliaToPanel(final JPanel bottom){}
	
	public void addJuliaControlsToPanel(JPanel b){}
	
	public class FractalListener implements MouseListener, MouseMotionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		//Sets the text to null if the mouse is off the screen
		@Override
		public void mouseExited(MouseEvent e) {
			complexLabel.setText("");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent e) {}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		//Sets the text according to the complex number the event was triggered at
		@Override
		public void mouseMoved(MouseEvent e) {
			complexLabel.setText("Currently at: " + getFractalPanel().getComplexNumber(e.getX(), e.getY()));
		}
	}
	
}

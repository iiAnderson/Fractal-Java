package Iteration1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/**
 * The Class FractalWindow.
 */
public class FractalWindow extends JFrame{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3953992444951878988L;
	
	/** The controlPanel. */
	private JPanel cp;
	
	/** The fractal panel. */
	protected FractalDisplay fractalPanel;
	
	/** The complex label. */
	protected JLabel complexLabel;
	
	/** The julia auto checkbox. */
	private static JCheckBox juliaAuto;
	
	/**  The julia display, stores the julia window currently tied to this class. */
	protected static JuliaWindow juliaDisplay;
	
	/**  The set clicked, to determine if the window has been clicked. */
	private boolean setClicked;
	
	/**  The saved julia sets, read from the file. */
	private ArrayList<Complex> savedJulia;
	
	/**  The current complex selected. */
	protected Complex currentComplex = null;
	
	/** The file to read in from. */
	private File f;
	
	/**  The bounds textFields, static so they can be updated with repaint in FractalDisplay. */
	protected static JTextField realMin, realMax, imagMin, imagMax;
	
	/** The menu panel. */
	private JPanel menuPanel;
	
	/** The menu bar. */
	private JMenuBar menuBar = null;
	
	/**
	 * Instantiates a new fractal window. Sets all of the characteristics for it and calls fractalConstructor to create a new FractalDisplay
	 * attach it to this class, and add the listeners to this window.
	 * It also initializes the GUI by calling init, which creates the components for it.
	 *
	 * @param set the set
	 * @param c the c
	 */
	public FractalWindow(String set, Complex c){
		super(set + " viewer");
		setClicked = false;
		JFrame window = this;
		window.setSize(800, 800);
		windowListener();
		
		fractalConstructor(c);
		getSavedComplexNumbers();
		
		init();
		window.setVisible(true);
	}
	
	/**
	 * Initializes the GUI for the window, creates the controlPanel and menuBar and adds them to the panels
	 */
	public void init(){
		JPanel backPanel = new JPanel();
		cp = new JPanel();
		
		menuPanel = new JPanel();
		
		backPanel.setLayout(new BorderLayout());
		createControlPanel();
		backPanel.add(cp, BorderLayout.SOUTH);
		addMenuBar(menuPanel);
		menuPanel.setOpaque(false);
		backPanel.add(fractalPanel, BorderLayout.CENTER);
		fractalPanel.add(menuPanel, BorderLayout.NORTH);
		
		setContentPane(backPanel);
	}
	
	/**
	 * Gets the saved complex numbers stored in the file, this is done by creating a new BufferedReader which reads in the lines from
	 * the file, and getComplexFromString is used to then change these to complex numbers and saved into the ArrayList.
	 * If there is an error, the errorWindow is Created
	 *
	 * @return the saved complex numbers
	 */
	public void getSavedComplexNumbers(){
		savedJulia = new ArrayList<Complex>();
		BufferedReader in;
		try {
			f = new File("SavedJuliaComplex.txt");
			in = new BufferedReader(new FileReader(f));
			String complex = in.readLine();
			while(complex != null){
				savedJulia.add(Complex.getComplexFromString(complex));
				complex = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			new ErrorWindow("The Complex numbers in favourites cannot be loaded");
		}
		
	}
	
	/**
	 * Fractal constructor, creates the display for the window, and attaches the listener to the display.
	 * (This method passes a complex, which is set to null for the mandelbrot set, but as JuliaWindow extends this one
	 * julia overwrites this class, null has to be passed. This saves on code, at the expense of some sense.
	 *
	 * @param c the Complex
	 */
	public void fractalConstructor(Complex c){
		fractalPanel = new FractalDisplay();
		fractalPanel.setLayout(new BorderLayout());
		FractalListener l = new FractalListener();
		fractalPanel.addMouseListener(l);
		fractalPanel.addMouseMotionListener(l);
	}
	
	/**
	 * Instantiates a new fractal window. This is the constructor called by the mandelbrot set, which passes null for the complex
	 * number. This allows this class to be extended and used for the julia window too
	 *
	 * @param set the set
	 */
	public FractalWindow(String set){
		this(set, null);
	}

	/**
	 * Window listener. This creates the windowListener, so that if the window is closing (ie someone closed it) then the complex 
	 * numbers in the array are written back to the file using the PrintWriter, throwing an errorwindow if there is a problem
	 */
	public void windowListener(){
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					PrintWriter out = new PrintWriter(f);
					for(Complex c: savedJulia){
						System.out.println(c);
						out.println(c);
					}
					out.close();
				} catch (IOException e1) {
					new ErrorWindow("The complex numbers cannot be saved in the file");
				}
				dispose();
			}
		});
	}

	/**
	 * Creates the control panel. This creates the control panel at the bottom, and sets the information about it. It then calls
	 * several methods to add the components to the JPanels. I used a BorderLayout as i only had three rows of information, with
	 * components with different heights, which would have not worked for grid layout
	 */
	public void createControlPanel(){
		cp.setPreferredSize(new Dimension(getWidth(), getHeight()/7));
		cp.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
		cp.setBorder(new TitledBorder("Fractal Control Panel"));
		cp.setLayout(new FlowLayout());
				
		JPanel centrePanel = new JPanel();
		JPanel centre = new JPanel();
		JPanel boundsCentre = new JPanel();
		
		centrePanel.setLayout(new BorderLayout());
		cp.add(centrePanel, BorderLayout.CENTER);
		
		addIterationsToPanel(centre);
		centrePanel.add(centre, BorderLayout.NORTH);
		
		addBoundsControl(boundsCentre);
		centrePanel.add(boundsCentre, BorderLayout.CENTER);
		
		complexLabel = new JLabel("");
		complexLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centrePanel.add(complexLabel, BorderLayout.SOUTH);
		
	}
	
	/**
	 * Adds the iterations control to panel. Passed the JPanel that the control will be added to
	 *
	 * @param bottomCentre the JPanel to add it to
	 */
	public void addIterationsToPanel(JPanel bottomCentre){
		final JTextField text = new JTextField(5);
		text.setText(getFractalPanel().getIterations()+"");
		addAutoJuliaToPanel(bottomCentre);
		bottomCentre.add(new JLabel("Iterations: "));
		bottomCentre.add(text);
		text.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				getFractalPanel().setIterations(Integer.parseInt(text.getText()));
				validate();
				repaint();
			}
		});
	}

	
	/**
	 * Adds the menu bar to the Panel. Passed the JPanel that the control will be added to
	 *
	 * @param panel the JPanel
	 */
	public void addMenuBar(JPanel panel){
		menuBar = new JMenuBar();
		//Creating the header Menus
		JMenu fractal = new JMenu("Fractal Control");
		fractal.setPreferredSize(new Dimension(100, fractal.getPreferredSize().height));
		JMenu view = new JMenu("View");
		view.setPreferredSize(new Dimension(100, view.getPreferredSize().height));
		JMenu help = new JMenu("Help");
		help.setPreferredSize(new Dimension(100, help.getPreferredSize().height));
		
		//Creating the sub menu and menuItems
		JMenuItem animatedZoom = new JMenuItem("Animate Zoom");
		animatedZoom.setPreferredSize(new Dimension(150, animatedZoom.getPreferredSize().height));
				
		JMenu zoom = new JMenu("Zoom Frames");
		zoom.setPreferredSize(new Dimension(150, zoom.getPreferredSize().height));
		JMenuItem zoom10 = new JMenuItem("10");
		zoom10.setPreferredSize(new Dimension(150, zoom10.getPreferredSize().height));
		JMenuItem zoom25 = new JMenuItem("25");
		zoom25.setPreferredSize(new Dimension(150, zoom25.getPreferredSize().height));
		JMenuItem zoom50 = new JMenuItem("50");
		zoom50.setPreferredSize(new Dimension(150, zoom50.getPreferredSize().height));
		//Attaches the listener to set the zoom iterations depending on which item was pressed
		ActionListener l = (new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem item = (JMenuItem) e.getSource();
				getFractalPanel().setZoomIterations(Integer.parseInt(item.getText()));
			}
		});
		//Adding the actionListener
		zoom10.addActionListener(l);
		zoom25.addActionListener(l);
		zoom50.addActionListener(l);
		
		//Adding the animatedZoom listener to set the zoom levels to 1, so there is no zoom animation
		animatedZoom.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getFractalPanel().setZoomIterations(1);
			}
		});
		
		zoom.add(zoom10);
		zoom.add(zoom25);
		zoom.add(zoom50);
		
		//Reset the bounds of the display
		JMenuItem resetBounds = new JMenuItem("Reset Bounds");
		resetBounds.setPreferredSize(new Dimension(150, resetBounds.getPreferredSize().height));
		resetBounds.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getFractalPanel().realMin = -1.6;
				getFractalPanel().realMax = 1.6;
				getFractalPanel().imagMin = -2.0;
				getFractalPanel().imagMax = 2.0;
				validate();
				repaint();
			}
		});
		
		//Changing the fractal used
		JMenu mainFractal = new JMenu("Main Fractal");
		mainFractal.setPreferredSize(new Dimension(100, mainFractal.getPreferredSize().height));
		JMenuItem burningShip = new JMenuItem("Burning Ship");
		burningShip.setPreferredSize(new Dimension(150, burningShip.getPreferredSize().height));
		JMenuItem mandlebrot = new JMenuItem("Mandlebrot");
		mandlebrot.setPreferredSize(new Dimension(150, mandlebrot.getPreferredSize().height));
		JMenuItem tricorn = new JMenuItem("Tricorn");
		tricorn.setPreferredSize(new Dimension(150, tricorn.getPreferredSize().height));
		
		//Setting the new fractalPanel in the fractalPanel class. item.getText() was used so only one listener needs to be attached
		ActionListener setListener = (new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem item = (JMenuItem) e.getSource();
				getFractalPanel().setSet(item.getText());
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						repaint();
					}
				});
			}
		});
		
		//Adding the actionListener
		burningShip.addActionListener(setListener);
		mandlebrot.addActionListener(setListener);
		tricorn.addActionListener(setListener);

		//Adding the fractal chooser to the mainFractal sub-menu and adding that submenu to the menu fractal
		mainFractal.add(burningShip);
		mainFractal.add(mandlebrot);
		mainFractal.add(tricorn);
		fractal.add(mainFractal);
			
		//Adding the items to the view menu
		view.add(animatedZoom);
		view.add(zoom);
		view.add(resetBounds);

		//Adding the main menus to the menubar
		subFractalMenu(fractal);
		menuBar.add(view);
		menuBar.add(fractal);
		panel.add(menuBar);
	}
	
	/**
	 * Sub fractal menu. Creates the submenus for loading/removing/exporting julia sets. This is in a new method as there is alot of code in here
	 * so uncomplicates it from the rest of the code i have.
	 *
	 * @param fractal the JMenu to add it to
	 */
	private void subFractalMenu(final JMenu fractal){
		//Creating the sub-menus for loading/removing/exporting
		final JMenu loadSub = new JMenu("Load sub-Fractal");
		loadSub.setPreferredSize(new Dimension(150, loadSub.getPreferredSize().height));
		final JMenu removeSub = new JMenu("Remove sub-Fractal");
		removeSub.setPreferredSize(new Dimension(150, removeSub.getPreferredSize().height));
		final JMenu exportSub = new JMenu("Export sub-Fractal");
		exportSub.setPreferredSize(new Dimension(150, exportSub.getPreferredSize().height));
		
		//Attaching an actionlistener to save the sub-fractal
		JMenuItem saveSub = new JMenuItem("Save sub-Fractal");
		saveSub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if the complex is already in the array, or the current complex is null an error is returned
				if(savedJulia.contains(currentComplex) || currentComplex == null){
					new ErrorWindow("This Complex is either null, or is already stored");
					return;
				}
				savedJulia.add(currentComplex);
				//updates the sub-menus created above to display the new changes to the array. This is done without repainting to increase
				//efficiency
				updateSubFractalLists(loadSub, removeSub, exportSub);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						menuBar.repaint();
					}
				});
			}
		});
		saveSub.setPreferredSize(new Dimension(150, saveSub.getPreferredSize().height));
		
		updateSubFractalLists(loadSub, removeSub, exportSub);
		
		//Adding these sub-menus to the main fractal menu
		fractal.add(saveSub);
		fractal.add(loadSub);
		fractal.add(removeSub);
		fractal.add(exportSub);
	}
	
	/**
	 * Update sub fractal lists. This will delete the items in the sub-menus and recreate them according to the changes made in the arraylist
	 *
	 * @param loadSub the JMenu for loading julia sets
	 * @param removeSub the JMenu for removing julia sets
	 * @param exportSub the JMenu for exporting julia sets
	 */
	public void updateSubFractalLists(final JMenu loadSub, final JMenu removeSub, final JMenu exportSub){
		//Removing all of the items attached to the menus
		loadSub.removeAll();
		removeSub.removeAll();
		exportSub.removeAll();
		//Iterating over the complex values stored
		for(Complex c: savedJulia){
			final Complex a = c;
			//Adding this complex as a item to the menu with the string representation of the complex number
			JMenuItem savedSub = new JMenuItem(a + "");
			//Attaching the actionlistener to open the julia window with the specified complex number
			savedSub.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new JuliaWindow("julia", a);
				}
			});
			savedSub.setPreferredSize(new Dimension(150, savedSub.getPreferredSize().height));
			loadSub.add(savedSub);
			
			//Adding this complex as a item to the menu with the string representation of the complex number
			JMenuItem removedSub = new JMenuItem(c + "");
			//Adding the actionListener to remove the complex selected from the array and recall this method to update the lists
			removedSub.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					savedJulia.remove(a);
					updateSubFractalLists(loadSub, removeSub, exportSub);
				}
			});
			removedSub.setPreferredSize(new Dimension(150, removedSub.getPreferredSize().height));
			removeSub.add(removedSub);	
			
			//Adding this complex as a item to the menu with the string representation of the complex number
			final JMenuItem exportedSub = new JMenuItem(a + "");
			//Exporting the image, which creates a new julia window and gets the image displayed in that window. it then saves it 
			//with the string of the complex number and as a .png
			//If there is an error then a new errorwindow is created
			exportedSub.addActionListener(new ActionListener() {
				JuliaWindow display;
				BufferedImage disp;
				@Override
				public void actionPerformed(ActionEvent e) {

					display = new JuliaWindow("Julia", Complex.getComplexFromString(exportedSub.getText()));

					try{
						disp = display.getFractalPanel().getFractal();
						System.out.println(disp);
						ImageIO.write(disp, "png", new File(exportedSub.getText()+".png"));
					} catch(Exception e1){
						e1.printStackTrace();
						new ErrorWindow("There is no Sub-Fractal window open, please open one and try again" + "\n" + e1);
					}
				}
			});
			exportedSub.setPreferredSize(new Dimension(150, exportedSub.getPreferredSize().height));
			exportSub.add(exportedSub);
		}
	}

	/**
	 * Adds the bounds control to the Control panel, and adds the actionListener. It also creates labels next to the JTextField to
	 * identify which field is which.
	 *
	 * @param panel the panel
	 */
	public void addBoundsControl(JPanel panel){
		realMin = new JTextField(3);
		addActionListenerToBounds(realMin, "realMin");
		imagMin = new JTextField(3);
		addActionListenerToBounds(imagMin, "imagMin");
		imagMax = new JTextField(3);
		addActionListenerToBounds(imagMax, "imagMax");
		realMax = new JTextField(3);
		addActionListenerToBounds(realMax, "realMax");
		
		fractalPanel.updateBounds();
				
		panel.add(new JLabel("Real Min: " ));
		panel.add(realMin);
		panel.add(new JLabel("Real Max: " ));
		panel.add(realMax);	
		
		panel.add(new JLabel("Imag Min: " ));
		panel.add(imagMin);
		panel.add(new JLabel("Imag Max: " ));
		panel.add(imagMax);	
		
		JButton but = new JButton("Reset Bounds");
		but.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getFractalPanel().realMin = -1.6;
				getFractalPanel().realMax = 1.6;
				getFractalPanel().imagMin = -2.0;
				getFractalPanel().imagMax = 2.0;
				validate();
				repaint();
			}
		});
		
		panel.add(but);
	}
	
	/**
	 * Adds the action listener to bounds. This was done to reduce the code written in the actionListener.
	 *
	 * @param field the field
	 * @param str the name of the textField
	 */
	public void addActionListenerToBounds(final JTextField field, final String str){
		field.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//parsing the double from the string
				String s = field.getText();
				Double i = null;
				try{
					i = Double.parseDouble(s);
				} catch (NumberFormatException exce){
					//Throwing an error if it isnt a vaild number
					new ErrorWindow("Please Enter a valid number");
				}
				switch(str){
				case "realMin":
					getFractalPanel().realMin = i;
					break;
				case "realMax":
					getFractalPanel().realMax = i;
					break;
				case "imagMin":
					getFractalPanel().imagMin = i;
					break;
				case "imagMax":
					getFractalPanel().imagMax = i;
					break;
				}
				//repainting
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						repaint();
					}
				});
			}
		});
	}
	
	/**
	 * Adds the auto julia control to panel.
	 *
	 * @param bottomCentre the bottom centre
	 */
	public void addAutoJuliaToPanel(JPanel bottomCentre){
		juliaAuto = new JCheckBox("Auto Generate Julia: ");
		bottomCentre.add(juliaAuto);
	}
		
	/**
	 * Gets the control panel.
	 *
	 * @return the control panel
	 */
	public JPanel getControlPanel() {
		return cp;
	}

	/**
	 * Gets the fractal panel.
	 *
	 * @return the fractal panel
	 */
	public FractalDisplay getFractalPanel() {
		return fractalPanel;
	}

	/**
	 * Gets the julia display.
	 *
	 * @return the julia display
	 */
	public FractalWindow getJuliaDisplay() {
		return juliaDisplay;
	}
	
	/**
	 * Gets the current complex.
	 *
	 * @return the current complex
	 */
	public Complex getCurrentComplex() {
		return currentComplex;
	}
	
	/**
	 * Julia closing. This is called by the juliaWindow when it is closing. This resets the juliaDisplay variable in this class
	 */
	public static void juliaClosing(){
		juliaDisplay = null;
		juliaAuto.setSelected(false);
	}

	/**
	 * This listener deals with the generating of the julia set and showing the userSelectedPoint
	 * It is in the class as it deals with the creation of the julia window, which is stored in this class, so couldn't be accessed
	 * from the FractalDisplay class.
	 */
	public class FractalListener implements MouseListener, MouseMotionListener {

		/**
		 * If the mouse is clicked and there is no julia set currently in JuliaDisplay, then a new JuliaWindow is created, otherwise the
		 * old window is updated with the new userSelectedPoint
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if(juliaDisplay == null){
				juliaDisplay = new JuliaWindow("julia", getFractalPanel().getComplexNumber(e.getX(), e.getY()));
			} else {
				setClicked = !setClicked;
			}
			currentComplex = getFractalPanel().getComplexNumber(e.getX(), e.getY());
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {}

		/**
		 * Sets the label to display the userSelectedPoint to nothing
		 */
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

		/**
		 * If AutoJulia is on then this will update the julia window with the new complex number to draw. It is also possible to
		 * pause the automation of the julia set by clicking on it, which allows the image to the be saved/export/zoomed in on.
		 * This also updates the label for the userSelectedPoint
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			complexLabel.setText("Currently at: " + getFractalPanel().getComplexNumber(e.getX(), e.getY()));
			if(juliaAuto != null && juliaAuto.isSelected()){
				if(juliaDisplay == null){
					juliaDisplay = new JuliaWindow("julia", getFractalPanel().getComplexNumber(e.getX(), e.getY()));
				} else if(!setClicked) {
					((JuliaDisplay) getJuliaDisplay().getFractalPanel()).setJuliaStatic(getFractalPanel().getComplexNumber(e.getX(), e.getY()));
					getJuliaDisplay().validate();
					getJuliaDisplay().repaint();
				}
			}
		}
	}

}

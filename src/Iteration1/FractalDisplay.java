package Iteration1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * The Class FractalDisplay.
 */
public class FractalDisplay extends JPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**The number of threads in the image threading. */
	private int threadNo;
	
	/** The iterations. */
	private int iterations = 100;
	
	/** The bounds of the window in the complex plane */
	protected Double realMax = 1.6, realMin = -1.6, imagMax = 1.8, imagMin = -1.8;
		
	/** The rectangle coords, used for storing the coordinates of the two rectangle points, to aid in zooming calcuations */
	private Integer[][] rectangleCoords;
	
	/** The Main fractal currently being displayed */
	private String set = "Mandlebrot";
	
	/** The fractal image to be rendered */
	protected BufferedImage fractal;
	
	/** The image threads. */
	protected ArrayList<Thread> imageThreads;
	
	/** The zoom iterations (The number of updates while zooming)  */
	private int zoomIterations = 25;

	/** The Rectangle specifications. */
	int x, y, width, height;
	
	/**
	 * Instantiates a new fractal display, attaches the mouse and motion listener to the window
	 */
	public FractalDisplay(){
		threadNo = 16;
		rectangleCoords = new Integer[2][2];
		ZoomListener l = new ZoomListener();
		addMouseListener(l);
		addMouseMotionListener(l);
	}

	/**
	 * Builds the fractal. This method uses two for loops to run through every pixel inside the parameters passed to it.
	 * It then calls getIterationsForDivergence() on a new complex number according to the pixel coordinates it was passed
	 * and sets the colour of the pixel in fractal (image) accordingly.
	 * This will then paint a strip (corresponding to the thread) onto the image, which will then later be painted to the canvas
	 *
	 * @param yLim the yLim of this thread
	 * @param y the y coordinate of the starting point of the thread
	 */
	public void buildFractal(int yLim, int y){
		for(int i = 0; i < getWidth(); i++){
			for(int j = y; j < yLim; j++){
				int it = getIterationsForDivergence(getComplexNumber(i, j));
				if(it == getIterations()){
					fractal.setRGB(i, j, Color.BLACK.getRGB());
				} else {
					int pixelColor = Color.HSBtoRGB((float) it/ getIterations(), 1, 1);
					fractal.setRGB(i, j,pixelColor);
				}
			}
		}
	}

	/**
	 * Paint component. This method controls all of the painting of the panel. It makes a call to its superclass to make sure that it is cleared
	 * before it is repainted again. It then runs the threading code to create the threads and start drawing the image in the buffer, before it's
	 * drawn to the screen. This does make the method slow, and as i had trouble moving this code out of the method i have just minimised my usage
	 * of repaint as much as possible, calling repaint only on the individual components when the need to be repainted.
	 * Once all of the threads have finished and the image has been drawn, the highlighting rectangle is drawn and the bounds are updated in the
	 * textfields.
	 *
	 * @param g the Graphics
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		imageThreads = new ArrayList<Thread>();
		Graphics2D g2 = (Graphics2D) g;
		fractal = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i = 1; i <= threadNo; i++){
			threadingDrawing((getHeight()/threadNo)*i, (getHeight()/threadNo)*(i-1));
		}
		g2.drawImage(fractal, 0, 0, null);
		drawHightlightedRectangle(g2);
		updateBounds();
		updateFractalBounds();
	}

	/**
	 * This method threads the drawing of the image by creating new threads which call the buildFractal method, which will build the image
	 * The paintComponent method calculates the y and yLim of the threads strip, passing it to buildFractal which will then make it.
	 * It then enters a while loop to make sure all of the threads have finished before continuing, or the image would not paint properly
	 *
	 * @param yLim the yLim
	 * @param y the y (Minimum)
	 */
	public void threadingDrawing(final int yLim, final int y){
		Thread t;
			t = new Thread(){
				@Override
				public void run() {
					buildFractal(yLim, y);
				}
			};
			imageThreads.add(t);
			t.start();
			
		boolean doneDrawing = false;
		while(!doneDrawing){
			int closedThread = 0;
			for(Thread thr: imageThreads){
				if(!thr.isAlive()){
					closedThread++;
				}
				if(closedThread == imageThreads.size()){
					doneDrawing = true;
					return;
				}
			}
		}
	}

	/**
	 * Updates the bounds for the textfields by calling repaint on them through the EDT, allowing swingutilities to run it when it can.
	 */
	public void updateFractalBounds(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				FractalWindow.realMin.repaint();
				FractalWindow.imagMin.repaint();

				FractalWindow.realMax.repaint();
				FractalWindow.imagMax.repaint();
			}
		});

	}

	/**
	 * Update bounds of the textfields so they display the right format, the numbers are rounded so they fit sensibly on the screen
	 */
	public void updateBounds(){
		DecimalFormat df = new DecimalFormat("#.#");
		FractalWindow.realMin.setText(df.format(realMin) + "");
		FractalWindow.imagMin.setText(df.format(imagMin) + "");	

		FractalWindow.realMax.setText(df.format(realMax) + "");
		FractalWindow.imagMax.setText(df.format(imagMax) + "");	
	}

	/**
	 * Draw hightlighted rectangle. It is passed the graphics object for this current instance, and calculates the position for the rectangle
	 * by working out which coordinate is larger, then working out the width/height depending on which coordinate is larger.
	 * This is also threaded to speed it up, as it is a very parallel process.
	 *
	 * @param g2 the Graphics2D
	 */
	public void drawHightlightedRectangle(Graphics2D g2){
		if(rectangleCoords[0][0] != null && rectangleCoords[1][0] != null){
			Thread xCalc = new Thread(){			
				public void run(){
					if(rectangleCoords[0][0] < rectangleCoords[1][0]){
						x = rectangleCoords[0][0];
						width = rectangleCoords[1][0] - rectangleCoords[0][0];
					} else {
						x = rectangleCoords[1][0];
						width = rectangleCoords[0][0] - rectangleCoords[1][0];
					}
				}
			};

			Thread yCalc = new Thread(){
				public void run(){

					if(rectangleCoords[0][1] < rectangleCoords[1][1]){
						y = rectangleCoords[0][1];
						height = rectangleCoords[1][1] - rectangleCoords[0][1];
					} else {
						y = rectangleCoords[1][1];
						height = rectangleCoords[0][1] - rectangleCoords[1][1];
					}
				}
			};

			yCalc.start();
			xCalc.start();

			while(true){
				if(!xCalc.isAlive() && !yCalc.isAlive()){
					g2.drawRect(x, y, Math.abs(width),  Math.abs(height));
					x = y = width = height = 0;
					break;
				}
			}
		}
	}


	/**
	 * Gets the iterations for the complex number passed. The main fractal is changed according to the string set, which can be changed when
	 * a button is pressed (in an actionlistener) and to add new fractal all that is needed is to add a case in this code with the formula.
	 *
	 * @param c the Complex number representation of the pixel-+
	 * @return the iterations
	 */
	public int getIterationsForDivergence(Complex c){
		switch(getSet()){
		case "Mandlebrot":
			Complex z = new Complex(c.getReal(), c.getImaginary());
			int inc = 0;
			while(inc < getIterations() && z.modulusSquared() < 4){
				z.square();
				z.add(c);
				inc++;
			}
			return inc;
		case "Burning Ship":
			Complex v = new Complex(0.0 , 0.0);
			int incre = 0;
			while(incre < getIterations() && v.modulusSquared() < 4){
				v.modulusPart();
				v.square();
				v.add(c);
				incre++;
			}
			return incre;
		case "Tricorn":
			Complex b = new Complex(c.getReal(), c.getImaginary());
			int incr = 0;
			while(incr < getIterations() && b.modulusSquared() < 4){
				b.conjugate();
				b.square();
				b.add(c);
				incr++;
			}
			return incr;
		}
		return 0;
	}

	/**
	 * Gets the complex representation of the pixel passed to it. This is done by calcuating its distance from the minimum bound, then
	 * multiplying the total distance and dividing it by the width. This gives the complex representation of the pixel
	 *
	 * @param x the x
	 * @param y the y
	 * @return the complex number representation
	 */
	public Complex getComplexNumber(int x, int y){	
		return new Complex(realMin + x * (realMax - realMin) / getWidth(), imagMin + y * (imagMax - imagMin) / getHeight());	
	}

	/**
	 * Gets the iterations.
	 *
	 * @return the iterations
	 */
	public int getIterations(){
		return iterations;
	}

	/**
	 * Sets the iterations.
	 *
	 * @param it the new iterations
	 */
	public void setIterations(int it){
		this.iterations = it;
	}

	/**
	 * Gets the zoom iterations.
	 *
	 * @return the zoom iterations
	 */
	public int getZoomIterations() {
		return zoomIterations;
	}
	
	/**
	 * Sets the zoom iterations.
	 *
	 * @param i the new zoom iterations
	 */
	public void setZoomIterations(int i) {
		zoomIterations = i;
	}

	/**
	 * Zoom calculations. This is where the bounds are changed for the zoom. This is done by working out the complex represetation
	 * of the pixels that are passed. Then the coordinates are checked to see which is larger, and depending on that the distance
	 * from the edge of the screen is calculated. So then it is divided by the zoomIterations so it can incrementally zoom in using
	 * the timer actionlistener. Once it reaches the zoomIterations the timer stops itself and the method ends
	 */
	public void zoomCalculations(){
		final Double r1, r2, i1, i2, rnDif, rxDif, ixDif, inDif;
		r1 = (realMin + rectangleCoords[0][0] * (realMax - realMin) / getWidth());
		r2 = (realMin + rectangleCoords[1][0] * (realMax - realMin) / getWidth());
		i1 = (imagMin + rectangleCoords[0][1] * (imagMax - imagMin) / getHeight());
		i2 = (imagMin + rectangleCoords[1][1] * (imagMax - imagMin) / getHeight());

		if(r1 < r2) {
			rnDif = (r1 - realMin)/zoomIterations;
			rxDif = (realMax - r2)/zoomIterations;
		} else {
			rnDif = (r2 - realMin)/zoomIterations;
			rxDif = (realMax - r1)/zoomIterations;
		}

		if(i1 < i2) {
			inDif = (i1 - imagMin)/zoomIterations;
			ixDif = (imagMax - i2)/zoomIterations;
		} else {
			inDif = (i2 - imagMin)/zoomIterations;
			ixDif = (imagMax - i1)/zoomIterations;
		}

		Timer timer = new Timer(20, new ActionListener() {
			int k = 0;
			@Override
			public void actionPerformed(ActionEvent ae) {
				realMin += rnDif;
				realMax -= rxDif;
				imagMin += inDif;
				imagMax -= ixDif;

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						repaint();
					}
				});
				k++;
				if(k == zoomIterations){
					((Timer)ae.getSource()).stop();
				}
			}
		});
		timer.start();
	}

	/**
	 * Gets the sets the.
	 *
	 * @return the sets the
	 */
	public String getSet() {
		return set;
	}

	/**
	 * Sets the sets the.
	 *
	 * @param set the new sets the
	 */
	public void setSet(String set) {
		this.set = set;
	}

	/**
	 * Gets the fractal image and returns it so the image can later be saved
	 *
	 * @return the fractal image
	 */
	public BufferedImage getFractal() {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < getWidth(); i++){
			for(int j = 0; j < getHeight(); j++){
				int it = getIterationsForDivergence(getComplexNumber(i, j));
				if(it == getIterations()){
					image.setRGB(i, j, Color.BLACK.getRGB());
				} else {
					int pixelColor = Color.HSBtoRGB((float) it/ getIterations(), 1, 1);
					image.setRGB(i, j,pixelColor);
				}
			}
		}
		return image;
	}

	/**
	 * This Listener is attached to the panel and deals with all of the mouse coordinate setting.
	 */
	public class ZoomListener implements MouseMotionListener, MouseListener {
		
		/** 
		 * If the mouse is dragged then the second set of coordinates stored in the array are reset to the position of this event
		 * And then SwingUtilities is used to repaint and update the rectangle.
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			rectangleCoords[1][0] = e.getX();
			rectangleCoords[1][1] = e.getY();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaint();
				}
			});
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved(MouseEvent e) {}

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
		@Override
		public void mouseExited(MouseEvent e) {}

		/** 
		 * The initial point for the rectangle, this sets the first elements coordinate to the position of this event
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			rectangleCoords[0][0] =  e.getX();
			rectangleCoords[0][1] =  e.getY();
		}

		/** 
		 * This method checks to see if the distance dragged was greater than 20, if it was then zoomCalcutions is called.
		 * This was done to make it easier for users to click to get the juliaPanel, as otherwise there is some issues with the mouse
		 * moving slightly and zooming in all the way.
		 * It then sets the second set of coordinates in the array to the position of this event, calls the zoom method and sets the
		 * original coordinate to null, so it can be checked later on to see if the rectangle drawing has ended
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if(Math.abs(rectangleCoords[0][0] - e.getX()) > 20 && Math.abs(rectangleCoords[0][1] - e.getY()) > 20){
				rectangleCoords[1][0] = e.getX();
				rectangleCoords[1][1] = e.getY();
				zoomCalculations();
			}
			rectangleCoords[0][0] = null;
			rectangleCoords[1][0] = null;
		}

	}
}

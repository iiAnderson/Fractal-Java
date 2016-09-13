package Iteration1;


// TODO: Auto-generated Javadoc
/**
 * The Class JuliaDisplay. This extends FractalDisplay, which means that none of the paintComponent etc needs to be in this class, the 
 * method that calcuates the set can be overwritten and that is all that's needed. This minimizes the code that needs to be written
 */
public class JuliaDisplay extends FractalDisplay {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2092639536558358220L;
	
	/** The julia static, the Complex number used in the julia calcuations and the complex representation of the pixel on the main Fractal */
	private Complex juliaStatic;

	
	/**
	 * Instantiates a new julia display and calls the superConstructor
	 *
	 * @param c the c
	 */
	public JuliaDisplay(Complex c){
		super();
		setJuliaStatic(c);
	}

	/**
	 * The calculate method of the julia Set
	 */
	public int getIterationsForDivergence(Complex c){
		Complex z = c;
		int inc = 0;
		while(inc < getIterations() && z.modulusSquared() < 4){
			z.square();
			z.add(juliaStatic);
			inc++;
		}
		return inc;
	}

	/**
	 * Gets the julia static.
	 *
	 * @return the julia static
	 */
	public Complex getJuliaStatic() {
		return juliaStatic;
	}

	/**
	 * Sets the julia static.
	 *
	 * @param juliaStatic the new julia static
	 */
	public void setJuliaStatic(Complex juliaStatic) {
		this.juliaStatic = juliaStatic;
	}
}

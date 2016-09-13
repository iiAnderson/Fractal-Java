package Iteration1;

import java.text.DecimalFormat;

/**
 * The Complex class which represents a complex number, with real and imaginary parts.
 */
public class Complex {

	
	/** The real part to the Complex number. */
	private Double real;
	
	/** The imaginary part to the Complex number. */
	private Double imaginary;
	
	/**
	 * Instantiates a new complex.
	 *
	 * @param real To set to the new real
	 * @param imaginary To set to the new imaginary
	 */
	public Complex(Double real, Double imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	
	/**
	 * Gets the real.
	 *
	 * @return the real
	 */
	public Double getReal(){
		return this.real;
	}
	
	/**
	 * Gets the imaginary.
	 *
	 * @return the imaginary
	 */
	public Double getImaginary(){
		return this.imaginary;
	}
	
	/**
	 * Adds the complex number passed to this instance of a complex number. This is done by adding the real and imaginary parts separatley
	 * and saving back into real and imaginary.
	 *
	 * @param complex the Complex number to be added
	 */
	public void add(Complex complex){
		this.real = this.getReal() + complex.getReal();
		this.imaginary = this.getImaginary() + complex.getImaginary();
	}

	/**
	 * Modulus squared.
	 *
	 * @return Double Which is the modulus Squared of the complex number
	 */
	public double modulusSquared(){
		 return this.getReal()*this.getReal() + this.getImaginary()*this.getImaginary();
	}

	/**
	 * Square of this instance of a complex number, with the real part being a^2 - b^2, and the imaginary 2 * a * b.
	 */
	public void square(){
		Double r = this.getReal(), i = this.getImaginary();
		real = ((r*r) - (i*i));
		imaginary = 2 * r * i;
	}

	/**
	 * Finds the modulus of each part of the complex number
	 */
	public void modulusPart(){
		real = Math.abs(this.getReal());
		imaginary = Math.abs(this.getImaginary());
	}
	
	/**
	 * Returns the string value of this object, so it is easier to print it out later on
	 */
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.000");
		return new String(df.format(real) + " + " + df.format(imaginary) + "i");
	}

	/**
	 * Finds the conjugate value of this complex number
	 */
	public void conjugate(){
		this.imaginary = -getImaginary();
	}
	
	/**
	 * Gets the complex from string.
	 *
	 * @param s the String with the complex number
	 * @return the complex from string
	 */
	public static Complex getComplexFromString(String s){
		String[] split = s.split("\\+");
		String[] secondSplit = split[1].split("i");
		return new Complex(Double.parseDouble(split[0]), Double.parseDouble(secondSplit[0]));
	}

}

package MonteCarlo;

import java.util.LinkedList;
import java.util.Random;

public class EuropeanOptionPrice 
{
	private LinkedList<Double> payoffs = new LinkedList<Double>();
	public double value;
	
	public LinkedList<Double> getPayoffs() 
	{
		return payoffs;
	}

	public void setPayoffs(LinkedList<Double> payoffs) 
	{
		this.payoffs = payoffs;
	}
	
	public static double[] generateBatch(int Size)
	{
		double[] uniformNumbers = new double[Size];
		
		Random R = new Random();
		
		for(int i=0; i<Size; i++)
		{
			uniformNumbers[i] = R.nextDouble();
			if((uniformNumbers[i] == 0.0)||(uniformNumbers[i] == 1.0))
			{
				uniformNumbers[i] = 0.5;
			}
		}
		return uniformNumbers;
	}
	
	// Calculating approximate inverse of a Gaussian distribution
	public static double invGaussian(double probab)
	{
		double probability = 1-probab;
		double t = Math.sqrt(Math.log(1/(probability*probability)));
		double c0 = 2.515517; double c1 = 0.802853; double c2 = 0.010328;
		double d1 = 1.432788; double d2 = 0.189269; double d3 = 0.001308;
		double xp = t- ((c0+(c1*t)+(c2*t*t))/(1+(d1*t)+(d2*t*t)+(d3*t*t*t)));
		return xp;
	}
	
	public EuropeanOptionPrice(String Type, double s0, double mu, double sigma, double strike, int Maturity)
	{
		boolean stopFlag = false;
		int batchSize = 50000;
		
		while(!stopFlag)
		{
			// Generate 2 batches of Uniform Random Variables to do the Box-Muller Transformation
			double[] uniformRV1 = generateBatch(batchSize);
			double[] uniformRV2 = generateBatch(batchSize);
			
			// Box-Muller Transformation
			BoxMullerInGPU myBM = new BoxMullerInGPU(uniformRV1, uniformRV2);
			
			// Normal Random Vectors generated
			LinkedList<Double> normalRV1 = myBM.getNormalRandomVariable1();
			LinkedList<Double> normalRV2 = myBM.getNormalRandomVariable2();
			
			// Using Normal Random Vectors generated to value European Options
			EuropeanOptionPayOutInGPU myOption = new EuropeanOptionPayOutInGPU(Type, s0, mu, sigma, strike, Maturity,normalRV1);
			for(int i=0; i< batchSize; i++)
			{
				this.payoffs.add(myOption.getPayOut().removeFirst());
			}
			
			myOption = new EuropeanOptionPayOutInGPU(Type, s0, mu, sigma, strike, Maturity,normalRV2);
			for(int i=0; i< batchSize; i++)
			{
				this.payoffs.add(myOption.getPayOut().removeFirst());
			}
			
			// Calculating the mean value
			this.value =0.0;
			for(int i=0; i< this.payoffs.size(); i++)
			{
				this.value = this.value + this.payoffs.get(i);
			}
			this.value = (this.value)/(this.payoffs.size());
			
			// calculating the std. dev
			double sdev =0.0;
			for(int i=0; i< this.payoffs.size(); i++)
			{
				sdev = sdev+(this.payoffs.get(i) - this.value)*(this.payoffs.get(i) - this.value);
			}
			sdev = sdev/(this.payoffs.size());
			sdev = Math.sqrt(sdev);
			
			// Stopping criteria
			double tol = 0.1;
			double accuracy = 0.96;
			
			double k = invGaussian(accuracy);
			
			if((2*k*sdev) <= (tol*Math.sqrt(this.payoffs.size())))
			{
				stopFlag = true;
				System.out.println("Stops");
			}
			else
			{
				stopFlag = false;
				System.out.println("Does not stop yet");
				System.out.println("Mu: "+ this.value +" Std. Dev: " + sdev);
				System.out.println("Length of confidence Interval: " +(2*k*sdev) + " stop if: " + (tol*Math.sqrt(this.payoffs.size())));
			}
			
			//stopFlag = true;
		}
	}
	
	public static void main(String[] args)
	{
		EuropeanOptionPrice Option = new EuropeanOptionPrice("Call", 152.35, 0.0001, 0.01, 165, 252);
		System.out.println("The value of Option is: " + Option.value);
	}
}

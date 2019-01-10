package SimulationFramework;

import java.util.Random;

public class NormalRandomVectorGenerator implements RandomVectorGenerator
{
	// Since we have to generate potential stock paths for next 252 days, each of the random vectors, needs to have a length of 252.
	public int vectorlength = 252;
	
	private Random[] rand = new Random[vectorlength];
	private double[] randvector = new double[vectorlength]; 
	
	public NormalRandomVectorGenerator()
	{
		for(int i=0; i< this.vectorlength; i++)
		{
			this.rand[i] = new Random();
			this.randvector[i] = this.rand[i].nextGaussian();
		}
	}
	
	@Override
	public double[] getVector() 
	{
		// TODO Auto-generated method stub
		return this.randvector;
	}
	
	/*
	public static void main(String[] args)
	{
		NormalRandomVectorGenerator nv = new NormalRandomVectorGenerator();
		double[] randomvector = nv.getVector();
		for(int i=0; i<randomvector.length; i++)
		{
			System.out.println("This is the " + (i+1) +"th value " + randomvector[i]);
		}
	}
	*/
}

package SimulationFramework;

public class StatsCollector 
{
	// Variable N is defined to know if during the process of evaluating multiple options, has some option expired before maturity
	// For example, if you instantiate with value 1, any time an option in consideration expires, you can set N to 0 via valuation methods
	// Thus we have warning that in StatsCollector, that one or more of the options you choose to evaluate have expired.
	// Note since Asian and European options are not such options, we have not used N in their valuation. 
	private int N;
	
	public StatsCollector(int Check)
	{
		this.N= Check;
	}
	
	public double invGaussian(double probability)
	{
		double t = Math.sqrt(Math.log(1/(probability*probability)));
		double c0 = 2.515517; double c1 = 0.802853; double c2 = 0.010328;
		double d1 = 1.432788; double d2 = 0.189269; double d3 = 0.001308;
		double xp = t- ((c0+(c1*t)+(c2*t*t))/(1+(d1*t)+(d2*t*t)+(d3*t*t*t)));
		return xp;
	}
	
	// This function values European Call over given tolerance and accuracy of a Monte Carlo Simulation
	public double EuropeanCallValuation(double tolerance, double accuracy, double s0, double mu, double sigma, double r, double K)
	{
		int flyingSize= 100000;
		double flyingSigma;
		double flyingPayoffSimulations[] = new double[flyingSize];
		double OptionValue;
		int NumberOfSimulations =0;
		int flag = 0;
		double meanValue = 0.0;
		EuropeanCallPayOut v = new EuropeanCallPayOut(r,K);
		for(int i=0; i<(flyingSize/2); i++)
		{
			GBMStockPath p = new GBMStockPath(s0, mu, sigma);
			meanValue = meanValue + v.getPayout(p);
			flyingPayoffSimulations[flag] = v.getPayout(p);
			//System.out.println(v.getPayout(p));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			//Using Path built from negation of Random Variables
			TransformedStockPath q = p.getAntiPath();
			meanValue = meanValue + v.getPayout(q);
			flyingPayoffSimulations[flag] = v.getPayout(q);
			//System.out.println(v.getPayout(q));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			NumberOfSimulations = NumberOfSimulations+2;
		}
		meanValue = meanValue/NumberOfSimulations;
		flyingSigma = 0.0;
		for(int j=0; j<flyingSize; j++)
		{
			flyingSigma = (meanValue-flyingPayoffSimulations[j])*(meanValue-flyingPayoffSimulations[j]); 
		}
		flyingSigma = Math.sqrt(flyingSigma/flyingSize);
		//
		//System.out.println(flyingSigma);
		//
		double sumOfValues = meanValue*NumberOfSimulations;
		// Note we have to pass 1-accuracy in the invGaussian function as mentioned in the article
		// https://www.johndcook.com/blog/normal_cdf_inverse/
		double invGaussianPDF = invGaussian(1-accuracy);
		double approxN = flyingSize*((invGaussianPDF*flyingSigma)/(tolerance))*((invGaussianPDF*flyingSigma)/(tolerance));
		// Rounding it to nearest integer
		int n = (int) (approxN + 0.5);
		//
		//System.out.println(approxN);
		//
		if(n < flyingSize)
		{
			OptionValue = meanValue;
		}
		else
		{
			for(int i=flyingSize; i<(n/2); i++)
			{
				GBMStockPath p = new GBMStockPath(s0, mu, sigma);
				sumOfValues = sumOfValues + v.getPayout(p);
				//Using Path built from negation of Random Variables
				StockPath q = p.getAntiPath();
				sumOfValues = sumOfValues + v.getPayout(q);
				NumberOfSimulations = NumberOfSimulations+2;
			}
			meanValue = (sumOfValues/NumberOfSimulations);
			OptionValue = meanValue;
		}
		return (OptionValue);
	}
	
	// This function values European Put over given tolerance and accuracy of a Monte Carlo Simulation
	public double EuropeanPutValuation(double tolerance, double accuracy, double s0, double mu, double sigma, double r, double K)
	{
		int flyingSize= 100000;
		double flyingSigma;
		double flyingPayoffSimulations[] = new double[flyingSize];
		double OptionValue;
		int NumberOfSimulations =0;
		int flag = 0;
		double meanValue = 0.0;
		EuropeanPutPayOut v = new EuropeanPutPayOut(r,K);
		for(int i=0; i<(flyingSize/2); i++)
		{
			GBMStockPath p = new GBMStockPath(s0, mu, sigma);
			meanValue = meanValue + v.getPayout(p);
			flyingPayoffSimulations[flag] = v.getPayout(p);
			//System.out.println(v.getPayout(p));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			//Using Path built from negation of Random Variables
			TransformedStockPath q = p.getAntiPath();
			meanValue = meanValue + v.getPayout(q);
			flyingPayoffSimulations[flag] = v.getPayout(q);
			//System.out.println(v.getPayout(q));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			NumberOfSimulations = NumberOfSimulations+2;
		}
		meanValue = meanValue/NumberOfSimulations;
		flyingSigma = 0.0;
		for(int j=0; j<flyingSize; j++)
		{
			flyingSigma = (meanValue-flyingPayoffSimulations[j])*(meanValue-flyingPayoffSimulations[j]); 
		}
		flyingSigma = Math.sqrt(flyingSigma/flyingSize);
		//
		//System.out.println(flyingSigma);
		//
		double sumOfValues = meanValue*NumberOfSimulations;
		// Note we have to pass 1-accuracy in the invGaussian function as mentioned in the article
		// https://www.johndcook.com/blog/normal_cdf_inverse/
		double invGaussianPDF = invGaussian(1-accuracy);
		double approxN = flyingSize*((invGaussianPDF*flyingSigma)/(tolerance))*((invGaussianPDF*flyingSigma)/(tolerance));
		// Rounding it to nearest integer
		int n = (int) (approxN + 0.5);
		//
		//System.out.println(approxN);
		//
		if(n < flyingSize)
		{
			OptionValue = meanValue;
		}
		else
		{
			for(int i=flyingSize; i<(n/2); i++)
			{
				GBMStockPath p = new GBMStockPath(s0, mu, sigma);
				sumOfValues = sumOfValues + v.getPayout(p);
				//Using Path built from negation of Random Variables
				StockPath q = p.getAntiPath();
				sumOfValues = sumOfValues + v.getPayout(q);
				NumberOfSimulations = NumberOfSimulations+2;
			}
			meanValue = (sumOfValues/NumberOfSimulations);
			OptionValue = meanValue;
		}
		return (OptionValue);
	}
	
	// This function values Asian Call over given tolerance and accuracy of a Monte Carlo Simulation
	public double AsianCallValuation(double tolerance, double accuracy, double s0, double mu, double sigma, double r, double K)
	{
		int flyingSize= 100000;
		double flyingSigma;
		double flyingPayoffSimulations[] = new double[flyingSize];
		double OptionValue;
		int NumberOfSimulations =0;
		int flag = 0;
		double meanValue = 0.0;
		AsianCallPayOut v = new AsianCallPayOut(r,K);
		for(int i=0; i<(flyingSize/2); i++)
		{
			GBMStockPath p = new GBMStockPath(s0, mu, sigma);
			meanValue = meanValue + v.getPayout(p);
			flyingPayoffSimulations[flag] = v.getPayout(p);
			//System.out.println(v.getPayout(p));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			//Using Path built from negation of Random Variables
			TransformedStockPath q = p.getAntiPath();
			meanValue = meanValue + v.getPayout(q);
			flyingPayoffSimulations[flag] = v.getPayout(q);
			//System.out.println(v.getPayout(q));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			NumberOfSimulations = NumberOfSimulations+2;
		}
		meanValue = meanValue/NumberOfSimulations;
		flyingSigma = 0.0;
		for(int j=0; j<flyingSize; j++)
		{
			flyingSigma = (meanValue-flyingPayoffSimulations[j])*(meanValue-flyingPayoffSimulations[j]); 
		}
		flyingSigma = Math.sqrt(flyingSigma/flyingSize);
		//
		//System.out.println(flyingSigma);
		//
		double sumOfValues = meanValue*NumberOfSimulations;
		// Note we have to pass 1-accuracy in the invGaussian function as mentioned in the article
		// https://www.johndcook.com/blog/normal_cdf_inverse/
		double invGaussianPDF = invGaussian(1-accuracy);
		double approxN = flyingSize*((invGaussianPDF*flyingSigma)/(tolerance))*((invGaussianPDF*flyingSigma)/(tolerance));
		// Rounding it to nearest integer
		int n = (int) (approxN + 0.5);
		//
		//System.out.println(approxN);
		//
		if(n < flyingSize)
		{
			OptionValue = meanValue;
		}
		else
		{
			for(int i=flyingSize; i<(n/2); i++)
			{
				GBMStockPath p = new GBMStockPath(s0, mu, sigma);
				sumOfValues = sumOfValues + v.getPayout(p);
				//Using Path built from negation of Random Variables
				StockPath q = p.getAntiPath();
				sumOfValues = sumOfValues + v.getPayout(q);
				NumberOfSimulations = NumberOfSimulations+2;
			}
			meanValue = (sumOfValues/NumberOfSimulations);
			OptionValue = meanValue;
		}
		return (OptionValue);
	}

	// This function values Asian Call over given tolerance and accuracy of a Monte Carlo Simulation
	public double AsianPutValuation(double tolerance, double accuracy, double s0, double mu, double sigma, double r, double K)
	{
		int flyingSize= 100000;
		double flyingSigma;
		double flyingPayoffSimulations[] = new double[flyingSize];
		double OptionValue;
		int NumberOfSimulations =0;
		int flag = 0;
		double meanValue = 0.0;
		AsianPutPayOut v = new AsianPutPayOut(r,K);
		for(int i=0; i<(flyingSize/2); i++)
		{
			GBMStockPath p = new GBMStockPath(s0, mu, sigma);
			meanValue = meanValue + v.getPayout(p);
			flyingPayoffSimulations[flag] = v.getPayout(p);
			//System.out.println(v.getPayout(p));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			//Using Path built from negation of Random Variables
			TransformedStockPath q = p.getAntiPath();
			meanValue = meanValue + v.getPayout(q);
			flyingPayoffSimulations[flag] = v.getPayout(q);
			//System.out.println(v.getPayout(q));
			//System.out.println(flag+ "   " +meanValue);
			flag++;
			NumberOfSimulations = NumberOfSimulations+2;
		}
		meanValue = meanValue/NumberOfSimulations;
		flyingSigma = 0.0;
		for(int j=0; j<flyingSize; j++)
		{
			flyingSigma = (meanValue-flyingPayoffSimulations[j])*(meanValue-flyingPayoffSimulations[j]); 
		}
		flyingSigma = Math.sqrt(flyingSigma/flyingSize);
		//
		//System.out.println(flyingSigma);
		//
		double sumOfValues = meanValue*NumberOfSimulations;
		// Note we have to pass 1-accuracy in the invGaussian function as mentioned in the article
		// https://www.johndcook.com/blog/normal_cdf_inverse/
		double invGaussianPDF = invGaussian(1-accuracy);
		double approxN = flyingSize*((invGaussianPDF*flyingSigma)/(tolerance))*((invGaussianPDF*flyingSigma)/(tolerance));
		// Rounding it to nearest integer
		int n = (int) (approxN + 0.5);
		//
		//System.out.println(approxN);
		//
		if(n < flyingSize)
		{
			OptionValue = meanValue;
		}
		else
		{
			for(int i=flyingSize; i<(n/2); i++)
			{
				GBMStockPath p = new GBMStockPath(s0, mu, sigma);
				sumOfValues = sumOfValues + v.getPayout(p);
				//Using Path built from negation of Random Variables
				StockPath q = p.getAntiPath();
				sumOfValues = sumOfValues + v.getPayout(q);
				NumberOfSimulations = NumberOfSimulations+2;
			}
			meanValue = (sumOfValues/NumberOfSimulations);
			OptionValue = meanValue;
		}
		return (OptionValue);
	}
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		StatsCollector S = new StatsCollector(1);
		double Europeancallval = S.EuropeanCallValuation(0.1, 0.04, 152.35, 0.0001, 0.01, 0.0001, 165.0);
		System.out.println("European Call Value: " +Europeancallval);
		//double Europeanputval = S.EuropeanPutValuation(0.1, 0.04, 152.35, 0.0001, 0.01, 0.0001, 165.0);
		//System.out.println("European put Value: " +Europeanputval);
		double Asiancallval = S.AsianCallValuation(0.1, 0.04, 152.35, 0.0001, 0.01, 0.0001, 164.0);
		System.out.println("Asian Call Value: " +Asiancallval);
		//double Asianputval = S.AsianPutValuation(0.1, 0.04, 152.35, 0.0001, 0.01, 0.0001, 164.0);
		//System.out.println("Asian Put Value: " +Asianputval);
		int n = S.N;
		System.out.println("Check: "+n);
	}

}

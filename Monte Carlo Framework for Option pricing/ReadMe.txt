This is the read me file for Monte Carlo method for option pricing. (Madhur Bhattad)

This read me file has the section of implementation, answers, concept and code design as mentioned below. 
Implementation section shows the general way to execute project.
Answers section show the specific cases of evaluation. 
Concept and Code design section shows the flow of the project. 
 
1.) Implementation:
To implement any valuation, you first declare a StatsCollector, in the main method of StatsCollector.

StatsCollector S = new StatsCollector(1);

Now to evaluate any given option price you need the following:
double tolerance, double accuracy, double s0, double mu, double sigma, double r, double K
This represents tolerance, accuracy required, initial underlying price, expected return, volatility, risk free rate and strike price respectively.

The pricing of European call, European put, Asian Call, and Asian put are done as follows:
double eCall = S.EuropeanCallValuation(tolerance, accuracy, s0, mu, sigma, r, K);  
double ePut = S.EuropeanPutValuation(tolerance, accuracy, s0, mu, sigma, r, K); 
double aCall = S.AsianCallValuation(tolerance, accuracy, s0, mu, sigma, r, K); 
double aPut = S.AsianPutValuation(tolerance, accuracy, s0, mu, sigma, r, K); 

2.) Answers:
The input in main method of the code:
		StatsCollector S = new StatsCollector(1);
		double Europeancallval = S.EuropeanCallValuation(0.1, 0.04, 152.35, 0.0001, 0.01, 0.0001, 165.0);
		System.out.println("European Call Value: " +Europeancallval);
		double Asiancallval = S.AsianCallValuation(0.1, 0.04, 152.35, 0.0001, 0.01, 0.0001, 164.0);
		System.out.println("Asian Call Value: " +Asiancallval);
		int n = S.N;
		System.out.println("Check: "+n);
The Output:
                European Call Value: 6.263497396327377
                Asian Call Value: 2.190164788628818
                Check: 1

  a.) The price of the mentioned European Call is $6.26 
  b.) The price of the mentioned Asian Call is $2.19

3.) Conecept and code design description: 
For the purpose of option pricing, one of the most prominent techniques is to use Monte Carlo simulations.
In this method, we simulate multiple stock paths by assuming some distribution of the underlying assets. These paths are then used for option pricing.
Thus we need a class to implement stock paths which inturn uses a class to generate random variables for each time step. We also need a class to find payout for a particular stock path.
Since we can abstractize these three classes depending on different ways we might want to use them, we have the following interfaces:
  a.) Interface RandomVectorGenerator: It consists of a method that returns an double array of random numbers
      This interface is used by the class NormalRandomVectorGenerator. It return normal random vectors.
      We restrict the size of normal random vector to 252, corresponding to size of approximate trading days in a year. This can be abstractized further depending on use.   
      Note generation of random vectors can be expensive. Thus it is useful to have a class that returns transformations of a random vector that have stabalizing properties.
      Thus we implement a class ScalingShiftingDecorator that implements this interface. It takes a random vector and scales it following by shifting it by determined factors.
      For example when we use a normal random vector for geometric Brownian motion stock path generation, we can use it's element negation for generating another path.
      (This is the concept of Anti Thetic Decorator Pattern)
  b.) Interface StockPath: It consists of a list of pair of integer and double. Integer part represents the k-th trading day and double represents the stock price (ex: close) that day.
      This interface is used by the class GBMStockPath. It generates a geometric Brownian Motion stock path. To instantiate it uses inital stock price, expected returns and volatility.
      We also use this interface in the class TransformedStockPath. The purpose of this class is simple to type cast a list of pairs of int and double to StockPath.
  c.) Interface PayOut: It consists of a method to return pay out from a given stock path.
      This interface is implemented by classes EuropeanCallPayOut, EuropeanPutPayOut, AsianCallPayOut and AsianPutPayOut which do the respective pay out calculation for a stock path.
Now that we have pay-outs for a particular option over a given stock path, all we need to do is to generate multiple stock paths and implement some stopping criteria for simulation. 
We first implement 100,000 paths to get an estimate of standard deviation say flyingSigma. Now we use the inverse of standard Gaussian distribution, to get number of simulations required.
The number of simulation depend on the required tolerance level which has to be given as an input. 
To do all this, we have a class StatsCollector. To instantiate it we give it some int input. This class contains the main method. 
The significance of this input is to see certain checking criteria while dealing with exotic options.  
(One such criteria can be that the call option is worthless if underlying goes above some value.)     
Since none of the Asian or European option are such options, this is not used while calculating its payoff.    
 
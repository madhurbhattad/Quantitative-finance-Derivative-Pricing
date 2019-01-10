package SimulationFramework;

import junit.framework.TestCase;

//import static org.junit.Assert.*;
//import org.junit.Test;

public class test_AsianPutPayOut extends TestCase
{

	//@Test
	//public void test() {
		//fail("Not yet implemented");
	//}

	// Tests obvious case when volatility, return of the underlying and risk free rate is zero, if initial underlying price equals strike.
	// In this case payoff should equal zero.
	public void testAsianPutPayOut0()
	{
		double r = 0.0;
		double strike = 100.0;
		AsianPutPayOut P = new AsianPutPayOut(r, strike);
		double mu = 0.0; double sigma = 0.0; double s0= 100.0;
		GBMStockPath gbm = new GBMStockPath(s0, mu, sigma);
		double tol = 0.0001;
		assertTrue(Math.abs((P.getPayout(gbm)-0.0))<tol);
	}
	
	// Tests obvious case when volatility, return of the underlying and risk free rate is zero, if initial underlying price < strike.
	// In this case payoff should equal difference in strike and underlying price.
	public void testAsianPutPayOut1()
	{
		double r = 0.0;
		double strike = 110.0;
		AsianPutPayOut P = new AsianPutPayOut(r, strike);
		double mu = 0.0; double sigma = 0.0; double s0= 100.0;
		GBMStockPath gbm = new GBMStockPath(s0, mu, sigma);
		double tol = 0.0001;
		assertTrue(Math.abs((P.getPayout(gbm)-10.0))<tol);
	}
	
	// Tests obvious case when volatility, return of the underlying and risk free rate is zero, if initial underlying price > strike.
	// In this case payoff should equal zero.
	public void testAsianCallPayOut2()
	{
		double r = 0.0;
		double strike = 90.0;
		AsianPutPayOut P = new AsianPutPayOut(r, strike);
		double mu = 0.0; double sigma = 0.0; double s0= 100.0;
		GBMStockPath gbm = new GBMStockPath(s0, mu, sigma);
		double tol = 0.0001;
	    assertTrue(Math.abs((P.getPayout(gbm)-0.0))<tol);
	}
	
}

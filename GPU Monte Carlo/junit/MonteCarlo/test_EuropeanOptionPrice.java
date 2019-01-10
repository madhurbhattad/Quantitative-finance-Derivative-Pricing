package MonteCarlo;

import junit.framework.TestCase;

public class test_EuropeanOptionPrice extends TestCase 
{
	//Trivial case we take a deep out of money call option
	public void test1()
	{
		EuropeanOptionPrice Option = new EuropeanOptionPrice("Call", 100.0, 0.0, 0.01, 120, 5);
		double value = Option.value;
		double maxTol = 0.1;
		assertTrue(value < maxTol);
	}
	
	//Trivial case we take a deep out of money put option
	public void test2()
	{
		EuropeanOptionPrice Option = new EuropeanOptionPrice("Put", 100.0, 0.01, 0.01, 50.0, 5);
		double value = Option.value;
		double maxTol = 0.1;
		assertTrue(value < maxTol);
	}
	
	//Put-Call Parity test
	public void test3()
	{
		EuropeanOptionPrice PutOption = new EuropeanOptionPrice("Put", 100.0, 0.01, 0.01, 50.0, 3);
		double putValue = PutOption.value;
		
		EuropeanOptionPrice CallOption = new EuropeanOptionPrice("Call", 100.0, 0.01, 0.01, 50.0, 3);
		double callValue = CallOption.value;
		
		// Put-call parity is given as follows:
		// Call price - put price = stock price - discounted value of strike price
		double LeftHandSide = callValue - putValue;
		double RightHandSide = (100.0 - 50.0);
		
		double value = LeftHandSide-RightHandSide;
		
		double maxTol = 0.1;
		assertTrue(value < maxTol);
	}
}

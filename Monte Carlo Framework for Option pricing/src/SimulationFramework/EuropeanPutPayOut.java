package SimulationFramework;

public class EuropeanPutPayOut implements PayOut
{
    //private double s0, mu, sigma;
    private double r, K;
	
	public EuropeanPutPayOut(double _r, double _K)
	{
		//this.s0 = _s0;
		//this.mu = _mu;
		//this.sigma = _sigma;
		this.r = _r;
		this.K = _K;
	}
	
	//GBMStockPath path = new GBMStockPath(s0, mu, sigma);
	
	// The pay off of an European put option is strike minus maximum of final stock price and 0
	
	@Override
	public double getPayout(StockPath path) 
	{
		// TODO Auto-generated method stub
		int SizeOfSimulation = path.getPrices().size();
		double sT = path.getPrices().get((SizeOfSimulation-1)).getSecondObject(); 
		double payoffAtT ; 
		if((sT-K)>0)
		{
			payoffAtT = 0;
		}
		else
		{
			payoffAtT = K-sT;
		}
		double discountedPayoff = payoffAtT * (Math.exp(-1*(this.r)*SizeOfSimulation));
		return discountedPayoff;
	}
	
	/*
	public static void main(String[] args)
	{
		GBMStockPath p = new GBMStockPath(152.35, 0.0001, 0.01);
		EuropeanPutPayOut payout = new EuropeanPutPayOut(0.0001, 165);
		for(int i=0; i< p.getPrices().size(); i++)
		{
			//System.out.println("Int: " + p.getPrices().get(i).getFirstObject());
			//System.out.println("Price: " + p.getPrices().get(i).getSecondObject());
		}
		double payoff = payout.getPayout(p);
		System.out.println(payoff);
	}
	*/
}


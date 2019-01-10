package SimulationFramework;

public class AsianPutPayOut implements PayOut
{
	//private double s0, mu, sigma;
	private double r,K;
	
	// We are considering an Asian call with a fixed strike.
	public AsianPutPayOut(double _r, double _K)
	{
		//this.s0 = _s0;
		//this.mu = _mu;
		//this.sigma = _sigma;
		this.r = _r;
		this.K = _K;
	}
	
	//GBMStockPath path = new GBMStockPath(s0, mu, sigma);
	
	// The pay off of an Asian put option is strike minus maximum of average stock price and 0 
	
	@Override
	public double getPayout(StockPath path) 
	{
		// TODO Auto-generated method stub
		int SizeOfSimulation = path.getPrices().size();
		//double sT = path.getPrices().get((SizeOfSimulation-1)).getSecondObject(); 
		double savg = 0;
		for(int i = 0; i< SizeOfSimulation; i++)
		{
			savg = savg + path.getPrices().get(i).getSecondObject(); 
		}
		savg= savg/SizeOfSimulation;
		double payoffAtT ; 
		if((savg-K)>0)
		{
			payoffAtT = 0;
		}
		else
		{
			payoffAtT = K-savg;
		}
		// We need to discount the profit we get in future to the current value.
		// Generally we use risk free rate to discount
		double discountedPayoff = payoffAtT * (Math.exp(-1*(this.r)*SizeOfSimulation));
		return discountedPayoff;
	}
	
	/*
	public static void main(String[] args)
	{
		GBMStockPath p = new GBMStockPath(152.35, 0.0001, 0.01);
		AsianPutPayOut payout = new AsianPutPayOut(0.0001, 164);
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

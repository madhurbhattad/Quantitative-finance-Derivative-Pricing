package SimulationFramework;

import java.util.List;

public class TransformedStockPath implements StockPath
{
	
	private List<Pair<Integer, Double>> TransformedPath;
	
	public TransformedStockPath(List<Pair<Integer, Double>> _TransformedPath)
	{
		this.TransformedPath = _TransformedPath;
	}

	@Override
	public List<Pair<Integer, Double>> getPrices() 
	{
		// TODO Auto-generated method stub
		return TransformedPath;
	}

}

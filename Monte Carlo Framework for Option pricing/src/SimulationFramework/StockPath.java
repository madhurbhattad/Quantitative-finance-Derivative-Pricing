package SimulationFramework;

import java.util.List;

public interface StockPath 
{
	// For any stock path we generate we want to hold an integer position indicating the i-th trading day and a double to hold prices
	List<Pair<Integer, Double>> getPrices();
}

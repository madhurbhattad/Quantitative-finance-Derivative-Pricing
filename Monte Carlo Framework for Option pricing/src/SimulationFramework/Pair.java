package SimulationFramework;

public class Pair<T1, T2> 
{
	private T1 o1;
	private T2 o2;
	
	public Pair(T1 _o1, T2 _o2)
	{
		o1 = _o1;
		o2 = _o2;
	}
	
	public T1 getFirstObject()
	{
		return o1;
	}
	
	public T2 getSecondObject()
	{
		return o2;
	}
}

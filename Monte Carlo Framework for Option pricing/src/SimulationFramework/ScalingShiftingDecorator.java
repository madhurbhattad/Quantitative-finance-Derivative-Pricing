package SimulationFramework;

public class ScalingShiftingDecorator implements RandomVectorGenerator
{
    private double shift, scale;
    private RandomVectorGenerator inner;
    
    public ScalingShiftingDecorator(RandomVectorGenerator inner, double shift, double scale)
    {
        this.shift = shift;
        this.scale = scale;
        this.inner = inner;
    }

	@Override
	public double[] getVector() 
	{
		// TODO Auto-generated method stub
		double[] ScaledShiftedVector = new double[inner.getVector().length];
		for(int i=0; i< inner.getVector().length; i++)
		{
			ScaledShiftedVector[i] = scale*inner.getVector()[i] + shift;
		}
		return ScaledShiftedVector;
	}
}

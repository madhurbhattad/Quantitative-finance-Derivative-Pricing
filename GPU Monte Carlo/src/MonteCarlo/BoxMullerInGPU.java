package MonteCarlo;

import org.jocl.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static org.jocl.CL.*;

import java.util.LinkedList;
//import java.util.Random;

// This class takes in input of a couple of arrays of uniform random variable between 0 and 1
// It returns a couple of linked lists of standard Gaussian random variable with size as the minimum of the two arrays

public class BoxMullerInGPU 
{
	private LinkedList<Double> normalRandomVariable1 = new LinkedList<Double>();
	private LinkedList<Double> normalRandomVariable2 = new LinkedList<Double>();
	
	public LinkedList<Double> getNormalRandomVariable1() 
	{
		return normalRandomVariable1;
	}

	public void setNormalRandomVariable1(LinkedList<Double> normalRandomVariable) 
	{
		this.normalRandomVariable1 = normalRandomVariable;
	}
	
	public LinkedList<Double> getNormalRandomVariable2() 
	{
		return normalRandomVariable2;
	}

	public void setNormalRandomVariable2(LinkedList<Double> normalRandomVariable) 
	{
		this.normalRandomVariable2 = normalRandomVariable;
	}
	
	public BoxMullerInGPU(double[] UniformRandomVariable1, double[] UniformRandomVariable2)
	{
		int size1 = UniformRandomVariable1.length;
		int size2 = UniformRandomVariable2.length;
		int size = Math.min(size1, size2);
		
		/*
		for(int i = 0; i< size; i++)
		{
			System.out.println(UniformRandomVariable1[i] + " " + UniformRandomVariable2[i]);
		}
		*/
		
        cl_platform_id[] platforms = new cl_platform_id[1];

        clGetPlatformIDs(1,platforms,null);

        cl_platform_id platform = platforms[0];
        cl_device_id[] devices = new cl_device_id[1];

        clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1,devices, null);

        cl_device_id device = devices[0];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM,platform);

        // Create a context for the selected device
        cl_context context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        @SuppressWarnings("deprecation")
		cl_command_queue command_queue = clCreateCommandQueue(context, device, 0, null);

        // Read the program sources and compile them :
        String src = "__kernel void box_muller_transform(__global const float* a, __global const float* b, __global float* outA, __global float* outB) \n" +
                "{\n" +
                "    int i = get_global_id(0);\n" +
                "    outA[i] = sqrt(-2 * log(a[i])) * cos( 2 * 3.14 * b[i]);\n" +
                "    outB[i] = sqrt(-2 * log(a[i])) * sin( 2 * 3.14 * b[i]);\n" +
                "}";

        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{src}, null, null);

        // Build the program
        clBuildProgram(program, 0,null,null,null,null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "box_muller_transform", null);

        //final long tmp = System.currentTimeMillis();
        //System.out.println(System.currentTimeMillis());
        
        final int n = size;
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArrayA[] = new float[n];
        float dstArrayB[] = new float[n];
        for (int i=0; i<n; i++)
        {
            srcArrayA[i] = (float) UniformRandomVariable1[i];
            srcArrayB[i] = (float) UniformRandomVariable2[i];
            //System.out.println(srcArrayA[i] +" " + srcArrayB[i]);
        }
        
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dstA = Pointer.to(dstArrayA);
        Pointer dstB = Pointer.to(dstArrayB);
        
        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[4];
        memObjects[0] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float *n, srcA, null);

        memObjects[1] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float *n, srcB, null);

        memObjects[2] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_float *n, null,null);

        memObjects[3] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_float *n, null,null);

        //System.out.println(System.currentTimeMillis()-tmp);

        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));

        clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));

        clSetKernelArg(kernel, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[2]));

        clSetKernelArg(kernel, 3,
                Sizeof.cl_mem, Pointer.to(memObjects[3]));


        //System.out.println((System.currentTimeMillis()-tmp));

        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        clEnqueueNDRangeKernel(command_queue, kernel, 1,null,
                global_work_size, local_work_size, 0,null,null);

        // Read the output data
        clEnqueueReadBuffer(command_queue, memObjects[2], CL_TRUE, 0,
                n *Sizeof.cl_float, dstA, 0,null,null);

        clEnqueueReadBuffer(command_queue, memObjects[3], CL_TRUE, 0,
                n *Sizeof.cl_float, dstB, 0,null,null);

        clEnqueueReadBuffer(command_queue, memObjects[0], CL_TRUE, 0,
                n *Sizeof.cl_float, srcA, 0,null,null);

        clEnqueueReadBuffer(command_queue, memObjects[1], CL_TRUE, 0,
                n *Sizeof.cl_float, srcB, 0,null,null);

        
        //System.out.println((System.currentTimeMillis()-tmp));
        
        for(int i=0; i<size; i++)
        {
        	//System.out.println(srcArrayA[i] + " " + srcArrayB[i] + " " + dstArrayA[i] + " " + dstArrayB[i]);
        	
        	double temp1 = (new Float(dstArrayA[i])).doubleValue();
        	//double temp1 = Double.parseDouble(new Float(dstArrayA[i]).toString());
        	this.normalRandomVariable1.add(temp1);
        	double temp2 = (new Float(dstArrayB[i])).doubleValue();
        	//double temp2 = Double.parseDouble(new Float(dstArrayB[i]).toString());
        	this.normalRandomVariable2.add(temp2);
        }
	}
	
    /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    @SuppressWarnings("unused")
	private static String getString(cl_device_id device, int paramName)
    {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];
        clGetDeviceInfo(device, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        clGetDeviceInfo(device, paramName, buffer.length, org.jocl.Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }

    /**
     * Returns the value of the platform info parameter with the given name
     *
     * @param platform The platform
     * @param paramName The parameter name
     * @return The value
     */
    @SuppressWarnings("unused")
	private static String getString(cl_platform_id platform, int paramName)
    {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];

        clGetPlatformInfo(platform, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        clGetPlatformInfo(platform, paramName, buffer.length, org.jocl.Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }

    /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    @SuppressWarnings("unused")
	private static long getLong(cl_device_id device, int paramName)
    {
        return getLongs(device, paramName, 1)[0];
    }

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    private static long[] getLongs(cl_device_id device, int paramName, int numValues)
    {
        long values[] = new long[numValues];
        clGetDeviceInfo(device, paramName, Sizeof.cl_long * numValues, org.jocl.Pointer.to(values), null);
        return values;
    }

    /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    @SuppressWarnings("unused")
	private static int getInt(cl_device_id device, int paramName)
    {
        return getInts(device, paramName, 1)[0];
    }

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    private static int[] getInts(cl_device_id device, int paramName, int numValues)
    {
        int values[] = new int[numValues];
        clGetDeviceInfo(device, paramName, Sizeof.cl_int * numValues, org.jocl.Pointer.to(values), null);
        return values;
    }

    /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    @SuppressWarnings("unused")
	private static long getSize(cl_device_id device, int paramName)
    {
        return getSizes(device, paramName, 1)[0];
    }

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    static long[] getSizes(cl_device_id device, int paramName, int numValues)
    {
        // The size of the returned data has to depend on
        // the size of a size_t, which is handled here
        ByteBuffer buffer = ByteBuffer.allocate(
                numValues * Sizeof.size_t).order(ByteOrder.nativeOrder());
        clGetDeviceInfo(device, paramName, Sizeof.size_t * numValues,
                org.jocl.Pointer.to(buffer), null);
        long values[] = new long[numValues];
        if (Sizeof.size_t == 4)
        {
            for (int i=0; i<numValues; i++)
            {
                values[i] = buffer.getInt(i * Sizeof.size_t);
            }
        }
        else
        {
            for (int i=0; i<numValues; i++)
            {
                values[i] = buffer.getLong(i * Sizeof.size_t);
            }
        }
        return values;
    }
    
    /*
    public static void main(String[] args)
    {
    	int batchSize = 100;
    	double[] u1 = new double[batchSize];
    	double[] u2 = new double[batchSize];
    	Random R = new Random();
    	for(int i=0; i<batchSize; i++)
    	{
    		u1[i] = R.nextDouble(); 
    		if((u1[i] == 0.0) || (u1[i] == 1.0))
    		{
    			u1[i] = 0.5;
    		}
    		u2[i] = R.nextDouble(); 
    		if((u2[i] == 0.0) || (u2[i] == 1.0))
    		{
    			u2[i] = 0.5;
    		}
    		//System.out.println(u1[i]+ "  " + u2[i]);
    	}
    	
    	System.out.println("Executing...");
    	System.out.println("Executing...");
    	System.out.println("Executing...");
    	
		BoxMullerInGPU myBM = new BoxMullerInGPU(u1, u2);
    	
    	//System.out.println("Executing...");
    	//System.out.println("Executing...");
    	//System.out.println("Executing...");
    	
    	
    	int sz = myBM.getNormalRandomVariable1().size();
    	
    	for(int i=0; i<sz; i++)
    	{
    		System.out.println("First: " + myBM.getNormalRandomVariable1().get(i) + "  " + "Second: " + myBM.getNormalRandomVariable2().get(i));
    		//System.out.println(i);
    	}
    	
    	System.out.println("Executed");
    	
    }
    */

}

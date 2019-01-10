package MonteCarlo;

import org.jocl.*;

import static org.jocl.CL.*;

import java.util.LinkedList;
//import java.util.Random;

public class EuropeanOptionPayOutInGPU 
{
	private LinkedList<Double> PayOut = new LinkedList<Double>();
	
	public LinkedList<Double> getPayOut() 
	{
		return PayOut;
	}

	public void setPayOut(LinkedList<Double> payOut) 
	{
		PayOut = payOut;
	}
	
	public EuropeanOptionPayOutInGPU(String Type, double s0, double mu, double sigma, double strike, int Maturity, LinkedList<Double> NormalRV)
	{
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
        
        // passing float arguments in the kernel
        //float[] gpuS0 = new float[1]; gpuS0[0] = (float) s0;
        //float[] gpuMu = new float[1]; gpuMu[0]= (float) mu;
        //float[] gpuSigma = new float[1]; gpuSigma[0]= (float) sigma;
        //float[] gpuStrike = new float[1]; gpuStrike[0]= (float) strike;
        //int[] gpuMaturity = new int[1]; gpuMaturity[0]= Maturity;
        
        float gpuS0 = (float) s0; 
        float gpuMu = (float) mu;
        float gpuSigma = (float) sigma;
        float gpuStrike = (float) strike;
        int gpuMaturity = Maturity;
        
        // Read the program sources and compile them :
        String src = "__kernel void calculate_call(__global const float* a, __global float* outA, float gpuS0, float gpuMu, float gpuSigma, float gpuStrike, int Maturity) \n" +
                "{\n" +
                "    int i = get_global_id(0);\n" +
                "    float sT = gpuS0*exp(((gpuMu-(gpuSigma*gpuSigma/2))*Maturity)+(gpuSigma*sqrt(Maturity)*a[i]));\n" +
                "    outA[i] = max((sT-gpuStrike),(sT-sT));\n" +
                "}\n" +
                "__kernel void calculate_put(__global const float* a, __global float* outA, float gpuS0, float gpuMu, float gpuSigma, float gpuStrike, int Maturity) \n" +
                "{\n" +
                "    int i = get_global_id(0);\n" +
                "    float sT = gpuS0*exp(((gpuMu-(gpuSigma*gpuSigma/2))*Maturity)+(gpuSigma*sqrt(Maturity)*a[i]));\n" +
                "    outA[i] = max((gpuStrike-sT),(sT-sT));\n" +
                "}\n";
        
        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{src}, null, null);

        // Build the program
        clBuildProgram(program, 0,null,null,null,null);

        // Create the kernel
        cl_kernel kernel;
        if(Type.equals("Call"))
        {
        	kernel = clCreateKernel(program, "calculate_call", null);
        }
        else
        {
        	kernel = clCreateKernel(program, "calculate_put", null);
        }

        //final long tmp = System.currentTimeMillis();
        //System.out.println(System.currentTimeMillis());
        
        final int n = NormalRV.size();
        float srcArrayA[] = new float[n];
        float dstArrayA[] = new float[n];
        for (int i=0; i<n; i++)
        {
            double temp = (NormalRV.get(i));
            srcArrayA[i] = (float) temp;
            //System.out.println(srcArrayA[i] +" " + srcArrayB[i]);
        }
        
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer dstA = Pointer.to(dstArrayA);
        Pointer pS0  = Pointer.to(new float[] {gpuS0});
        Pointer pMu  = Pointer.to(new float[] {gpuMu});
        Pointer pSigma  = Pointer.to(new float[] {gpuSigma});
        Pointer pStrike  = Pointer.to(new float[] {gpuStrike});
        Pointer pMaturity  = Pointer.to(new int[] {gpuMaturity});
        
        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[7];
        memObjects[0] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float *n, srcA, null);

        memObjects[1] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_float *n, null,null);
        
        memObjects[2] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float, pS0, null);
        
        memObjects[3] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float, pMu, null);
        
        memObjects[4] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float, pSigma, null);
        
        memObjects[5] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float, pStrike, null);
        
        memObjects[6] = clCreateBuffer(context,
                CL_MEM_READ_ONLY |CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int, pMaturity, null);

        //System.out.println(System.currentTimeMillis()-tmp);

        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));

        clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        
        clSetKernelArg (kernel, 2, Sizeof.cl_float, Pointer.to(new float[] {gpuS0}));
        
        clSetKernelArg (kernel, 3, Sizeof.cl_float, Pointer.to(new float[] {gpuMu}));
        
        clSetKernelArg (kernel, 4, Sizeof.cl_float, Pointer.to(new float[] {gpuSigma}));
        
        clSetKernelArg (kernel, 5, Sizeof.cl_float, Pointer.to(new float[] {gpuStrike}));
        
        clSetKernelArg (kernel, 6, Sizeof.cl_float, Pointer.to(new int[] {gpuMaturity}));

        //System.out.println((System.currentTimeMillis()-tmp));

        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        clEnqueueNDRangeKernel(command_queue, kernel, 1,null,
                global_work_size, local_work_size, 0,null,null);

        // Read the output data
        clEnqueueReadBuffer(command_queue, memObjects[1], CL_TRUE, 0,
                n *Sizeof.cl_float, dstA, 0,null,null);

        clEnqueueReadBuffer(command_queue, memObjects[0], CL_TRUE, 0,
                n *Sizeof.cl_float, srcA, 0,null,null);


        
        //System.out.println((System.currentTimeMillis()-tmp));
        
        for(int i=0; i<n; i++)
        {
        	//System.out.println(srcArrayA[i] + " " + srcArrayB[i] + " " + dstArrayA[i] + " " + dstArrayB[i]);
        	
        	double temp1 = Double.parseDouble(new Float(dstArrayA[i]).toString());
        	this.PayOut.add(temp1);
        }
	}
	
	/*
	public static void main(String[] args)
	{
		LinkedList<Double> nRV = new LinkedList<Double>();
		Random R = new Random();
		for(int i=0; i<100; i++)
		{
			nRV.add(R.nextGaussian());
		}
		EuropeanOptionPayOutInGPU E = new EuropeanOptionPayOutInGPU("Call", 152.35, 0.0001, 0.01, 165, 252, nRV);
		LinkedList<Double> Payouts = E.getPayOut();
		
		for(int i=0; i< Payouts.size(); i++)
		{
			System.out.println("Payout: " + Payouts.get(i));
		}
	}
	*/
}

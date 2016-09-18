package fractal;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_WRITE_ONLY;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clSetKernelArg;
import static org.jocl.CL.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

public class Tester {
	public static final int check1 = 1;
	public static final int check2 = 17;
	
    private static cl_context context;
    private static cl_command_queue commandQueue;
    private static cl_kernel kernel;
	public static void main(String[] args) {
		initCL();
	}
	/**
     * Helper function which reads the file with the given name and returns 
     * the contents of this file as a String. Will exit the application
     * if the file can not be read.
     * 
     * @param fileName The name of the file to read.
     * @return The contents of the file
     */
	/**
     * Initialize OpenCL: Create the context, the command queue
     * and the kernel.
     */
    private static void initCL()
    {
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;
        
        int length = 7;
        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        // Create a command-queue for the selected device
        commandQueue = 
            clCreateCommandQueue(context, device, 0, null);
        System.out.println("bruh");

        // Program Setup
        String source = readFile("toDank.cl");
    	source = source.replaceAll("ELEMENT_LENGTH", Integer.toString(length));
    	source = source.replaceAll("SCALE_MASK", 	  			 "0x3fffffffffffffff");
    	source = source.replaceAll("SIGN_MASK",  	 			 "0x8000000000000000");
    	source = source.replaceAll("SET_ZERO_MASK",  			 "0x4000000000000000");
    	source = source.replaceAll("ZERO_MASK",  	 			 "0x4000000000000000");
    	source = source.replaceAll("FLIP_FIRST_MASK",			 "0x8000000000000000");
    	source = source.replaceAll("UINT_MAX_VALUE", 			 "0x00000000ffffffff");
    	source = source.replaceAll("FLIP_MASK",      			 "0x00000000ffffffff");
    	source = source.replaceAll("UNSIGNEDLONG_TOP_MASK", 	 "0xffffffff00000000");
    	source = source.replaceAll("UNSIGNEDLONG_BOTTOM_MASK",   "0x00000000ffffffff");
		source = source.replaceAll("check1", Integer.toString(check1));
		source = source.replaceAll("check2", Integer.toString(check2));
		
    	System.out.println("bruh");

        // Create the program
        cl_program cpProgram = clCreateProgramWithSource(context, 1, 
            new String[]{ source }, null, null);
        System.out.println("bruh");

        // Build the program
        clBuildProgram(cpProgram, 0, null, "-cl-mad-enable", null, null);
        System.out.println("bruh");

        // Create the kernel
        kernel = clCreateKernel(cpProgram, "test", null);
        System.out.println("bruh");

        // Create the memory object which will be filled with the
        // pixel data
        
        long[] a = new long[length];
        long[] b = new long[length];
        long[] c = new long[length];
        

        a[0] = 0x00000000L;
        a[1] = 576;
        a[2] = 235008;
        a[3] = 23970816;
        a[4] = 0;
        a[5] = 0;
        
        b[0] = 0x00000000L;
        b[1] = 576;
        b[2] = 235008;
        b[3] = 23970816;
        b[4] = 0;
        b[5] = 0;
        		
        /*b[0] = 0x00000000L;
        b[1] = 655;
        b[2] = 1546455613;	
        b[3] = 1917059072;
        b[4] = 0;
        b[5] = 0;*/
        System.out.println("bruh");

        cl_mem d_a = clCreateBuffer(context, CL_MEM_COPY_HOST_PTR | CL_MEM_READ_ONLY, Sizeof.cl_long * length, Pointer.to(a), null);
        cl_mem d_b = clCreateBuffer(context, CL_MEM_COPY_HOST_PTR | CL_MEM_READ_ONLY, Sizeof.cl_long * length, Pointer.to(b), null);
        cl_mem d_c = clCreateBuffer(context, CL_MEM_COPY_HOST_PTR | CL_MEM_WRITE_ONLY, Sizeof.cl_long * length, Pointer.to(c), null);
        System.out.println("bruh");

        clEnqueueWriteBuffer(commandQueue, d_a, CL_TRUE, 0L, Sizeof.cl_long * length, Pointer.to(a), 0, null, null);
        clEnqueueWriteBuffer(commandQueue, d_b, CL_TRUE, 0L, Sizeof.cl_long * length, Pointer.to(b), 0, null, null);
        System.out.println("bruh");

        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(d_a));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(d_b));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(d_c));
        
        
        long global = 1;
        System.out.println("bruh");

        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, 
                new long[]{global}, null, 0, null, null);
        System.out.println("bruh");

        
        clEnqueueReadBuffer(commandQueue, d_c, CL_TRUE, 0L, Sizeof.cl_long * length, Pointer.to(c), 0, null, null);
        System.out.println((int)(a[0] & 0xffffffffL) + " " + Arrays.toString(a));
        System.out.println((int)(b[0] & 0xffffffffL) + " " + Arrays.toString(b));
        System.out.println((int)(c[0] & 0xffffffffL) + " " + Arrays.toString(c));
        for(int i = 0; i < length; i++) {
        	System.out.print((int)a[i]+" ");
        }
        System.out.println();
        for(int i = 0; i < length; i++) {
        	System.out.print((int)b[i]+" ");
        }
        System.out.println();
        for(int i = 0; i < length; i++) {
        	System.out.print((int)c[i]+" ");
        }
        System.out.println();
        
        System.out.println(Long.toHexString(c[0]));
    }

    private static String readFile(String fileName)
    {
        try
        {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName)));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while (true)
            {
                line = br.readLine();
                if (line == null)
                {
                    break;
                }
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}

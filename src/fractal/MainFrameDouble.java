package fractal;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_MEM_WRITE_ONLY;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueWriteBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.MathContext;

import javax.swing.JFrame;

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


public class MainFrameDouble extends JFrame{

	public static final int PREFERRED_WIDTH = 512;
	public static final int PREFERRED_LENGTH = 256;
	public static final int PRECISION = 30;
	public static final int MAX_TIMES = 255;
	
	public static final MathContext MC = new MathContext(PRECISION);
	public static final double X_SCROLL_SPEED = 10;
	public static final double Y_SCROLL_SPEED = 10;
	public static final double ZOOM_SPEED = 1.2;
	public static final double PREFERRED_WIDTH_BD_OVER_TWO = PREFERRED_WIDTH/2;
	public static final double PREFERRED_LENGTH_BD_OVER_TWO = PREFERRED_LENGTH/2;
	public static final BigComplexDouble[][] PIXEL_COMPLEX_ARRAY = new BigComplexDouble[PREFERRED_WIDTH][PREFERRED_LENGTH];
	

	
	public double zoom = 0.08;
	public BigComplexDouble center = new BigComplexDouble(0, 0);
	public int[] dankness = new int[PREFERRED_WIDTH * PREFERRED_LENGTH];
	public MainPanel MP = new MainPanel(dankness, PREFERRED_WIDTH, PREFERRED_LENGTH, MAX_TIMES);
	
	public BigComplexDouble[][] complexArray = new BigComplexDouble[PREFERRED_WIDTH][PREFERRED_LENGTH];
	public BigComplexDouble[][] complexArrayTesting = new BigComplexDouble[PREFERRED_WIDTH][PREFERRED_LENGTH];
	
	private cl_context context;
    private cl_command_queue commandQueue;
    private cl_kernel kernel;
    private cl_mem pixelMem;
    private cl_mem colorMapMem;

	public MainFrameDouble() {
		add(MP);
		setSize(new Dimension(PREFERRED_WIDTH, PREFERRED_LENGTH));
		setPixelComplexArray();
		setVisible(true);
		addKeyListener(new KeyPressed());
		update();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	public static void setPixelComplexArray() {
		for(int i = 0; i < PREFERRED_WIDTH; i++) {
			for(int j = 0; j < PREFERRED_LENGTH; j++) {
				PIXEL_COMPLEX_ARRAY[i][j] = new BigComplexDouble(i, j);
			}
		}
	}
	
	public void fillComplex() {
		BigComplexDouble topLeft = center.subtract(new BigComplexDouble(zoom*PREFERRED_WIDTH_BD_OVER_TWO, zoom*PREFERRED_LENGTH_BD_OVER_TWO));
		for(int i = 0; i < PREFERRED_WIDTH; i++) {
			for(int j = 0; j < PREFERRED_LENGTH; j++) {
				complexArray[i][j] = topLeft.add(PIXEL_COMPLEX_ARRAY[i][j].multiply(zoom));
			}
		}
	}
	public void fillDankness() {
		for(int i = 0; i < PREFERRED_WIDTH; i++) {
			for(int j = 0; j < PREFERRED_LENGTH; j++) {
				dankness[i*PREFERRED_LENGTH + j] = 0;
				complexArrayTesting[i][j] = complexArray[i][j];
				while(complexArrayTesting[i][j].magnitudeLessThanTwo() && dankness[i*PREFERRED_LENGTH + j] < MAX_TIMES) {
					dankness[i*PREFERRED_LENGTH + j]++;
					complexArrayTesting[i][j] = function(complexArrayTesting[i][j], complexArray[i][j]);
				}
			}
		}
	}
	
	private BigComplexDouble function(BigComplexDouble previousState, BigComplexDouble constant) {
		return previousState.multiply(previousState).add(constant);
	}
	private void update() {
		fillComplex();
		fillDankness();
		repaint();
	}
	private class KeyPressed implements KeyListener {

		@Override
		public void keyPressed(KeyEvent keyEvent) {
			int keyCode = keyEvent.getKeyCode();
			if(keyCode == KeyEvent.VK_RIGHT) {
				center.r += zoom*X_SCROLL_SPEED;
				update();
			}
			if(keyCode == KeyEvent.VK_LEFT) {
				center.r -= zoom*(X_SCROLL_SPEED);
				update();
			}
			if(keyCode == KeyEvent.VK_DOWN) {
				center.i += zoom*(Y_SCROLL_SPEED);
				update();
			}
			if(keyCode == KeyEvent.VK_UP) {
				center.i -= zoom*(Y_SCROLL_SPEED);
				update();
			}
			if(keyCode == KeyEvent.VK_1) {
				zoom /= ZOOM_SPEED;
				update();
			}
			if(keyCode == KeyEvent.VK_2) {
				zoom *= ZOOM_SPEED;
				update();
			}
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	/**
     * Initialize OpenCL: Create the context, the command queue
     * and the kernel.
     */
    private void initCL()
    {
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

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

        // Program Setup
        String source = readFile("toDank.cl");

        // Create the program
        cl_program cpProgram = clCreateProgramWithSource(context, 1, 
            new String[]{ source }, null, null);

        // Build the program
        clBuildProgram(cpProgram, 0, null, "-cl-mad-enable", null, null);

        // Create the kernel
        kernel = clCreateKernel(cpProgram, "add", null);

        // Create the memory object which will be filled with the
        // pixel data
 
    }
    /**
     * Helper function which reads the file with the given name and returns 
     * the contents of this file as a String. Will exit the application
     * if the file can not be read.
     * 
     * @param fileName The name of the file to read.
     * @return The contents of the file
     */
    private String readFile(String fileName)
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

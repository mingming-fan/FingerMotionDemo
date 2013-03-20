package mingming.ics.uci.edu.fingermotiondemo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class MotionVizView extends View {
	final String TAG = "MotionVisualization";
	
	final int width = 480;
	final int height = 800;
	
	final float maxx = 242;
	final float  minx = -242;
	final float  maxy = 562;
	final float  miny = 0;
	final float  maxz = 180;
	final float  minz = -207;
	final float ratioX =  2;//1;
	final float ratioY = 1.42f;
	final float ratioZ = 4; //2.1f; // this is actually Y axis
	
	private float x;
	private float y;
	private float z;
	private float r = 10;
	private float z_prev = 0;
	final float ZMax = 5;
	final float ZMin = 0.1f;
	Paint paint;

	private Handler handler;
	
	private boolean tag = true;
	public MotionVizView(Context context) {
		super(context);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		Connect2Server();

		handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				GetDataFromServer();
				
				if(tag)
					handler.postDelayed(this, 1000);
			}}, 1000);

	}
	
    Socket socket = null;
    
    public void Connect2Server()
    {
		try{
			//InetAddress serverAddr = InetAddress.getByName(ip);
			//socket = new Socket(serverAddr,port);	
			Log.i(TAG, "In Socket communication ");
			Log.i(TAG, "IP: " + MotionVisualizationActivity.IP +"; port:"+ MotionVisualizationActivity.port);
			socket = new Socket(MotionVisualizationActivity.IP,MotionVisualizationActivity.port);			
		}
		catch (UnknownHostException e1) {
	         e1.printStackTrace();
	      } catch (IOException e1) {
	         e1.printStackTrace();
	      }
    }
    
    public DataInputStream input = null;
    public void GetDataFromServer()
    {	    
    	Log.i(TAG,"0");
		if(socket != null)
		{
			tag = false;
		  //read data from server
		  try {
			Log.i(TAG, "before creating input stream ");
		  //  BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				if(input == null)
				{
					Log.i(TAG, "before getting inputstream ");
					InputStream inputstream = socket.getInputStream();
					Log.i(TAG, "after getting inputstream ");
					input  = new DataInputStream(inputstream);
					Log.i(TAG, "after getting DataInputStream");
				}
				else
				{
					input.close();
					input  = new DataInputStream(socket.getInputStream());
				}
				Log.i(TAG, "1");
			    if(input != null)
				{
			    	Log.i(TAG, "2");
			    	render();
				}
				else
				{
					Log.i(TAG, "BufferedReader is null... ");
				}
			    Log.i(TAG, "5");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    String data;
    public void render()
    {
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true)
				{
					//Log.i(TAG, "3");
				    
					try {
						data = input.readLine();
					  //  Log.i(TAG, "line: "+ data);
					    if(data == null || data == "")
					    {
					    	Log.i(TAG, "4");
					    	input.close();
					    	break;
					    }
					    
					    
						if(data != null && data != "")
						{	
							//process data
							String[] position = data.split(",");
							if(position.length == 3)
							{
								x = (Float.parseFloat(position[0])- minx)*ratioX;
								y = (Float.parseFloat(position[2]) - minz) * ratioZ;
								//x = Float.parseFloat(position[0]) < 0 ?(Float.parseFloat(position[0])+width) % width : Float.parseFloat(position[0]);
								//y = Float.parseFloat(position[1]) < 0 ?(Float.parseFloat(position[1])+height)% height :Float.parseFloat(position[1]);
								z = Float.parseFloat(position[1])-miny < 0.0001? 0: (Float.parseFloat(position[1])-miny)/(maxy-miny);
								if(z > z_prev+0.001 )
								{
									r = (r + 1)%200;
								}
								else if ( z < z_prev-0.001)
								{
									r = r > 3? r-1:2;
								}
								z_prev = z;
								postInvalidate();
								//Log.i(TAG, x + " , "+ y + " , "+ r);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				}
			}}).start();
    }
    
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, r, paint);
	}

}

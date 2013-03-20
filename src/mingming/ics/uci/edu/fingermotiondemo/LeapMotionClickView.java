package mingming.ics.uci.edu.fingermotiondemo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class LeapMotionClickView extends View {
	final String TAG = "LeapMotionClickView";
	
	//////////socket related///////////////
	private Handler handler;
	private boolean tag = true;
	
	///////////graphic draw related///////////////////
	private static final int INVALID_POINTER_ID = -1;
	// The ¡®active pointer¡¯ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	private float mScaleFactor;
	private float culmulateScaleFactor;
	
    private float mPosX;
    private float mPosY;     
    private float mLastTouchX;
    private float mLastTouchY;	
	float r;
	int xnum;
	int ynum;
	
	//HashMap<Integer,Integer> pos; 
	//HashMap<Integer,Integer> nonpos; 
	Multimap<Integer,Integer> pos;
	ArrayList<Position> targetPosition;
	Multimap<Integer,Integer> nonpos;
	ArrayList<Position> nontargetPosition;
	
	Paint paint;
	Paint paint2;
	final int targetNumber = 15;
	final int nontargetNumber = 50;

	long starttime,endtime;
	boolean start;
	
	Bitmap cursor;    //finger image
	float curX;
	float curY;
	
	public LeapMotionClickView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		////////graphic draw related//////////////////////////////
		paint = new Paint();
		paint2 = new Paint();
		//pos = new HashMap<Integer,Integer>();
		//nonpos = new HashMap<Integer,Integer>();
		pos = ArrayListMultimap.create(); 
		nonpos = ArrayListMultimap.create();
		targetPosition = new ArrayList<Position>();
		nontargetPosition = new ArrayList<Position>();
		//Log.i("TAG","after generate non-targes");
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		curX = width/2;
		curY = height/2;
		cursor = BitmapFactory.decodeResource(getResources(), R.drawable.cursor);	
		ResetEverything();
		
		
		///////socket related///////////////////////////////////////////
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
	
	private void ResetEverything()
	{
		
		paint.setColor(Color.BLACK);
		paint2.setColor(Color.rgb(80,73,73));
		starttime = 0;
		endtime = 0;
		start = false;
        mScaleFactor = 1.f;
		culmulateScaleFactor = 1.f;
	    mPosX = 0;
	    mPosY = 0;	     
	    mLastTouchX = 0;
	    mLastTouchY = 0;		
		r = 30;
		xnum =  Math.round(240 / r);
		ynum = Math.round(400 / r);
		pos.clear();
		nonpos.clear();
		targetPosition.clear();
		nontargetPosition.clear();
		//Log.i("TAG","xnum:"+xnum+",ynum:"+ynum);
		//Log.i("TAG","generate targes")
		RandomGenerateTargetPos();
		//Log.i("TAG","generate non-targes");
		RandomGenerateNoneTargetPos();
	}
	
	private void RandomGenerateTargetPos()
	{
		//int counter = 0;
		while(pos.size() < targetNumber)
		//while(counter < 15)
		{
			Log.i("TAG","pos.size():"+pos.size());
			int posx = (int)(Math.floor(Math.random()*xnum));
			int posy = (int)(Math.floor(Math.random()*ynum));
			//Log.i("TAG","posx:"+posx+",posy:" + posy);
			//if(!pos.containsKey(posx)|| (pos.containsKey(posx) && pos.get(posx).intValue() != posy))
		//	{	
				//Log.i("TAG","INSIDE");
				pos.put(posx,posy);	
				targetPosition.add(new Position(r+2*r*posx+1, r+2*r*posy+1));
				//Log.i("TAG","posx:"+posx+",posy:" + posy);
		//	}
			//counter ++;
		}
	}
	
	private void RandomGenerateNoneTargetPos()
	{
		while(nonpos.size() < nontargetNumber)
		{
			int posx = (int)(Math.floor(Math.random()*xnum));
			int posy = (int)(Math.floor(Math.random()*ynum));
		//	Log.i("TAG","nonpos.size():"+nonpos.size());
		//	if(!nonpos.containsKey(posx)|| (nonpos.containsKey(posx) && nonpos.get(posx).intValue() != posy))
			if(!pos.containsEntry(posx, posy))
			{
				nonpos.put(posx,posy);
				nontargetPosition.add(new Position(r+2*r*posx+1, r+2*r*posy+1));
				//Log.i("TAG","posx:"+posx+",posy:" + posy);
			}
		}
	}
	
    Socket socket = null;
    
    public void Connect2Server()
    {
		try{
			//InetAddress serverAddr = InetAddress.getByName(ip);
			//socket = new Socket(serverAddr,port);	
			Log.i(TAG, "In Socket communication ");
			Log.i(TAG, "IP: " + LeapMotionClickActivity.IP +"; port:"+ LeapMotionClickActivity.port);
			socket = new Socket(LeapMotionClickActivity.IP,LeapMotionClickActivity.port);			
		}
		catch (UnknownHostException e1) {
	         e1.printStackTrace();
	      } catch (IOException e1) {
	         e1.printStackTrace();
	      }
    }
    
    
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        for(int i = 0; i < targetPosition.size(); i++)
        {
        	canvas.drawCircle(targetPosition.get(i).x, targetPosition.get(i).y, r, paint2);
        }
        
        for(int i = 0; i < nontargetPosition.size(); i++)
        {
        	canvas.drawCircle(nontargetPosition.get(i).x, nontargetPosition.get(i).y, r, paint);
        }
        
        canvas.drawBitmap(cursor, curX, curY, null);
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
    
    
    public void simulateClick()
    {
    	long downTime = SystemClock.uptimeMillis();
    	long eventTime = SystemClock.uptimeMillis() + 100;
    	// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
    	
    	MotionEvent motionEvent = MotionEvent.obtain(
    	    downTime, 
    	    eventTime, 
    	    MotionEvent.ACTION_DOWN, 
    	    curX, 
    	    curY, 
    	    0
    	);
    	
    	
    	((Activity) getContext()).dispatchTouchEvent(motionEvent);
    	
    	long downTime1 = SystemClock.uptimeMillis();
     	long eventTime1 = SystemClock.uptimeMillis() + 100;
     	// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
     	
     	MotionEvent motionEvent1 = MotionEvent.obtain(
     	    downTime1, 
     	    eventTime1, 
     	    MotionEvent.ACTION_UP, 
     	    curX, 
     	    curY, 
     	    0
     	);
     	
     	((Activity) getContext()).dispatchTouchEvent(motionEvent1);
    	
    	click = 0;
    }
    
    //
	int width = 480;
	int height = 800;
	//////leap motion related constants//////////////
	final float maxx = 242;
	final float  minx = -242;
	final float  maxy = 562;
	final float  miny = 0;
	final float  maxz = 180;
	final float  minz = -207;
	final float ratioX =  2;//1;
	final float ratioY = 1.42f;
	final float ratioZ = 4; //2.1f; // this is actually Y axis
	///////////////////////////////////////////////////
	private float x = 0;
	private float y = 0;
	private float deltaX = 0;
	private float deltaY = 0;
	private float deltaZ = 1;
	private float z;    
    String data;
    private int click = 0;
    int counterdown = 0;
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
							if(position.length == 4)
							{								
								click = Integer.parseInt(position[3]);
								if(click == 1)
								{
									counterdown = 100;
									Log.i("TAG","click...");
									simulateClick();
								}
								else
								if((x != 0 || y != 0) && counterdown == 0)
								{
									deltaX = 8*(Float.parseFloat(position[0]) - x);
									deltaY = 8*(Float.parseFloat(position[2]) - y);
									deltaZ = (z+200)/(Float.parseFloat(position[1])+200);
									if(deltaZ > 1)
									{
										deltaZ *= 1.01;
										deltaZ = Math.min(deltaZ, 5);
									}
									else if(deltaZ < 1)
									{
										deltaZ /= 1.01;
										deltaZ = Math.max(deltaZ, 0.01f);
									}
									/*
									if(Float.parseFloat(position[1]) >= 100)
									{
										// 100  250     
										// 1    5
										//deltaZ = 2*Float.parseFloat(position[1])/75 - 5/3;	
										
									}
									else
									{
										//deltaZ = 0.99f*Float.parseFloat(position[1])/150 + 0.34f;
									}
									*/
									x = Float.parseFloat(position[0]);
									y = Float.parseFloat(position[2]);
									z = Float.parseFloat(position[1]);
								}
								else
								{
									x = Float.parseFloat(position[0]);
									y = Float.parseFloat(position[2]);
									z = Float.parseFloat(position[1]);
								}
								
								if(counterdown > 0)
								{
									counterdown --;
									Log.i("TAG","counterdown..."+counterdown);
								}
								
								for(int i = 0; i < targetPosition.size();i++)
								{
									Position tmp = targetPosition.get(i);
								//	Log.i("TAG","before:"+tmp.x+","+tmp.y);
									targetPosition.get(i).updatePos((tmp.x - deltaX), (tmp.y - deltaY));
								//	Log.i("TAG","before:"+targetPosition.get(i).x+","+targetPosition.get(i).y);
								}
								
								for(int i = 0; i < nontargetPosition.size();i++)
								{
									Position tmp = nontargetPosition.get(i);
								//	Log.i("TAG","before:"+tmp.x+","+tmp.y);
									nontargetPosition.get(i).updatePos((tmp.x - deltaX) , (tmp.y - deltaY));
								//	Log.i("TAG","before:"+nontargetPosition.get(i).x+","+nontargetPosition.get(i).y);
								}
								
								//r *= deltaZ;
								//x = (Float.parseFloat(position[0])- minx)*ratioX;
								//y = (Float.parseFloat(position[2]) - minz) * ratioZ;
								//x = Float.parseFloat(position[0]) < 0 ?(Float.parseFloat(position[0])+width) % width : Float.parseFloat(position[0]);
								//y = Float.parseFloat(position[1]) < 0 ?(Float.parseFloat(position[1])+height)% height :Float.parseFloat(position[1]);
								//z = Float.parseFloat(position[1])-miny < 0.0001? 0: (Float.parseFloat(position[1])-miny)/(maxy-miny);

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
}

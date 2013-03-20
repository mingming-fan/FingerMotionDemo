package mingming.ics.uci.edu.fingermotiondemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.google.common.collect.*;

public class DotsClickExpView extends View {
   
	private static final int INVALID_POINTER_ID = -1;
	// The ¡®active pointer¡¯ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;
	
	private ScaleGestureDetector mScaleDetector;
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
	
	public DotsClickExpView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Log.i("TAG","initialization");
		paint = new Paint();
		paint2 = new Paint();
		//pos = new HashMap<Integer,Integer>();
		//nonpos = new HashMap<Integer,Integer>();
		pos = ArrayListMultimap.create(); 
		nonpos = ArrayListMultimap.create();
		targetPosition = new ArrayList<Position>();
		nontargetPosition = new ArrayList<Position>();
		//Log.i("TAG","after generate non-targes");
		mScaleDetector = new ScaleGestureDetector(context,new ScaleListener());	
		ResetEverything();
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
		r = 10;
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
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		mScaleDetector.onTouchEvent(ev);
		final int action = ev.getAction();
		switch(action & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				mLastTouchX = ev.getX();
				mLastTouchY = ev.getY();
				mActivePointerId = ev.getPointerId(0);
				break;
			case MotionEvent.ACTION_MOVE:
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);
				
				if(!mScaleDetector.isInProgress())
				{
					mPosX += (x - mLastTouchX);
					mPosY += (y - mLastTouchY);
										
					for(int i = 0; i < targetPosition.size();i++)
					{
						Position tmp = targetPosition.get(i);
					//	Log.i("TAG","before:"+tmp.x+","+tmp.y);
						targetPosition.get(i).updatePos(tmp.x += (x - mLastTouchX), tmp.y += (y - mLastTouchY));
					//	Log.i("TAG","before:"+targetPosition.get(i).x+","+targetPosition.get(i).y);
					}
					
					for(int i = 0; i < nontargetPosition.size();i++)
					{
						Position tmp = nontargetPosition.get(i);
					//	Log.i("TAG","before:"+tmp.x+","+tmp.y);
						nontargetPosition.get(i).updatePos(tmp.x += (x - mLastTouchX), tmp.y += (y - mLastTouchY));
					//	Log.i("TAG","before:"+nontargetPosition.get(i).x+","+nontargetPosition.get(i).y);
					}
										
					invalidate();
				}				
				mLastTouchX = x;
				mLastTouchY = y;
				break;		
			case MotionEvent.ACTION_UP:
				final int pointerIndex0 = ev.findPointerIndex(mActivePointerId);
				final float xx = ev.getX(pointerIndex0);
				final float yy = ev.getY(pointerIndex0);
			//	Log.i("TAG","xx:"+xx+",yy:"+yy);
			//	Log.i("TAG","size:"+targetPosition.size());
				
				for(int i = 0; i < targetPosition.size(); i++)
				{
					Position tmp = targetPosition.get(i);
				
					if(Math.pow(tmp.x - xx, 2)+ Math.pow(tmp.y - yy, 2) <= r * r)
					{
						if(!start)
						{
							start = true;
							starttime = System.currentTimeMillis();
						}
						//Log.i("TAG","remove one object");
						targetPosition.remove(i);
						break;
					}
				}
				invalidate();
				mActivePointerId = INVALID_POINTER_ID;
				if(targetPosition.size() == 0)
				{
					// pop up window
					endtime = System.currentTimeMillis();				
					save("test", Long.toString(endtime -starttime));
					onFinishClicking();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				mActivePointerId = INVALID_POINTER_ID;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				final int pointerIndex2 = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = ev.getPointerId(pointerIndex2);
				if(pointerId == mActivePointerId)
				{
					final int newPointerIndex = pointerIndex2 == 0 ? 1: 0;
					mLastTouchX = ev.getX(newPointerIndex);
					mLastTouchY = ev.getY(newPointerIndex);
					mActivePointerId = ev.getPointerId(newPointerIndex);
				}
				break;
				
		}
		return true;
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
	
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

      //  canvas.save();
      //  canvas.translate(mPosX, mPosY);
      //  canvas.scale(mScaleFactor, mScaleFactor);
        
        for(int i = 0; i < targetPosition.size(); i++)
        {
        	canvas.drawCircle(targetPosition.get(i).x, targetPosition.get(i).y, r, paint2);
        }
        
        for(int i = 0; i < nontargetPosition.size(); i++)
        {
        	canvas.drawCircle(nontargetPosition.get(i).x, nontargetPosition.get(i).y, r, paint);
        }
        
        /*
        for(int i = 0; i < xnum; i++)
        	for(int j = 0; j < ynum; j++)
        	{
        		
        		if(pos.containsEntry(i,j))
        		{     			
        			canvas.drawCircle(r+2*r*i+1, r+2*r*j+1, r, paint2);
        		}
        		else 
        		
        		if(nonpos.containsEntry(i, j))
        		{
        			canvas.drawCircle(r+2*r*i+1, r+2*r*j+1, r, paint);
        		}
        	}
        */
     //   canvas.restore();
	}
	
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
		//	mScaleFactor *= detector.getScaleFactor();
		//	mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            mScaleFactor = detector.getScaleFactor();
            culmulateScaleFactor *= mScaleFactor;
            if(culmulateScaleFactor < 0.1f)
            {   
            	culmulateScaleFactor = 0.1f;
            	mScaleFactor = 1;
            }
            else if(culmulateScaleFactor > 10.0f)
            {
            	culmulateScaleFactor = 10.0f;
            	mScaleFactor = 1; 	
            }
		    
			for(int i = 0; i < targetPosition.size();i++)
			{
				Position tmp = targetPosition.get(i);
			//	Log.i("TAG","before:"+tmp.x+","+tmp.y);
				targetPosition.get(i).updatePos(tmp.x *= mScaleFactor, tmp.y *= mScaleFactor);
			//	Log.i("TAG","before:"+targetPosition.get(i).x+","+targetPosition.get(i).y);
			}
			
			for(int i = 0; i < nontargetPosition.size();i++)
			{
				Position tmp = nontargetPosition.get(i);
			//	Log.i("TAG","before:"+tmp.x+","+tmp.y);
				nontargetPosition.get(i).updatePos(tmp.x *= mScaleFactor, tmp.y *= mScaleFactor);
			//	Log.i("TAG","before:"+nontargetPosition.get(i).x+","+nontargetPosition.get(i).y);
			}
			
			r *= mScaleFactor;
			
			invalidate();
			return true;
		}
	}
	
	
	private void onFinishClicking()
	{
		final Intent intent = new Intent(getContext(),MainActivity.class);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		// Add the buttons
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button	
	        	   // reset everything;
	        	   ResetEverything();
	        	   invalidate();
	           }
	       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
			       		((Activity) getContext()).finish();	 
			       		getContext().startActivity(intent);
		        	   //android.os.Process.killProcess(android.os.Process.myPid());
		           }
		       });
	
		builder.setMessage(R.string.dialog_message)
	    .setTitle(R.string.dialog_title);
		// Create the AlertDialog
		builder.create().show();
	}
	
	private void save(String filename, String data)
	{
	      File sdcard = Environment.getExternalStorageDirectory();
          File file = new File(sdcard,"/DotsClickTestTouch.txt");
          if(!file.exists()) 
          {
             try {                           
            	 file.createNewFile();
                 } 
             catch (IOException e) 
             {
                 Toast.makeText(getContext(),"Error creating file!",Toast.LENGTH_LONG).show();
             }                     
          }
          

		try {
			  FileOutputStream fOut = new FileOutputStream(file,true);
	          OutputStreamWriter outWriter = new OutputStreamWriter(fOut);
	          if(outWriter != null)
	          {
	        	  try {
					outWriter.append(data + "\n");
					outWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

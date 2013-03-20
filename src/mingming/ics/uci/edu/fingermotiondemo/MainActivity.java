package mingming.ics.uci.edu.fingermotiondemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity implements OnClickListener{
	final String TAG = "SOCKETAPP";
	EditText ET_IP;
	EditText ET_Port;
	EditText ET_Receive;
	ScrollView sv;
	TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        ET_IP = (EditText)findViewById(R.id.editText_IP);
        ET_Port = (EditText)findViewById(R.id.editText_port);
        ET_Receive = (EditText)findViewById(R.id.editText_receivedata);
        
        Button button_connect = (Button)findViewById(R.id.bt_connect);
        button_connect.setOnClickListener(this);
        
        Button button_dotsclick = (Button)findViewById(R.id.button_test);
        button_dotsclick.setOnClickListener(this);
        
        sv = (ScrollView)findViewById(R.id.scrollView1);
        
        tv = (TextView)findViewById(R.id.tv_result);
              
        sv.fullScroll(View.FOCUS_DOWN);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		//for dots click test
		case R.id.button_test:
			Intent intent = new Intent(this,DotsClickExp.class);
			startActivity(intent);
			break;
		
		case R.id.bt_connect:
			String ip = ET_IP.getText().toString();
			Log.i(TAG, "IP: " + ip);
			if(ip != null)
			{
				int length = ip.split("\\.").length;
				Log.i(TAG,"LENGTH: " + length);
				if(length == 4)
				{
					String port = ET_Port.getText().toString();
					Log.i(TAG, "Port: " + port);
					if(port != null && isInteger(port))
					{
						int portnum = Integer.parseInt(port);
						Log.i(TAG, "IP AND Port: " + ip + "\t" + port);
						//ConnectToServer(ip,portnum);
						//call another activity
						//Intent i = new Intent(this,MotionVisualizationActivity.class);
						Intent i = new Intent(this,LeapMotionClickActivity.class);
						i.putExtra("ip", ip);
						i.putExtra("port",portnum);
						startActivity(i);
					}
				}
			}
			break;
		default:
			break;
				
		}
	}
	
	public static boolean isInteger(String str)
	{
		try
		{
			int i = Integer.parseInt(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}


}

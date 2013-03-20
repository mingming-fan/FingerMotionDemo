package mingming.ics.uci.edu.fingermotiondemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class LeapMotionClickActivity extends Activity {
	
	LeapMotionClickView  LV;
	public static String IP;
	public static int port = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
            }
        // Get data via the key
        IP = extras.getString("ip");
        port = extras.getInt("port");
        
        
        LV = new LeapMotionClickView(this);
        LV.setBackgroundColor(Color.WHITE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(LV);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        
    }
}

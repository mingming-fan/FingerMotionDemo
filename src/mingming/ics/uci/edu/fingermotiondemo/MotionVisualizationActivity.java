package mingming.ics.uci.edu.fingermotiondemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MotionVisualizationActivity extends Activity {
	
	MotionVizView MVV;
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
        
        
        MVV = new MotionVizView(this);
        MVV.setBackgroundColor(Color.WHITE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(MVV);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        

    }
    

}

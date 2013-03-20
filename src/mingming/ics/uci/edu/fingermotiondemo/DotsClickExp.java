package mingming.ics.uci.edu.fingermotiondemo;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class DotsClickExp extends Activity {
	DotsClickExpView dv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dv = (DotsClickExpView)new DotsClickExpView(this);
		dv.setBackgroundColor(Color.WHITE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(dv);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   
	}
	

	
}

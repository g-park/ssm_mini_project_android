package uk.co.halfninja.videokit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static {
	    System.loadLibrary("videokit");
	  }
	public native void initialise();
	public native void run(String[] args);
	public native CharSequence stringFromJNI2();
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.main);
		 context = getApplicationContext();
		 Button buttonRUN = (Button)findViewById(R.id.buttonRUN);
		 buttonRUN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//String[] str = {"¹Ú°¡","¶÷"};
				//run(str);
				Toast.makeText(context, stringFromJNI2(), 0).show();
			}
		});
//		 initialise();	 
	}
}

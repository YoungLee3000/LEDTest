package com.gotop.lcdtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private LinearLayout ll_lcd;

    private boolean mDestroyed = false;

    private int[] mColors = new int[]{
            Color.RED,
            Color.YELLOW,
            Color.BLUE,
            Color.GREEN,
            Color.GRAY,
            Color.WHITE,
            Color.BLACK
    };

    private int mColorCount = mColors.length;
    private final static int MSG_UPDATE = 0x1;

    private PowerManager powerManager = null;
    private WakeLock wakeLock = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE:
                    if(mDestroyed)
                        break;
                    int index = msg.arg1;
                    if(index > mColorCount-1 || index < 0 )
                        index = 0;

                    ll_lcd.setBackgroundColor(mColors[index]);
                    Message msg2 = Message.obtain(mHandler,MSG_UPDATE,index+1,0);
                    sendMessageDelayed(msg2,500);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ll_lcd = (LinearLayout) findViewById(R.id.ll_lcd);

        Message msg = Message.obtain(mHandler,MSG_UPDATE,0,0);
        mHandler.sendMessageDelayed(msg,500);

        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"LEDTest:My lock");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

}

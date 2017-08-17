package com.example.kinnplh.tanforcedemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import static java.lang.Float.NaN;

public class MainActivity extends AppCompatActivity implements Runnable{
    final static int NEW_LINE = 0;
    final static int DEBUG = -1;
    TextView receivedMsgTv;
    Button connectBtn;
    EditText ipEt;
    EditText portEt;
    LinearLayout mainInputLayout;
    GetLogcat logReader;
    Handler h;
    Thread t;
    DemoView dv;
    Vibrator vibrator;
    long startTime;
    int countDispFrameNum;
    float lastNorForce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mainInputLayout = (LinearLayout)findViewById(R.id.inputLayout);
        receivedMsgTv = (TextView) findViewById(R.id.receivedMsg);
        connectBtn = (Button) findViewById(R.id.connect);
        ipEt = (EditText)findViewById(R.id.ip);
        portEt = (EditText)findViewById(R.id.port);
        dv = (DemoView) findViewById(R.id.MainDemoView);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        receivedMsgTv.setText("init");
        h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //receivedMsgTv.setText((String)msg.obj);
                String[] splitedString = ((String) msg.obj).trim().split(" ");
                if(splitedString.length != 6)
                    return;
                float nor_mag = Float.valueOf(splitedString[1]);
                float tan_mag = Float.valueOf(splitedString[3]);
                float tan_dir = NaN;

                if(!splitedString[2].equalsIgnoreCase("nan")){
                    tan_dir = Float.valueOf(splitedString[2]);
                }
                Log.i("force-info", String.format("nor_mag: %f, tan_mag: %f, tan_dir: %f", nor_mag, tan_mag, tan_dir));
                dv.drawForceinfo(tan_mag, tan_dir, nor_mag);
                if(Integer.valueOf(splitedString[4]) == 1){
                    vibrator.vibrate(100);
                }
                if(lastNorForce != nor_mag) {
                    countDispFrameNum += 1;
                    lastNorForce = nor_mag;
                }
                long crtTime = System.currentTimeMillis();
                if(crtTime - startTime > 500){
                    float fps = countDispFrameNum / ((crtTime - startTime) / 500.0f);
                    countDispFrameNum = 0;
                    startTime = crtTime;
                    dv.drawFps(fps);
                }
            }
        };

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logReader = new GetLogcat("", ipEt.getText().toString(), portEt.getText().toString());
                t = new Thread(getThis());
                t.start();
                mainInputLayout.setVisibility(View.INVISIBLE);
                dv.setVisibility(View.VISIBLE);
                startTime = System.currentTimeMillis();
            }
        });
    }

    @Override
    public void run() {
        while(true) {
            String l = logReader.getOneLineLog();
            if(!l.isEmpty()) {
                Message m = new Message();
                m.what = NEW_LINE;
                m.obj = l;
                h.sendMessage(m);
            }
        }
    }

    @Override
    protected void onDestroy() {
        t.interrupt();
        logReader.interrupt();
        super.onDestroy();
    }

    MainActivity getThis(){
        return this;
    }
}

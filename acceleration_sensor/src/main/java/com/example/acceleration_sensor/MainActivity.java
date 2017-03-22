package com.example.acceleration_sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private Button btn_start;
    private TextView tv_step;
    private int step = 0;
    private boolean motiveStata = true;  //是否处于运动状态
    private boolean processStata = false; //标记当前是否已经在记步
    private SensorManager sensorManager;
    private Sensor sensor;
    private double curValues = 0;
    private double lasValues = 0;
    private double oriValues = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);    //注册
        bindView();


    }

    private void bindView() {
        btn_start = (Button) findViewById(R.id.btn_action);
        tv_step = (TextView) findViewById(R.id.tv_step);
        btn_start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        step = 0;
        tv_step.setText("0");
        if (processStata == true) {
            btn_start.setText("开始");
            processStata = false;
        } else {
            btn_start.setText("停止");
            processStata = true;
        }
    }

    public double magnitube(float x, float y, float z) {
        double magnitube = 0;
        magnitube = Math.sqrt(x * x + y * y + z * z);
        return magnitube;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        double range = 1;   //给定一个范围的值
        float[] valus = event.values;
        curValues = magnitube(valus[0], valus[1], valus[2]);  //计算当前的摸
        //向上加速的状态
        if (motiveStata == true) {
            if (curValues >= lasValues)
                lasValues = curValues;
            else {
                //检测到一次峰值
                if (Math.abs(curValues - lasValues) > range) {
                    oriValues = curValues;
                    motiveStata = false;
                }
            }
        }   //向下加速的状态
        if (motiveStata == false) {
            if (curValues <= lasValues) {
                lasValues = curValues;
            } else {
                if (Math.abs(curValues - lasValues) > range) {
                    //检测到一次峰值
                    oriValues = curValues;
                    if (processStata == true) {
                        step++;   //记步数+1
                        Log.d("haha", "step===: "+step);
                        if (processStata == true) {
                            tv_step.setText(step + "");   //更新记步数
                            Log.d("haha", "当前计步数==: "+tv_step);
                        }

                    }

                    motiveStata = true;
                }
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

}

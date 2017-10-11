package com.example.shortcutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_activityid;
    private TextView tv_taskid;
    private TextView tv_applicationid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_activityid = (TextView) findViewById(R.id.tv_activityid);
        tv_taskid = (TextView) findViewById(R.id.tv_taskid);
        tv_applicationid = (TextView) findViewById(R.id.tv_applicationid);
        settingIds();
    }

    private void settingIds(){
        tv_activityid.setText("Activity:" + hashCode());
        tv_taskid.setText("TaskId:" + getTaskId());
        tv_applicationid.setText("Application:" + getApplication().hashCode());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_install:
                Toast.makeText(this , "Install ShortCut" , Toast.LENGTH_SHORT).show();
                ShortCutUtils.installShortCut(this);
                break;

            case R.id.tv_uninstall:
                Toast.makeText(this , "UnInstall ShortCut" , Toast.LENGTH_LONG).show();
                ShortCutUtils.unInstallShortCut(this);
                break;

            default:
                break;
        }
        settingIds();
    }
}

package com.facedetect.ui;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facedetect.Constant.Constant;
import com.facedetect.R;

/**
 * Created by l00385426 on 2016/11/11.
 */

public abstract class BaseActivity extends Activity {
    protected <T extends View> T getView(int viewId) {
        View view = findViewById(viewId);
        return (T) view;
    }

    public View btnBack;
    public TextView titleName;
    public LinearLayout titleLayout;

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void initTitle() {
        btnBack = getView(R.id.left_title_layout);
        titleName = getView(R.id.center_title);
        titleLayout = getView(R.id.TITLE);
    }

    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        }
        return true;
    }

    public void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant.WRITE_EXTERNAL_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeSDCardPermission();
                } else {
                    Toast.makeText(this, "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.READ_EXTERNAL_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSDCardPermission();
                } else {
                    Toast.makeText(this, "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.READ_PHONE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readPhoneNumber();
                } else {
                    Toast.makeText(this, "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.CALL_PHONE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhoneNumber();
                } else {
                    Toast.makeText(this, "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.ACCESS_COARSE_LOCATION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhoneNumber();
                } else {
                    Toast.makeText(this, "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void callPhoneNumber() {

    }
    public void readPhoneNumber() {

    }

    public void writeSDCardPermission() {

    }

    public void readSDCardPermission() {

    }
}

package com.facedetect.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.facedetect.Bean.EventBean;
import com.facedetect.Constant.Constant;
import com.facedetect.Constant.DataSource;
import com.facedetect.R;
import com.facedetect.adapter.FourPicGridAdapter;
import com.facedetect.adapter.FourPicGridStrAdapter;
import com.facedetect.sdk.FacePlusPlusHelper;
import com.facedetect.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.facedetect.ui.CompareActivity.GROUP_CHOSE_PICTURE;
import static com.facedetect.ui.CompareActivity.GROUP_TACK_PICTURE;

/**
 * Created by l00385426 on 2016/11/11.
 */

public class SearchFamilyActivity extends BaseActivity implements View.OnClickListener {

    private AlertDialog dialog;
    private AlertDialog pushMessageDialog;
    private ProgressDialog mProgressDialog;
    private Button mUploadPic;
    private GridView gridView;
    private FourPicGridAdapter adapter;
    private List<EventBean> beans;
    private String takePicFromCamera;


    private GridView uploadGridView;
    private FourPicGridStrAdapter uploadAdapter;
    private List<String> uploadBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_family);
        EventBus.getDefault().register(this);
        initTitle();
        btnBack.setOnClickListener(this);
        titleName.setText("寻找家人");
        mUploadPic = getView(R.id.upload_picture);
        mUploadPic.setOnClickListener(this);
        gridView = getView(R.id.search_family_comparison_grid);
        beans = new ArrayList<>();
        beans.addAll(DataSource.getInstance().getBeansFromUrl());
        adapter = new FourPicGridAdapter(this, beans, R.layout.gridview_four_pic_item);
        gridView.setAdapter(adapter);
//
        uploadGridView = getView(R.id.search_family_upLoad_history_grid);
        uploadBeans = new ArrayList<>();
        uploadBeans.addAll(DataSource.getInstance().getUploadPicHistory());
        uploadAdapter = new FourPicGridStrAdapter(this, uploadBeans, R.layout.gridview_four_pic_item);
        uploadGridView.setAdapter(uploadAdapter);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private Uri uri;
    private String imagePath;

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.left_title_layout:
                finish();
                break;
            case R.id.upload_picture:
                showDialog();
                break;
            case R.id.btn_take_pictures:
                if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(Constant.WRITE_EXTERNAL_CODE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    writeSDCardPermission();
                }
                break;
            case R.id.btn_from_album:
                if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermission(Constant.READ_EXTERNAL_CODE,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    readSDCardPermission();
                }

                break;
            case R.id.push_message_giveup:
                pushMessageDialog.dismiss();
                break;

            case R.id.push_message_submit:

                Intent intent1 = new Intent(SearchFamilyActivity.this,SearchHelpActivity.class);
                startActivity(intent1);
                pushMessageDialog.dismiss();
//                UmengPushHelper.getInstance().sendAndroidBroadcast();
//                if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
//                    requestPermission(Constant.READ_PHONE_CODE,
//                            Manifest.permission.READ_PHONE_STATE);
//                } else {
//
//                    readPhoneNumber();
//                    Intent intent1 = new Intent(SearchFamilyActivity.this,SearchHelpActivity.class);
//                    startActivity(intent1);
//                    pushMessageDialog.dismiss();
//                }

                break;
        }
    }

    @Override
    public void readPhoneNumber() {
        super.readPhoneNumber();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = tm.getLine1Number();
        AVPush push = new AVPush();
        JSONObject object = new JSONObject();
        try {
            object.put("alert", "您附近有孩子或者老人走失，请帮助他");
            object.put(Constant.CLOUD_PHONE_NUMBER, phoneNum);
//            object.put("action","com.facedetect.UPDATE_STATUS");

            for (int i = 0; i < beans.size(); i++) {
                object.put(Constant.CLOUD_IMAGES_STRING + i,
                        beans.get(i).getImage());
                Log.i("AVInstallation", i + ": " + beans.get(i).getImage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        push.setPushToAndroid(true);
        push.setData(object);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // push successfully.

                    Log.i("AVInstallation", "successfully");
                } else {
                    // something wrong.
                    Log.i("AVInstallation", "wrong");
                }
            }
        });
    }


    @Override
    public void readSDCardPermission() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GROUP_CHOSE_PICTURE);
    }

    @Override
    public void writeSDCardPermission() {
        if (takePicFromCamera == null) {
            String sdcardPath;
            if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                sdcardPath = SearchFamilyActivity.this.getFilesDir().getPath();// 没存储卡时使用内存
            } else {
                sdcardPath = android.os.Environment.getExternalStorageDirectory().getPath();
            }
            if (!sdcardPath.endsWith(File.separator)) {
                sdcardPath = sdcardPath + File.separator;
            }
            takePicFromCamera = sdcardPath + System.currentTimeMillis() + ".png";
        }
        uri = Uri.fromFile(new File(takePicFromCamera));
        Intent i = new Intent();
        i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 两个参数:外部存储设备目录,存储的路径
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 设置拍照的照片存储在哪个位置。
        startActivityForResult(i, GROUP_TACK_PICTURE);
    }


    private void showPushMessageDialog() {
        pushMessageDialog = new AlertDialog.Builder(this).create();//创建一个AlertDialog对象
        View view = getLayoutInflater().inflate(R.layout.view_push_message_dialog, null);//自定义布局
        pushMessageDialog.setView(view, 0, 0, 0, 0);//把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        pushMessageDialog.show();//一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = getWindowManager().getDefaultDisplay().getWidth();//得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = pushMessageDialog.getWindow().getAttributes();//得到这个dialog界面的参数对象
        params.width = width - (width / 6);//设置dialog的界面宽度
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;//设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;//设置dialog的重心
        //dialog.getWindow().setLayout(width-(width/6),  LayoutParams.WRAP_CONTENT);//用这个方法设置dialog大小也可以，但是这个方法不能设置重心之类的参数，推荐用Attributes设置
        pushMessageDialog.getWindow().setAttributes(params);//最后把这个参数对象设置进去，即与dialog绑定
        View giveup = view.findViewById(R.id.push_message_giveup);
        View submit = view.findViewById(R.id.push_message_submit);
        giveup.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    private void showDialog() {
        dialog = new AlertDialog.Builder(this).create();//创建一个AlertDialog对象
        View view = getLayoutInflater().inflate(R.layout.view_dialog, null);//自定义布局
        dialog.setView(view, 0, 0, 0, 0);//把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        dialog.show();//一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = getWindowManager().getDefaultDisplay().getWidth();//得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();//得到这个dialog界面的参数对象
        params.width = width - (width / 6);//设置dialog的界面宽度
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;//设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;//设置dialog的重心
        //dialog.getWindow().setLayout(width-(width/6),  LayoutParams.WRAP_CONTENT);//用这个方法设置dialog大小也可以，但是这个方法不能设置重心之类的参数，推荐用Attributes设置
        dialog.getWindow().setAttributes(params);//最后把这个参数对象设置进去，即与dialog绑定
        View btnTakePics = view.findViewById(R.id.btn_take_pictures);
        View btnFromAlbum = view.findViewById(R.id.btn_from_album);
        btnTakePics.setOnClickListener(this);
        btnFromAlbum.setOnClickListener(this);
    }

    public void onEventMainThread(EventBean bean) {
        Log.i("onEventMainThread", bean.toString());
        mProgressDialog.dismiss();
        if (bean.isCorrect()) {
            if (bean.getMode() == FacePlusPlusHelper.METHOD_ONE) {
                DataSource.getInstance().addBeans(bean);
                DataSource.getInstance().addUploadHistory(bean.getImage());
                uploadBeans.add(0, bean.getImage());
                Toast.makeText(SearchFamilyActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                beans.add(0, bean);
                beans.remove(beans.size() - 1);
                adapter.notifyDataSetChanged();
                uploadAdapter.notifyDataSetChanged();
                takePicFromCamera = null;
                dialog.dismiss();
                showPushMessageDialog();
            } else if (bean.getMode() == FacePlusPlusHelper.METHOD_TWO) {
            } else if (bean.getMode() == FacePlusPlusHelper.METHOD_THREE) {
            }
        } else {
            Toast.makeText(this, bean.getDescribe(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GROUP_TACK_PICTURE://照相
                    int angle = MyUtils.readPictureDegree(takePicFromCamera);
                    if (angle != 0) {
                        Bitmap bitmap = MyUtils.rotaingImageView(angle,
                                MyUtils.decodeSampledBitmapFromPath(takePicFromCamera, 400, 400));
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(takePicFromCamera);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                bitmap.recycle();
                            }
                        }
                    } else {
                        faceDetect(takePicFromCamera);
                    }
                    break;
                case GROUP_CHOSE_PICTURE://从相册选取
                    Uri originalUri = data.getData();
                    Cursor cursor = getContentResolver().query(originalUri, null, null, null, null);
                    String str;
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex("_data");
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        if (picturePath == null || picturePath.equals("null")) {
                            return;
                        }
                        str = picturePath;
                    } else {
                        File file = new File(originalUri.getPath());
                        if (!file.exists()) {
                            return;
                        }
                        str = file.getAbsolutePath();
                    }
                    imagePath = str;

                    faceDetect(str);
                    break;
            }
        }
    }

    private void faceDetect(String str) {
        FacePlusPlusHelper.getInstance().faceDetect(str);
        dialog.dismiss();
        mProgressDialog.show();
    }
}

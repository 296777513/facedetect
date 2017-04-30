package com.facedetect.ui;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.facedetect.Bean.EventBean;
import com.facedetect.Constant.Constant;
import com.facedetect.Constant.DataSource;
import com.facedetect.Constant.ImageId;
import com.facedetect.R;
import com.facedetect.adapter.FourPicGridAdapter;
import com.facedetect.utils.BitmapUtil;
import com.facedetect.utils.MyUtils;
import com.lidroid.xutils.BitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class CompareActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static final int GROUP_TACK_PICTURE = 10016;
    public static final int GROUP_CHOSE_PICTURE = 10017;
    public static final int GROUP_TACK_PICTURE_RESULT = 10018;
    public static final String IMAGE_PATH = "takePicFromAlbumStr";

    private String comparePath = null;
    private String takePicFromCamera = null;

    private Uri uri;
    private ImageView imageView;

    private GridView gridView;
    private FourPicGridAdapter adapter;
    private List<EventBean> beans;
    //    private ArrayList<String> imageUrls;
    private int picPos = -1;
    private Button start;
    private AlertDialog dialog;
    private boolean isFromOther = false;
    private Intent intent;
    private TextView addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        initTitle();


        titleLayout.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        titleName.setText("开始比对");


        imageView = (ImageView) findViewById(R.id.btn_take_disappear);
        imageView.setOnClickListener(this);

        start = getView(R.id.activity_main_start);
        start.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.view_picture_comparison_grid);
//        imageUrls = new ArrayList<>();
        beans = new ArrayList<>();
        adapter = new FourPicGridAdapter(this, beans, R.layout.gridview_four_pic_item);
        gridView.setAdapter(adapter);

        intent = getIntent();
        addressView = getView(R.id.view_picture_person_address);

        String str = intent.getStringExtra(Constant.ACTIVITY_FROM_VOLUN);
        Log.i("AVInstallation", "str: " + str);
        if (str != null && str.equals(Constant.ACTIVITY_FROM_VOLUN)) {
            beans.addAll(DataSource.getInstance().getBeansFromUrl());
            adapter.notifyDataSetChanged();
        } else {
            isFromOther = true;
            Log.i("AVInstallation", "AVObject: " + (ImageId.imageIdStr == null));
            String str1 = intent.getExtras().getString("com.avos.avoscloud.Data");
            JSONObject json = JSONObject.parseObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
            Log.i("AVInstallation1", "json: " + json.toString());
            mAddress = json.getString(Constant.CLOUD_ADDRESS);
            for (int i = 0; i < 4; i++) {
                String url = json.getString(Constant.CLOUD_IMAGES_STRING + i);
                EventBean bean = new EventBean();
                bean.setImage(url);
                beans.add(bean);
            }
            adapter.notifyDataSetChanged();
            if (mAddress != null && mAddress.length() != 0) {
                addressView.setVisibility(View.VISIBLE);
                addressView.setText("丢失家人的位置： " + mAddress);
            }
        }

    }

    private String mAddress = null;


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

    private void dismissDialog() {
        dialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                picPos = 0;
                break;

            case 1:
                picPos = 1;
                break;

            case 2:
                picPos = 2;
                break;

            case 3:
                picPos = 3;
                break;
        }
        showDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_title_layout:
                finish();
                break;
            case R.id.activity_main_start:
                if (comparePath == null) {
                    Toast.makeText(this, "请选择需要比对照片", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.setClass(CompareActivity.this, DetectComparisonActivity.class);
                intent.putExtra(IMAGE_PATH, comparePath);
                startActivity(intent);
                break;
            case R.id.btn_take_disappear:
                picPos = 4;
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
            default:
                break;
        }
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
                sdcardPath = CompareActivity.this.getFilesDir().getPath();// 没存储卡时使用内存
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

    private void showImage(String imagePath, int pos) {
        dismissDialog();
        if (imagePath != null && pos != -1 && pos < 4) {
            uri = Uri.fromFile(new File(imagePath));
            EventBean bean = new EventBean();
            bean.setImage(imagePath);
//            imageUrls.add(pos, bitmap);
//            imageUrls.remove(pos + 1);
            beans.add(pos, bean);
            beans.remove(pos + 1);
            adapter.notifyDataSetChanged();
        } else if (imagePath != null && pos == 4) {
            comparePath = imagePath;
            BitmapUtil.getInstance().display(imageView, imagePath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                        showImage(takePicFromCamera, picPos);
                        takePicFromCamera = null;
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
                    showImage(str, picPos);

                    break;
            }
        }
    }
}

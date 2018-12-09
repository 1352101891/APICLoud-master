package com.example.waImageClip.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.v2113723766.yqc.example.waImageClip.R;

import java.io.IOException;


public class GuideActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean flag=false;
    private String imagepath="";
    private ImageView showimage;
    private Button choose;
    private final static int REQUEST_CODE=1112;
    private final static int GETIMAGE=1122;

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        InitView();
    }

    public void InitView(){
        showimage=(ImageView) findViewById(R.id.showimage);
        choose=(Button) findViewById(R.id.choose);
        choose.setOnClickListener(this);
        requestPermisson();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            if (requestCode==GETIMAGE){
                Uri uri=data.getData();
                String path= util.getRealPathFromUri(this,uri);
                // 意图实现activity的跳转
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("path",path);
                startActivityForResult(intent,REQUEST_CODE);
            }else if(requestCode==REQUEST_CODE) {
                String path = data.getStringExtra("path");
                Bitmap bitmap=null;
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        bitmap = util.getBitmapByPath(this,path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    bitmap = BitmapFactory.decodeFile(path);
                }
                showimage.setImageBitmap(bitmap);
            }
        }else if (resultCode==Activity.RESULT_CANCELED){
            Toast.makeText(this,"取消选择图片！",Toast.LENGTH_SHORT).show();
        }
    }

    public void requestPermisson(){

        if ( RequestPermissionHelper.isPermissionStore(this)){
            flag=true;
            getImage();
        }else {
            flag=false;
            showDialog();
            Toast.makeText(this,"请允许读写权限！",Toast.LENGTH_SHORT).show();
        }
    }

    //  提示框
    private void showDialog(){

            new AlertDialog.Builder(this)
                    .setTitle("标题")
                    .setMessage("请去设置中打开该应用的读写权限！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermisson();
                        }
                    })
                    .show();

    }

    public void getImage(){
        if (!flag){
            showDialog();
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        this.startActivityForResult(photoPickerIntent, GETIMAGE);
    }


    public void goback(View view){
        if (!flag){
            showDialog();
        }
        Intent intent=new Intent();
        GuideActivity.this.setResult(RESULT_OK,intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.choose) {
            getImage();
        }
    }
}

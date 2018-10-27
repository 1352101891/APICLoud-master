package com.lechange.demo.example.waImageClip.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.lechange.demo.example.waImageClip.R;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    CropImageView cropImageView;
    private boolean flag=false;
    private String imagepath="";

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagepath=getIntent().getStringExtra("path");
        InitView();
    }

    public void InitView(){

        cropImageView=(CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setAspectRatio(5, 10);
        cropImageView.setFixedAspectRatio(false);
        cropImageView.setGuidelines(1);
        requestPermisson();
    }


    public void requestPermisson(){

        if (RequestPermissionHelper.isPermissionStore(this)){
            flag=true;
            Bitmap bitmap= null;
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    bitmap = util.getBitmapByPath(this,imagepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                bitmap = BitmapFactory.decodeFile(imagepath);
            }
            cropImageView.setImageBitmap(bitmap);
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

    public void rotate(View view){
        if (!flag){
            showDialog();
        }
        cropImageView.rotateImage(90);
    }

    public void getImage(View view){
        if (!flag){
            showDialog();
        }
        Bitmap croppedImage = cropImageView.getCroppedImage();
        String path=util.saveBitmap(this,croppedImage);
        Intent intent=new Intent();
        intent.putExtra("path",path);
        MainActivity.this.setResult(RESULT_OK,intent);
        this.finish();
    }

    public void goback(View view){
        if (!flag){
            showDialog();
        }
        Intent intent=new Intent();
        MainActivity.this.setResult(RESULT_OK,intent);
        this.finish();
    }
}

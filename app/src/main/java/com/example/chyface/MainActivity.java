package com.example.chyface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class MainActivity extends AppCompatActivity {

    private  static  final int GALLERY_REQUEST_CODE = 123;
    Button Btn;
    ImageView im1;
    BitmapDrawable drawable ;
    Bitmap bitmap;
    String imageString = "";
    Bitmap selectedimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        im1 = (ImageView) findViewById(R.id.image_view);
        Btn = (Button)findViewById(R.id.btn);

        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        final Python py= Python.getInstance();

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawable = (BitmapDrawable)im1.getDrawable();
                bitmap = drawable.getBitmap();
                imageString = getStringImage(bitmap);
                PyObject pyo = py.getModule("myscript");
                PyObject obj = pyo.callAttr("main",imageString);

                String str = obj.toString();
                byte data[] = android.util.Base64.decode(str,Base64.DEFAULT);

                Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);

                im1.setImageBitmap(bmp);


            }
        });


       /* if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        Python py= Python.getInstance();
        final PyObject pyobj = py.getModule("script");


        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            PyObject obj = pyobj.callAttr("main");

            Tv.setText(obj.toString());

            }
        });*/



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            try {

                if(Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedimage = ImageDecoder.decodeBitmap(source);

                    im1.setImageBitmap(selectedimage);
                }
                else{
                    selectedimage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    im1.setImageBitmap(selectedimage);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);//burdan hata çıkabilir
        return encodedImage;

    }

    public void selectImage(View view){


        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else{
            Intent intenttogaleri = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intenttogaleri,2);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intenttogaleri = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intenttogaleri,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
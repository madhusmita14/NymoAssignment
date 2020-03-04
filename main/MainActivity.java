package com.madhusmita.nymo_create_dir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView image_placeHolder;
    ImageButton imageCamera,imageGallery;
    BottomSheetBehavior behavior;
    Uri imageUri;
    TextView path_text;
    CardView bottom_sheet_cardView;

    File dataPath;
    File dir;
    public String path;
    String fileName;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Click on image");

        bottom_sheet_cardView=findViewById(R.id.cardView);
        bottom_sheet_cardView.setCardBackgroundColor(Color.WHITE);

        //Attach the bottomsheet with the view and set its state/behavior
        View bottomSheet=findViewById(R.id.bottom_sheet_id);
        behavior=BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //creating objects of all the views used
        image_placeHolder=findViewById(R.id.img);
        path_text=findViewById(R.id.path_txt);
        imageCamera=findViewById(R.id.imageCamera);
        imageGallery=findViewById(R.id.imageGallery);
    }
    public void onCLick(View v)
    {
        switch (v.getId())
        {
            case R.id.img:
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.imageCamera:
                //open camera and capture image
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent IntentCamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(IntentCamera,0);
                break;
            case R.id.imageGallery:
                //open gallery and fetch photo
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent IntentGallery=new Intent();
                IntentGallery.setType("image/*");
                IntentGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(IntentGallery,1);
        }
    }

    private void dataSaveToMemory() {

        BitmapDrawable bitmapDrawable=(BitmapDrawable)image_placeHolder.getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();

        FileOutputStream out=null;
        File internal_storage=Environment.getExternalStorageDirectory();//fetch the root directory

        dir=new File(internal_storage.getAbsolutePath()+"/Nymo");//set own directory

        DeletePreviousFile(dir);//clear directory before entering new data
        dir.mkdirs();

        fileName=String.format("%d.jpg",System.currentTimeMillis());//set the file name with extension

        dataPath=new File(dir,fileName);//fetch the path of data ehere it is stored

        path=dataPath.toString();
        path_text.setText(path);

        try {
            out= new FileOutputStream(dataPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Bitmap bitmapCamera=(Bitmap)data.getExtras().get("data");//fetch intent data in bitmap format
            image_placeHolder.setImageBitmap(bitmapCamera);//attach image with imageview
     
            //Require permission to write external storage
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                {
                    String[] permission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission,EXTERNAL_STORAGE_CODE);
                }
                else
                {
                    dataSaveToMemory();;
                }
            }
            else
            {

            }
        }
        else if(requestCode==1 && data!=null)
        {
            imageUri=data.getData();//get data from storage in uri
            try {
                Bitmap BitmapGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image_placeHolder.setImageBitmap(BitmapGallery);//set image in imageview

                String galleryPath = imageUri.getPath().toString();//parse the usri to string
                path_text.setText(galleryPath);//set path in textview

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
    
     @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case EXTERNAL_STORAGE_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    dataSaveToMemory();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"permission denied",Toast.LENGTH_SHORT).show();
                }
        }
    }

    public static boolean DeletePreviousFile(File dir) {
        if (dir.exists()) {
            File[] fileList = dir.listFiles();
            if (fileList == null) {
                return true;
            }
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    DeletePreviousFile(fileList[i]);
                } else {
                    fileList[i].delete();
                }
            }
        }
        return dir.delete();
    }
}

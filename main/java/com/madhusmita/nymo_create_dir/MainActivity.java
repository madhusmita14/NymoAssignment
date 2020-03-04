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
import android.widget.Toast;

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

        BitmapDrawable draw = (BitmapDrawable)image_placeHolder.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();

        dir = new File(sdCard.getAbsolutePath() + "/Nymo");//saveimage folder
        //Toast.makeText(getApplicationContext(), "" + dir, Toast.LENGTH_SHORT).show();

        DeletePreviousFile(dir);
        dir.mkdirs();

        fileName = String.format("%d.jpg", System.currentTimeMillis());

        dataPath= new File(dir, fileName);//saveimage/pic.jpg
        Toast.makeText(getApplicationContext(), "" +dataPath, Toast.LENGTH_SHORT).show();
        path=dataPath.toString();
        path_text.setText(path);

        try {
            outStream = new FileOutputStream(dataPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
            image_placeHolder.setImageBitmap(bitmapCamera);

            dataSaveToMemory();;
        }
        else if(requestCode==1 && data!=null)
        {
            imageUri = data.getData();
            try {
                Bitmap BitmapGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image_placeHolder.setImageBitmap(BitmapGallery);

                String galleryPath = imageUri.getPath().toString();
                path_text.setText(galleryPath);

            }
            catch (IOException e)
            {
                e.printStackTrace();
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

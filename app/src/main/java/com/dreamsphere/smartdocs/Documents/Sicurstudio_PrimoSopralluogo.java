package com.dreamsphere.smartdocs.Documents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsphere.smartdocs.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class Sicurstudio_PrimoSopralluogo extends AppCompatActivity {

    Context context;
    TextView add_interest_point, create_pdf;
    Integer interestPointNumber;
    EditText worksite_name;
    Bitmap bitmap_map_image, scaledMapImage;
    Integer pageWidth = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document__sicurstudio__primosopralluogo);

        create_pdf = findViewById(R.id.create_pdf);
        worksite_name = findViewById(R.id.worksite_name);
        context=this;
        interestPointNumber=1;




        create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_worksite_name = worksite_name.getText().toString();

                ActivityCompat.requestPermissions(Sicurstudio_PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                try {
                    createPDF(string_worksite_name);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void createPDF(String string_worksite_name) throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "CompanyName - "+string_worksite_name + "- DocumentType");
        OutputStream outputStream = new FileOutputStream(file);
        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page = document.startPage(pageInfo1);
        Canvas canvas = page.getCanvas();

        bitmap_map_image = BitmapFactory.decodeResource(getResources(), R.drawable.background_sky);
        scaledMapImage = Bitmap.createScaledBitmap(bitmap_map_image, 1200,500,false);
        canvas.drawBitmap( scaledMapImage,0,50, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(40);
        canvas.drawText("CompanyName - "+string_worksite_name, pageWidth/2, 200, paint);

        document.finishPage(page);

        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();


    }

    private void checkPermissions() {

        int permsission = ActivityCompat.checkSelfPermission(Sicurstudio_PrimoSopralluogo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            pickImage();
        }else {
            if (permsission != PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(Sicurstudio_PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }else {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri uri = data.getData();
            switch (requestCode){
                case 100:
                    //Intent intent = new Intent(MainActivity.this, )
            }
        }
    }
}
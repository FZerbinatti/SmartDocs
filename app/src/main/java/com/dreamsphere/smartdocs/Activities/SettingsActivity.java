package com.dreamsphere.smartdocs.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dreamsphere.smartdocs.AutenticationServices.LoginActivity;
import com.dreamsphere.smartdocs.R;
import com.dreamsphere.smartdocs.Services.PreferencesData;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {

    ImageButton button_logout, button_signature;
    ImageView image_signature;
    private FirebaseAuth firebaseAuth;
    public static final String TAG ="SettingsActivity";
    Context context;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    File signature_directory;
    private File current_signature_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseAuth = FirebaseAuth.getInstance();

        button_logout = findViewById(R.id.button_logout);
        button_signature = findViewById(R.id.button_signature);
        image_signature = findViewById(R.id.image_signature);
        context= this;
        loadSignatureImage();


        logout();

        addSignature();
    }

    private void loadSignatureImage(){

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("signature", Context.MODE_PRIVATE);
        //String path = Environment.getExternalStorageDirectory().toString()+"/app_signature";
        File newfile = new File(directory, "signature_cropped"+".jpg");
        /*Log.d("Files", "Path: " + directory);
        //File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }*/

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.square_blue_box);
        image_signature.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        Glide
                .with(context)
                .asBitmap()
                .load(Uri.fromFile(newfile))
                .apply(options)
                .into(image_signature);

    }

    private void logout() {
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to Logout?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
                                firebaseAuth.signOut();
                                startActivity(intent);
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            }
        });
    }



    private void addSignature() {
        button_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //scatta una foto, croppala, salvala inla cartella
                String[] PERMISSIONS = {android.Manifest.permission.CAMERA};

                if (!hasPermissions(context, PERMISSIONS))
                {
                    Log.d(TAG, "onClick: 2");
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, MY_CAMERA_PERMISSION_CODE );
                }
                else
                {
                    Log.d(TAG, "onClick: 3");
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }


            }
        });
    }
    // permessi e risultati per GPS e load image
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 6) requestCode:" +requestCode +" resultcode "+resultCode +" data: "+data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //l'immagine bitmap viene caricata nell'imageview con Glide come preview temporanea
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //salva l'immagine come jpg in galleria
            String photo_name = "Signature";
            Log.d(TAG, "onActivityResult: "+photo_name);

            FileOutputStream out=null;
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }else {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("signature", Context.MODE_PRIVATE);
                signature_directory = directory;
                Log.d(TAG, "onActivityResult: "+directory);
                File newfile = new File(directory, photo_name+".jpg");
                if (newfile.exists()) {
                    newfile.delete();
                }
                Log.d("path", newfile.toString());
                FileOutputStream fos = null;
                try {
                    Log.d(TAG, "onActivityResult: ok");
                    fos = new FileOutputStream(newfile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    current_signature_file =newfile;
                    Log.d(TAG, "onActivityResult: "+ current_signature_file.getName());
                    Log.d(TAG, "onActivityResult: "+ current_signature_file.getAbsolutePath());

                    CropImage.activity(Uri.fromFile(newfile))
                            .start(this);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.square_blue_box);

                Glide
                        .with(context)
                        .asBitmap()
                        .load(resultUri)
                        .apply(options)
                        .into(image_signature);


                Log.d(TAG, "onActivityResult: result URI: "+resultUri);
                //questo salvalo nella posizione signature_cropped.jpg
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("signature", Context.MODE_PRIVATE);
                File croppedSignatureUri = new File(String.valueOf(resultUri));
                //InputStream inputStream = getContentResolver().openInputStream(resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(resultUri)));
                    File newfile = new File(directory, "signature_cropped"+".jpg");
                    if (newfile.exists()) {
                        newfile.delete();
                    }
                    FileOutputStream fos = null;
                    Log.d(TAG, "onActivityResult: "+bitmap.getWidth()+"x"+bitmap.getHeight());
                    fos = new FileOutputStream(newfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
/*
                Log.d(TAG, "onActivityResult: "+newfile);
                Log.d(TAG, "onActivityResult: "+newfile.getAbsolutePath());



                try {
                    Log.d(TAG, "onActivityResult: ok");
                    fos = new FileOutputStream(newfile);
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    current_signature_file =newfile;
                    Log.d(TAG, "onActivityResult: "+ current_signature_file.getName());
                    Log.d(TAG, "onActivityResult: "+ current_signature_file.getAbsolutePath());

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: "+error);*/
            }
        }
        
    }
}
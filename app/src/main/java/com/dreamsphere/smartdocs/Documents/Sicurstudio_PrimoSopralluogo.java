package com.dreamsphere.smartdocs.Documents;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.dreamsphere.smartdocs.Adapters.RecyclerView_Marker_Adapter;
import com.dreamsphere.smartdocs.ImageModLibraries.GPSTracker;
import com.dreamsphere.smartdocs.ImageModLibraries.MapPin;
import com.dreamsphere.smartdocs.ImageModLibraries.PinView2;
import com.dreamsphere.smartdocs.Models.Coordinates;
import com.dreamsphere.smartdocs.Models.Marker;
import com.dreamsphere.smartdocs.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Sicurstudio_PrimoSopralluogo extends AppCompatActivity implements View.OnLongClickListener, View.OnTouchListener{
    public static final String TAG ="Documento_Sopralluogo";

    Context context;
    TextView add_interest_point, create_pdf;
    GPSTracker gps;
    private static final int REQUEST = 112;
    PinView2  imageview_map ;
    //RectangleView2 imageview_picture;
    Bitmap bitmap_map_image, scaledMapImage;
    Integer pageWidth = 1200;
    private static int RESULT_LOAD_IMAGE = 1;
    String CHANNEL_ID = "100";
    private static final int CAMERA_REQUEST = 1888;
    ImageView  imgeview_dialog_image;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ConstraintLayout marker_point_view, dialog_background;
    Button annulla_interest_point, save_interest_point;
    ScrollView scrollviewalertDialog;
    RecyclerView recyclerview_markers;
    RecyclerView_Marker_Adapter recyclerView_marker_adapter;
    Float lastKnownX;
    Float lastKnownY;
    ArrayList mapPinsArrayList;
    ArrayList<Coordinates> arrayListPins;
    ArrayList<Marker> marker_list;
    Integer pinCounter;
    MapPin mapPin1;
    String picturePath;
    Paint paint;
    Rect rect;
    float x_down;
    float y_down;
    float x_move;
    float y_move;
    float x_up;
    float y_up;
    Rect rectangle;
    Bitmap current_marker_photo_bitmap;
    Canvas current_photo_canvas;
    Rect current_photo_rectangle;
    PointF current_photo_position;
    int photo_scaled_height;
    String current_photo_description;
    EditText edittext_marker_description;
    ImageView imageview_picture;
    String string_path_current_photo;
    String folderPath;
    private String cameraId;
    private Size imageDimension;
    String photo_name;
    private File current_photo_file;
    ArrayList<File> photos_taken_files;
    File file_photo_directory;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    Integer interestPointNumber;
    ImageButton automatic_coordinates;
    ImageButton button_upload, button_draw_circle, button_add_picture;
    EditText denominazione_opera, indirizzo_cantiere, coordinates_north, coordinates_est;

    double A4_WIDTH = 1240.0;
    double A4_HEIGHT = 1754.0;
    double PHOTO_WIDTH = 1280.0;
    double PREVIEW_PHOTO = 640.0;

    String project_name, user_company, document_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document__sicurstudio__primosopralluogo);

        context=this;
        interestPointNumber=1;
        pinCounter =0;
        mapPinsArrayList = new ArrayList();
        arrayListPins = new ArrayList<>();

        create_pdf = findViewById(R.id.create_pdf);
        denominazione_opera = findViewById(R.id.denominazione_opera);
        indirizzo_cantiere = findViewById(R.id.indirizzo_cantiere);
        automatic_coordinates = findViewById(R.id.automatic_coordinates);
        coordinates_north = findViewById(R.id.coordinates_north);
        coordinates_est = findViewById(R.id.coordinates_est);
        button_upload = findViewById(R.id.button_upload);
        imageview_map = findViewById(R.id.imageview_map);
        imageview_picture = findViewById(R.id.imageview_picture);
        marker_point_view = findViewById(R.id.marker_point_view);

        annulla_interest_point = findViewById(R.id.annulla_interest_point);
        save_interest_point = findViewById(R.id.save_interest_point);
        scrollviewalertDialog = findViewById(R.id.scrollviewalertDialog);
        dialog_background = findViewById(R.id.dialog_background);
        recyclerview_markers = findViewById(R.id.recyclerview_markers);
        imgeview_dialog_image = findViewById(R.id.imgeview_dialog_image);
        button_draw_circle = findViewById(R.id.button_draw_circle);
        button_add_picture = findViewById(R.id.button_add_picture);
        edittext_marker_description = findViewById(R.id.edittext_marker_description);

        imageview_map.isLongClickable();
        imageview_map.isClickable();
        imageview_map.hasOnClickListeners();

        //ottieni il nome del progetto corrente dall'activity precendente per poter salvare il doc sotto il progetto utente corrente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            project_name = extras.getString(getString(R.string.extra_project_name));
            user_company = extras.getString(getString(R.string.extra_user_company));
            document_type = extras.getString(getString(R.string.extra_document_type));
            Log.d(TAG, "onCreate: "+user_company+"/"+ project_name+"/"+document_type);

        }else {

            project_name = "Cantiere Udine Est";
            user_company = "DreamSphereStudio";
            document_type = "Generic_Document";
            Log.d(TAG, "onCreate: "+user_company+"/"+ project_name+"/"+document_type);
        }


        ArrayList mapPins = new ArrayList();
        imageview_map.setPins(mapPins);
        imageview_map.setOnLongClickListener(this);
        imageview_map.setOnTouchListener(this);

        marker_list = new ArrayList<>();
        current_marker_photo_bitmap = null;
        current_photo_canvas = new Canvas();
        current_photo_rectangle = new Rect();
        current_photo_position = new PointF();
        photo_scaled_height =0;
        current_photo_description ="";



        //add image to the image field
        uploadImageview();

        // get the coordinates GPS
        buttonGetCoordinates();

        // generate the pdf file after compiling it
        buttonGeneratePDF();

        buttonAnnullaMarker();

        buttonSaveMarker();



    }

    private void buttonDrawRectangleOnPicture(Canvas canvas) {
        Log.d(TAG, "onActivityResult: canvas: "+canvas.getWidth()+"x"+canvas.getHeight());


        Log.d(TAG, "buttonDrawRectangleOnPicture: ");

        button_draw_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PointF start_point = new PointF(x_down, y_down);
                PointF end_point = new PointF(x_up, y_up);

                PointF moving_point = new PointF(x_move, y_move);
                rectangle= new Rect();
                x_down=0;
                y_down=0;
                x_move=0;
                y_move=0;
                x_up=0;
                y_up=0;
                paint = new Paint();
                rect = new Rect();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);



                imageview_picture.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {



                        //draw rectangle on touch down-> on touch move -> on touch up

                        switch(motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                // touch down code
                                //Log.d(TAG, "onTouchDown: coordinates: X,Y: (" +motionEvent.getX() +"," +motionEvent.getY()+")");
                                x_down=motionEvent.getX();
                                y_down=motionEvent.getY();
                                current_photo_position = new PointF(x_down, y_down);
                                break;

                            case MotionEvent.ACTION_MOVE:
                                // touch move code
                                //Log.d(TAG, "onTouchMove: coordinates: X,Y: (" +motionEvent.getX() +"," +motionEvent.getY()+")");
                                x_move=motionEvent.getX();
                                y_move=motionEvent.getY();
                                rectangle = new Rect((int) Math.round(x_down),(int) Math.round(y_down), (int) Math.round(x_move),(int) Math.round(y_move));
                                //imageview_picture.setRectangle(rectangle);
                                current_photo_rectangle = rectangle;
                                //Log.d(TAG, "onTouch: ");

                                imageview_picture.post(new Runnable(){
                                    public void run(){
                                        imageview_picture.getRootView().postInvalidate();
                                    }
                                });
                                break;

                            case MotionEvent.ACTION_UP:
                                // touch up code
                                Log.d(TAG, "onTouchUp: coordinates: X,Y: (" +motionEvent.getX() +"," +motionEvent.getY()+")");
                                Log.d(TAG, "onClick: current_photo_canvas: photo taken: "+ current_photo_canvas.getWidth() +"x"+ current_photo_canvas.getHeight());
                                Log.d(TAG, "onTouch: rettangolo finale disegnato on photo: "+current_photo_rectangle);

                                break;
                        }
                        return false;
                    }
                });
                imageview_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: click");
                    }
                });
            }
        });

    }

    private void buttonSaveMarker() {

        save_interest_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*              //aggiungi l'immagine e la descrizione alla recycler view, resetta la view dell'immagine imgeview_picture
                //prendi l'immagine del marker e salvalo come bitmap
                paint = new Paint();
                rect = new Rect();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);



                //current_marker_bitmap =  Bitmap.createBitmap((int)PHOTO_WIDTH, photo_scaled_height, Bitmap.Config.ARGB_8888);*/
/*Canvas test = new Canvas(current_marker_photo_bitmap);
                Log.d(TAG, "onClick: current_marker_bitmap: "+ current_marker_photo_bitmap.getWidth() +"x"+ current_marker_photo_bitmap.getHeight());

                double scale_preview_factor =(float) current_marker_photo_bitmap.getWidth()/imageview_picture.getWidth();
                Log.d(TAG, "onClick: scale_preview_factor: "+scale_preview_factor);
                //int rectangle_height = current_photo_rectangle.bottom-current_photo_rectangle.top;
                //test.drawRect(integer_value_scaled(current_photo_rectangle.left, scale_preview_factor),integer_value_scaled(current_photo_rectangle.top+rectangle_height*2, scale_preview_factor),
                //        integer_value_scaled(current_photo_rectangle.right, scale_preview_factor) ,integer_value_scaled(current_photo_rectangle.bottom+rectangle_height*2, scale_preview_factor), paint);
                Log.d(TAG, "onClick: rettangolo in miniatura: " +current_photo_rectangle.left+","+current_photo_rectangle.top+","+ current_photo_rectangle.right+","+current_photo_rectangle.bottom);
                Rect test_rect = new Rect (rectangle.left, rectangle.top, rectangle.right, rectangle.bottom);
                test.drawRect(test_rect, paint);

                test.save();*/
/*                //*******************************************************************************************************

                current_marker_bitmap= current_marker_bitmap.copy(Bitmap.Config.ARGB_8888, true);

                double imageScale = (float) (PREVIEW_PHOTO / current_marker_bitmap.getWidth());
                int scaled_height =(int) Math.round(current_marker_bitmap.getHeight()*imageScale);
                current_marker_bitmap = Bitmap.createScaledBitmap(current_marker_bitmap, (int)PREVIEW_PHOTO, scaled_height,false);
                photo_canvas.drawBitmap( current_marker_bitmap,0,0, new Paint());


                Log.d(TAG, "onClick: ??? "+rectangle.right+"-"+rectangle.left +"="+(rectangle.right-rectangle.left));
                    int marker_width = rectangle.right-rectangle.left;
                    int marker_height = rectangle.bottom-rectangle.top;
                    Log.d(TAG, "onClick: photo passed: "+marker_width+"x"+marker_height);
                    Log.d(TAG, "onClick: rectangle passed: "+rectangle.left+","+rectangle.top+","+rectangle.right+","+rectangle.bottom);
                    int left_scaled = Math.round((float) (rectangle.left*imageScale-marker_width/2));
                    int top_scaled = Math.round((float) (rectangle.top*imageScale+marker_height/2));
                    int right_scaled = Math.round((float) (rectangle.right*imageScale-marker_width/2));
                    int bottom_scaled = Math.round((float) (rectangle.bottom*imageScale+marker_height/2));
                    Rect scaled_rectangle = new Rect(left_scaled, top_scaled, right_scaled, bottom_scaled);
                Log.d(TAG, "onClick: scaled rectangle: "+scaled_rectangle);
                    photo_canvas.drawRect(scaled_rectangle, paint);

                    photo_canvas.save();



 */
/*                Canvas photo_canvas = new Canvas(current_marker_photo_bitmap);
                current_marker_photo_bitmap = current_marker_photo_bitmap.copy(Bitmap.Config.ARGB_8888, true);

                double imageScale = (float) (PREVIEW_PHOTO / current_marker_photo_bitmap.getWidth());
                int scaled_height =(int) Math.round(current_marker_photo_bitmap.getHeight()*imageScale);
                Bitmap scaledPhotoImage = Bitmap.createScaledBitmap(current_marker_photo_bitmap, (int)PREVIEW_PHOTO, scaled_height,false);
                photo_canvas.drawBitmap( scaledPhotoImage,0,200, new Paint());

                Bitmap bitmap_rectangle = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_rectangle);
                int marker_width = rectangle.right-rectangle.left;
                int marker_height = rectangle.bottom-rectangle.top;
                float pointerXscaled = (float) (lastKnownX*imageScale-marker_width/2);
                float pointerYscaled = (float) (lastKnownY*imageScale+marker_height/2);
                Log.d(TAG, "createPDF: pointer coordinates: X: "+pointerXscaled +" Y:"+pointerYscaled);
                photo_canvas.drawBitmap(bitmap_rectangle,pointerXscaled, pointerYscaled, null);
                photo_canvas.save();*/
                //*********************************************************************************************************/
                Log.d(TAG, "onClick: "+current_photo_file.getName());
                Log.d(TAG, "onClick: "+current_photo_file.getAbsolutePath());
                marker_list.add(new Marker(edittext_marker_description.getText().toString(), current_photo_file));
                if(marker_list.size()>0){
                    //marker_list.add(new Marker(edittext_marker_description.getText().toString(), current_marker_photo_bitmap));
                    //imageview_picture.setImage(R.drawable.buttons_background_white_stroke_white_bkg);
                    //edittext_marker_description.setText("");
                    recyclerView_marker_adapter = new RecyclerView_Marker_Adapter(context, marker_list);
                    recyclerView_marker_adapter.notifyDataSetChanged();
                    recyclerview_markers.setLayoutManager(new LinearLayoutManager(context));
                    recyclerview_markers.setAdapter(recyclerView_marker_adapter);

                    alertDialog(false);
                    edittext_marker_description.setText("");
                    hideSoftKeyboard();
                }



            }
        });
    }

    public int integer_value_scaled(int value, double scale){



        int scaled=0;

        scaled = (int) Math.round(value*scale);


        return scaled;

    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void buttonAnnullaMarker() {

        annulla_interest_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog(false);
                //togli il marker corrente

                imageview_map.deletePin(pinCounter-1);
                pinCounter--;
            }
        });
        edittext_marker_description.setText("");
        hideSoftKeyboard();

    }

    private void buttonTakePicture() {
        Log.d(TAG, "buttonTakePicture: 1");

        button_add_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    //metodi per il caricamento dell'immagine
    private void  uploadImageview() {

        button_upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(context, PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST );
                    } else {
                        ImagePickAction();
                    }
                } else {
                    ImagePickAction();
                }

            }
        });
    }

    public void ImagePickAction(){

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void pickImage() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);

    }

    //metodi per la geolocalizzazione
    private void buttonGetCoordinates() {

        automatic_coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION};
                    if (!hasPermissions(context, PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST );
                    } else {
                        //call get location here
                        location();
                    }
                } else {
                    //call get location here
                    location();
                }
            }
        });
    }

    public void location(){
        gps = new GPSTracker(Sicurstudio_PrimoSopralluogo.this);
        // Check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            coordinates_north.setText("N: "+ String.valueOf(latitude));
            coordinates_est.setText("E: "+ String.valueOf(longitude));
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
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
        if (requestCode == 100 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }else if (requestCode == REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //call get location here
            } else {
                Toast.makeText(context, "The app was not allowed to access your location", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == MY_CAMERA_PERMISSION_CODE)
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

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Log.d(TAG, "onActivityResult: selectedImage: "+selectedImage);
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d(TAG, "onActivityResult: picturePath: "+picturePath);


            imageview_map.setImage(ImageSource.bitmap(BitmapFactory.decodeFile(picturePath)));
        }else  if (requestCode == 100){
            Log.d(TAG, "onActivityResult: result code 100");
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Log.d(TAG, "onActivityResult: PHOTO TAKEN");

            //l'immagine bitmap viene caricata nell'imageview con Glide come preview temporanea
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            enablePhotoTakenVIew(true);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.square_blue_box);
            imageview_picture.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

            Glide
                    .with(context)
                    .asBitmap()
                    .load(photo)
                    .apply(options)
                    .into(imageview_picture);

            //salva l'immagine come jpg in galleria
            photo_name = denominazione_opera.getText().toString()+"_marker_photo"+mapPinsArrayList.size();
            Log.d(TAG, "onActivityResult: "+photo_name);

            FileOutputStream out=null;
            ActivityCompat.requestPermissions(Sicurstudio_PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

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
                File directory = cw.getDir("photos", Context.MODE_PRIVATE);
                file_photo_directory = directory;
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
                    current_photo_file=newfile;
                    Log.d(TAG, "onActivityResult: "+current_photo_file.getName());
                    Log.d(TAG, "onActivityResult: "+current_photo_file.getAbsolutePath());
                } catch (java.io.IOException e) {
                    Log.d(TAG, "onActivityResult: porcodio");
                    e.printStackTrace();
                }
            }
        }
    }

    private void createFolder() {
        Log.d(TAG, "createFolder: ");

        folderPath = Environment.getExternalStorageDirectory() + "/SmartDocs";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            File makers_photo_directory = new File(folderPath);
            makers_photo_directory.mkdirs();
            Log.d(TAG, "createFolder: creato folder");
        }
    }

    public void changeAddToDrawIcon(){

        button_add_picture.setVisibility(View.GONE);
        button_draw_circle.setVisibility(View.VISIBLE);
    }

    private void buttonGeneratePDF() {

        create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_denominazione_opera = denominazione_opera.getText().toString();

                ActivityCompat.requestPermissions(Sicurstudio_PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                try {
                    createPDF(string_denominazione_opera);
                    Toast.makeText(context, "PDF Generato, cartella Download", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createPDF(String string_worksite_name) throws FileNotFoundException {
        String pdf_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        Log.d(TAG, "createPDF: writing to: "+pdf_path);
        String pdf_name =user_company+"_"+project_name+string_worksite_name+"_"+currentDate+".pdf";
        File file = new File(pdf_path, pdf_name);
        Log.d(TAG, "createPDF: "+file.getName()+ " path: "+file.getAbsolutePath());

        //Genera un file PDF con altezza e larghezza prefissata di 1 pagina
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder((int)A4_WIDTH, (int)A4_HEIGHT,1).create();
        PdfDocument.Page page = document.startPage(pageInfo1);
        Canvas canvas = page.getCanvas();


        //devo fare il decode Resource della cosa che è adesso dentro l'imageview
        Log.d(TAG, "createPDF: picturePath: "+picturePath);
        bitmap_map_image = BitmapFactory.decodeFile(picturePath);
        //per trasformarla in una bitmap scalabile:
        bitmap_map_image= bitmap_map_image.copy(Bitmap.Config.ARGB_8888, true);

        //crea una canvas con la bitmap dellìimmagine uploaddata, scalala in modo che la height della immagine mappa sia 1/2 della height tot del pdf, e la width sia scalata
        // e la height sia divisa tra il rapport tra la width vera dell'img/2480
        double imageScale = (float) (A4_WIDTH / bitmap_map_image.getWidth());

        Log.d(TAG, "createPDF: imageScale: "+ A4_WIDTH +"/" +bitmap_map_image.getWidth() +"="+imageScale );
        Log.d(TAG, "createPDF: bitmap_map_image.getWidth():"+bitmap_map_image.getWidth());
        Log.d(TAG, "createPDF: bitmap_map_image.getHeight():"+bitmap_map_image.getHeight());
        int scaled_height =(int) Math.round(bitmap_map_image.getHeight()*imageScale);
        Log.d(TAG, "createPDF: scaled_height:"+scaled_height);
        scaledMapImage = Bitmap.createScaledBitmap(bitmap_map_image, (int)A4_WIDTH, scaled_height,false);
        canvas.drawBitmap( scaledMapImage,0,200, new Paint());

        // usa le coordinate dei pointer per applicare le immagini dei pointer sopra l'immagine uploaddata


        //solo se ci sono effettivamente pin/markers allora disegnali sulla mappa
        if (arrayListPins.size()>0){
            for(int i=0; i<arrayListPins.size(); i++){
                //bitmap del numero nei drawable, scalato per la scala dell'immagine
                Bitmap bitmap_number = null;
                switch (i){
                    case 0: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_1);
                    case 1: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_2);
                    case 2: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_3);
                    case 3: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_4);
                    case 4: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_5);
                    case 5: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_6);
                    case 6: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_7);
                    case 7: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_8);
                    case 8: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_9);
                    case 9: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_10);
                    case 10: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_11);
                    case 11: bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_12);
                }


                int marker_width = bitmap_number.getWidth();
                int marker_height = bitmap_number.getHeight();
                float pointerXscaled = (float) (lastKnownX*imageScale-marker_width/2);
                float pointerYscaled = (float) (lastKnownY*imageScale+marker_height/2);
                Log.d(TAG, "createPDF: pointer coordinates: X: "+pointerXscaled +" Y:"+pointerYscaled);
                canvas.drawBitmap(bitmap_number,pointerXscaled, pointerYscaled, null);

            }

        }


        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(60);
        //prendi il punto centrale della pagina per centrarlo
        int width_center =(int) Math.round(bitmap_map_image.getWidth()*imageScale*0.5f);
        Log.d(TAG, "createPDF: page width: "+ A4_WIDTH + " center? "+width_center);
        canvas.drawText("CompanyName - "+string_worksite_name, width_center, 100, paint);


        document.finishPage(page);

        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            Log.d(TAG, "createPDF: ERRORE CREAZIONE PDF");
            e.printStackTrace();
        }

        document.close();
        deleteRecursive(file_photo_directory);
        Log.d(TAG, "createPDF: DOCUMENT CLOSED");
        addNotification(file);

    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public void addNotification(File file) {

        // Aggiunge una notifica che al tap porta agli ultimi download del telefono, dove è presente il file generato
        createNotificationChannel();
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bulb)
                .setColor(context.getResources().getColor(R.color.y2))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Clicca qui per aprire il file")
                )
                .setContentTitle(file.getName())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManagerCompat.notify(1, notification);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.imageview_map) {
            if (pinCounter ==12){
                Toast.makeText(this, "Max number of Marker is 12!", Toast.LENGTH_SHORT).show();
            }else {
                arrayListPins.add(new Coordinates(lastKnownX, lastKnownY));
                mapPin1 = new MapPin(lastKnownX,lastKnownY,pinCounter);
                Log.d(TAG, "onLongClick: coordinates: X: "+lastKnownX+ " , Y: "+lastKnownY);
                pinCounter++;
                mapPinsArrayList.add(mapPin1);
                imageview_map.setPins(mapPinsArrayList);


                imageview_map.post(new Runnable(){
                    public void run(){
                        imageview_map.getRootView().postInvalidate();
                    }
                });

                // qui fai un Alert Dialog falsissimo con possibilità di scattare foto e inserire testo da inserire poi nella recyclerview sotto la mappa

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        alertDialog(true);
                        buttonTakePicture();
                    }
                }, 1000);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.getId()== R.id.imageview_map && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            PointF sCoord =imageview_map.viewToSourceCoord(motionEvent.getX(), motionEvent.getY());
            lastKnownX = sCoord.x;
            lastKnownY = sCoord.y;
        }
        return false;
    }

    public void enablePhotoTakenVIew(boolean enabled){

        if (enabled){
            imgeview_dialog_image.setVisibility(View.INVISIBLE);
            imageview_picture.setVisibility(View.VISIBLE);
        }else{
            imgeview_dialog_image.setVisibility(View.VISIBLE);
            imageview_picture.setVisibility(View.INVISIBLE);
        }


    }

    public void alertDialog(Boolean enabled){

        if (enabled==true){

            dialog_background.setVisibility(View.VISIBLE);
            marker_point_view.setVisibility(View.VISIBLE);
            create_pdf.setVisibility(View.GONE);
            scrollviewalertDialog.setVisibility(View.VISIBLE);
            dialog_background.animate().alpha(1.0f).setDuration(1000);
            marker_point_view.animate().alpha(1.0f).setDuration(1000);
            enablePhotoTakenVIew(false);

        }else {
            dialog_background.setVisibility(View.GONE);
            marker_point_view.setVisibility(View.GONE);
            create_pdf.setVisibility(View.VISIBLE);
            scrollviewalertDialog.setVisibility(View.GONE);
            dialog_background.animate().alpha(0.0f).setDuration(1000);
            marker_point_view.animate().alpha(0.0f).setDuration(1000);
            //button_add_picture.setVisibility(View.VISIBLE);
            //button_draw_circle.setVisibility(View.GONE);


        }

    }
}
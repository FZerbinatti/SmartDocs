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
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.dreamsphere.smartdocs.Models.CompanyInfo;
import com.dreamsphere.smartdocs.Models.Coordinates;
import com.dreamsphere.smartdocs.Models.DocumentDownload;
import com.dreamsphere.smartdocs.Models.Marker;
import com.dreamsphere.smartdocs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrimoSopralluogo extends AppCompatActivity implements View.OnLongClickListener, View.OnTouchListener{
    public static final String TAG ="Documento_Sopralluogo";

    Context context;
    TextView add_interest_point, create_pdf;
    GPSTracker gps;
    private static final int REQUEST = 112;
    PinView2  imageview_map ;
    //RectangleView2 imageview_picture;
    Bitmap bitmap_map_image, scaledMapImage;
    Integer pageWidth = 1200;
    private static final int REQUEST_CODE_SPEACH_INPUT = 1000;

    private static int RESULT_LOAD_IMAGE = 1;
    String CHANNEL_ID = "100";
    private static final int CAMERA_REQUEST = 1888;
    ImageView  imgeview_dialog_image;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ConstraintLayout dialog_background , box_principale;
    Button annulla_interest_point, save_interest_point;
    ConstraintLayout scrollviewalertDialog;

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
    int altezza_photo_marker_pdf =0;
    String current_marker_description;
    int logo_height;;
    ImageButton button_microphone;

    //Dati aziendali
    String company_name;
    String company_info_1;
    String company_info_2;
    String company_logo;

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
        setContentView(R.layout.document_primosopralluogo);

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
        box_principale = findViewById(R.id.box_principale);

        annulla_interest_point = findViewById(R.id.annulla_interest_point);
        save_interest_point = findViewById(R.id.save_interest_point);
        scrollviewalertDialog = findViewById(R.id.scrollviewalertDialog);
        dialog_background = findViewById(R.id.dialog_background);
        recyclerview_markers = findViewById(R.id.recyclerview_markers);
        imgeview_dialog_image = findViewById(R.id.imgeview_dialog_image);
        button_draw_circle = findViewById(R.id.button_draw_circle);
        button_add_picture = findViewById(R.id.button_add_picture);
        edittext_marker_description = findViewById(R.id.edittext_marker_description);
        button_microphone = findViewById(R.id.button_microphone);

        current_photo_file = new File("","");




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
            //loadCompanyInfo(user_company);

        }else {

            project_name = "Cantiere Udine Est";
            user_company = "DreamSphereStudio";
            document_type = "Generic_Document";

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

        buttonMicrophone();



    }

    private void buttonMicrophone() {

        button_microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent_voice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Location of the photo");


                try {
                    startActivityForResult(intent_voice, REQUEST_CODE_SPEACH_INPUT);
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+ e.getMessage());
                }
            }
        });
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

                if (current_photo_file.getName().isEmpty()){
                    Toast.makeText(context,getResources().getString(R.string.aggiungi_foto) , Toast.LENGTH_SHORT).show();
                }else {
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
                        current_marker_description = edittext_marker_description.getText().toString();
                        edittext_marker_description.setText("");
                        hideSoftKeyboard();
                    }


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

                arrayListPins.remove(pinCounter-1);
                //mapPinsArrayList.remove(pinCounter-1);
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
                        try {
                            location();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //call get location here
                    try {
                        location();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void location() throws IOException {
        gps = new GPSTracker(PrimoSopralluogo.this);
        // Check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            coordinates_north.setText("N: "+ String.valueOf(latitude));
            coordinates_est.setText("E: "+ String.valueOf(longitude));
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            Log.d(TAG, "location: coordinates: " +latitude +", "+longitude);
            if (latitude!=0&&longitude!=0){
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                String indirizzo= address;
                indirizzo_cantiere.setText(indirizzo);
            }



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
            photo_name = denominazione_opera.getText().toString().replace(" ","-")+"_marker_photo"+mapPinsArrayList.size();
            Log.d(TAG, "onActivityResult: "+photo_name);

            FileOutputStream out=null;
            ActivityCompat.requestPermissions(PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

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
        }else if (requestCode ==REQUEST_CODE_SPEACH_INPUT){
            if (resultCode == RESULT_OK && data != null ) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String description = edittext_marker_description.getText().toString();

                if (description.equals("") || description.equals(" ") || description.isEmpty() ){
                    edittext_marker_description.setText(result.get(0));
                }else{
                    edittext_marker_description.setText(result.get(0));
                }
            }
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
                Log.d(TAG, "onClick: loading company info for company: "+company_name);
                loadCompanyInfo(company_name);
            }
        });
    }

    private void loadCompanyInfo(String company_name) {

        company_name= "DreamSphereStudio";

        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Companies))
                .child(company_name)
                .child(getString(R.string.firebase_company_info));

        datareference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                CompanyInfo company_document = dataSnapshot.getValue(CompanyInfo.class);
                String string_denominazione_opera = denominazione_opera.getText().toString().replace(" ","-");

                ActivityCompat.requestPermissions(PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                try {
                    createPDF(string_denominazione_opera, company_document);
                    Toast.makeText(context, "PDF Generato, cartella Download", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

    }

    public String getCurrentDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    public String getCurrentDateDay(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    private void createPDF(String string_worksite_name, CompanyInfo company_info) throws IOException {

        // dove salvare il pdf
        String pdf_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();


        // nome del documento
        String pdf_name =user_company+"_"+project_name.replace(" ", "")+"_"+string_worksite_name+"_"+getCurrentDate()+".pdf";
        File file = new File(pdf_path, pdf_name);

        Log.d(TAG, "createPDF: "+file.getName()+ " path: "+file.getAbsolutePath());

        //Genera un file PDF con altezza e larghezza prefissata di 1 pagina
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo page_1_settings = new PdfDocument.PageInfo.Builder((int)A4_WIDTH, (int)A4_HEIGHT,1).create();
        PdfDocument.Page page_1 = document.startPage(page_1_settings);
        Canvas page_1_canvas = page_1.getCanvas();

        //***************************************************************  H E A D E R  ***********************************************************************************

        // COMPANY LOGO

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference company_logo_reference = storage.getReference(getString(R.string.firebase_companies_logos)).child(user_company+".png");
        Log.d(TAG, "createPDF: logo image path: " +getString(R.string.firebase_companies_logos)+"/"+user_company);
        Log.d(TAG, "createPDF: logo image path: " +company_logo_reference);
        final long ONE_MEGABYTE = 1024 * 1024;

        Paint table_paint = new Paint();
        table_paint.setColor(ContextCompat.getColor(context, R.color.black));
        table_paint.setStrokeWidth(3);
        table_paint.setStyle(Paint.Style.STROKE);

        company_logo_reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap company_logo_bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                Log.d(TAG, "onSuccess: received logo: "+company_logo_bitmap);
                Bitmap scaled_company_logo = scaleCenterCrop(company_logo_bitmap, 150,150) ;

                page_1_canvas.drawBitmap(scaled_company_logo,20,20, new Paint());
                page_1_canvas.drawRect(new Rect(20,20,scaled_company_logo.getWidth()+20,scaled_company_logo.getHeight()+20),table_paint );
                logo_height= scaled_company_logo.getHeight();
                int logo_width = scaled_company_logo.getWidth();
                Log.d(TAG, "onSuccess: logo_height: "+logo_height);


                // COMPANY INFOS

/*        company_logo= company_logo.copy(Bitmap.Config.ARGB_8888, true);
        Log.d(TAG, "createPDF: logo: " +company_logo.getWidth()+"x"+company_logo.getHeight());
        double logo_scale = (float) (150.0f / company_logo.getHeight());
        Log.d(TAG, "createPDF: logo_scale: "+logo_scale);
        int logo_scaled_width =(int) Math.round(company_logo.getWidth()*logo_scale);
        Log.d(TAG, "createPDF: logo_scaled_width: " +(int) Math.round(company_logo.getHeight()) + "*" +logo_scale +"= "+logo_scaled_width);
        Bitmap scaled_company_logo = Bitmap.createScaledBitmap(company_logo, (int)150, logo_scaled_width,false);*/


                // DOCUMENT TITLE
                Paint title_paint = new Paint();
                title_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                title_paint.setTextAlign(Paint.Align.LEFT);
                title_paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                title_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_header));
                // DOCUMENT SUBTITLE
                Paint subtitle_paint = new Paint();
                subtitle_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                subtitle_paint.setTextAlign(Paint.Align.LEFT);
                subtitle_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_subheader));
                // DOCUMENT TEXT BOLD
                Paint text_bold_paint = new Paint();
                text_bold_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                text_bold_paint.setTextAlign(Paint.Align.LEFT);
                text_bold_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_page_title));
                // MARKERS DESCRIPTIONS
                Paint text_descriptions_paint = new Paint();
                text_descriptions_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                text_descriptions_paint.setTextAlign(Paint.Align.LEFT);
                text_descriptions_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_subheader));

                // DOCUMENT TEXT
                Paint text_common_paint = new Paint();
                text_common_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                text_common_paint.setTextAlign(Paint.Align.LEFT);
                text_common_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_page_title));

                //prendi il punto centrale della pagina per centrarlo
                int width_center =(int) Math.round(A4_WIDTH*0.5f)+50;
                int title_left_margin = logo_width+40;
                Log.d(TAG, "createPDF: page width: "+ A4_WIDTH + " center? "+title_left_margin);
                int h1=70;
                page_1_canvas.drawText(user_company, title_left_margin, h1, title_paint);
                int h2=h1+50;
                page_1_canvas.drawText(string_worksite_name.replace("-"," ")+" - " +project_name, title_left_margin, h2, subtitle_paint);
                int h3=h2+40;
                page_1_canvas.drawText(getCurrentDateDay() , title_left_margin, h3, subtitle_paint);

                //dati cantiere
                int h4=logo_height+80;
                page_1_canvas.drawText("Nome cantiere: "+ denominazione_opera.getText().toString(), 20, h4, text_common_paint);
                int h5=h4+40;
                page_1_canvas.drawText("Indirizzo cantiere: "+ indirizzo_cantiere.getText().toString(), 20, h5, text_common_paint);
                int h6=h5+40;
                page_1_canvas.drawText("Coordinate cantiere "+ coordinates_north.getText().toString()+ " "+coordinates_est.getText().toString(), 20, h6, text_common_paint);




                //*************************************************************** E N D  H E A D E R  ***********************************************************************************


                // MAPPA DELLA ZONA CON I MARKER

                //devo fare il decode Resource della cosa che è adesso dentro l'imageview
                Log.d(TAG, "createPDF: picturePath: "+picturePath);
                bitmap_map_image = BitmapFactory.decodeFile(picturePath);
                //per trasformarla in una bitmap scalabile:
                bitmap_map_image= bitmap_map_image.copy(Bitmap.Config.ARGB_8888, true);

                //crea una page_1Canvas con la bitmap dellìimmagine uploaddata, scalala in modo che la height della immagine mappa sia 1/2 della height tot del pdf, e la width sia scalata
                // e la height sia divisa tra il rapport tra la width vera dell'img/2480
                double imageScale = (float) (A4_WIDTH / bitmap_map_image.getWidth());

                Log.d(TAG, "createPDF: imageScale: "+ A4_WIDTH +"/" +bitmap_map_image.getWidth() +"="+imageScale );
                Log.d(TAG, "createPDF: bitmap_map_image.getWidth():"+bitmap_map_image.getWidth());
                Log.d(TAG, "createPDF: bitmap_map_image.getHeight():"+bitmap_map_image.getHeight());
                int scaled_height =(int) Math.round(bitmap_map_image.getHeight()*imageScale);
                Log.d(TAG, "createPDF: scaled_height:"+scaled_height);
                scaledMapImage = Bitmap.createScaledBitmap(bitmap_map_image, (int)A4_WIDTH, scaled_height,false);
                int altezza_mappa_in_pdf =h6+20+20+00;
                page_1_canvas.drawBitmap( scaledMapImage,0,altezza_mappa_in_pdf, new Paint());
                altezza_photo_marker_pdf=altezza_mappa_in_pdf+scaledMapImage.getHeight()+50;

                Bitmap current_photo_marker_pdf;

                //solo se ci sono effettivamente pin/markers allora disegnali sulla mappa
                if (arrayListPins.size()>0){
                    for(int i=0; i<arrayListPins.size(); i++){
                        //bitmap del numero nei drawable, scalato per la scala dell'immagine
                        Bitmap bitmap_number = null;

                        if (i==0){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_1);}
                        else if (i==1){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_2);}
                        else if (i==2){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_3);}
                        else if (i==3){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_4);}
                        else if (i==4){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_5);}
                        else if (i==5){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_6);}
                        else if (i==6){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_7);}
                        else if (i==7){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_8);}
                        else if (i==8){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_9);}
                        else if (i==9){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_10);}
                        else if (i==10){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_11);}
                        else if (i==11){bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_12);}

                        // usa le coordinate dei pointer per applicare le immagini dei pointer sopra l'immagine uploaddata
                        int marker_width = bitmap_number.getWidth();
                        int marker_height = bitmap_number.getHeight();

                        float pointerXscaled = (float) (arrayListPins.get(i).getX()*imageScale-marker_width/2);
                        float pointerYscaled = (float) (arrayListPins.get(i).getY()*imageScale+marker_height+h3+20);
                        Log.d(TAG, "createPDF: pointer coordinates: X: "+pointerXscaled +" Y:"+pointerYscaled);
                        page_1_canvas.drawBitmap(bitmap_number, pointerXscaled, pointerYscaled, null);

                    }
                    // fai una tabella con dentro le immagini dei marker distanziate su altezza_photo_marker_pdf


                    if (marker_list.size()>0){
                        page_1_canvas.drawRect(new Rect(0,altezza_photo_marker_pdf, (int) (A4_WIDTH),altezza_photo_marker_pdf+500), table_paint);
                        //metti l'immagine in centercrop a sinistra
                        current_photo_marker_pdf = scaleCenterCrop(BitmapFactory.decodeFile(marker_list.get(0).getFile().getAbsolutePath()), 500, 500);
                        page_1_canvas.drawBitmap( current_photo_marker_pdf,0,altezza_photo_marker_pdf, new Paint());
                        // aggiungi il testo descrizione dell'immagine
                        page_1_canvas.drawText("1) "+marker_list.get(0).getDescription(), current_photo_marker_pdf.getWidth()+40, altezza_photo_marker_pdf+40, text_descriptions_paint);

                        //***************************************************************  F O O T E R  ***********************************************************************************

/*                        Paint footer_paint = new Paint();
                        footer_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                        footer_paint.setTextAlign(Paint.Align.CENTER);
                        footer_paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        footer_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_footer));
                        page_1_canvas.drawText(company_info.getCompany_data(), width_center, (float) (A4_HEIGHT-35), footer_paint);
                        page_1_canvas.drawText(company_info.getCompany_address() +" - "+company_info.getCompany_PIVA() +" - "+company_info.getCompany_number() , width_center, (float) (A4_HEIGHT-15), footer_paint);

                        //  S I G N A T U R E
                        if (true){
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File directory = cw.getDir("signature", Context.MODE_PRIVATE);
                            //String path = Environment.getExternalStorageDirectory().toString()+"/app_signature";
                            File newfile = new File(directory, "signature_cropped"+".jpg");
                            try {
                                Bitmap bitmap_signature = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(newfile));

                                double sign_left = A4_WIDTH-bitmap_signature.getWidth()-2;
                                double sign_top = A4_HEIGHT - bitmap_signature.getHeight()-2;

                                Paint signature_paint = new Paint();
                                signature_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                                signature_paint.setTextAlign(Paint.Align.LEFT);
                                signature_paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                signature_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_signature));

                                page_1_canvas.drawBitmap(bitmap_signature, Math.round(sign_left),(float) sign_top, new Paint());
                                page_1_canvas.drawRect(new Rect((int) Math.round(sign_left), (int) Math.round(sign_top)-2,(int) Math.round(sign_left+bitmap_signature.getWidth()+2),(int) Math.round(sign_top+bitmap_signature.getHeight()+2)), table_paint);
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String userSignature = user.getUid().substring(0,6).toUpperCase()+getCurrentDate().replace(".","").replace("-","");

                                page_1_canvas.drawText(userSignature, (int) Math.round(sign_left), (float) (sign_top-10), signature_paint);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }*/
                        boolean justOnePage;
                        if (marker_list.size()<2){
                            justOnePage =true;
                        }else {
                            justOnePage=false;
                        }
                        addFooter(page_1_canvas, width_center, company_info, justOnePage);

                        //*************************************************************** E N D  F O O T E R  ***********************************************************************************

                        document.finishPage(page_1);

                    }



                    //ho finito la prima pagina con il primo marker, se ci sono tot marker,aggiungi e compila la second apagina
                    if (marker_list.size()>1&&marker_list.size()<=4){
                        PdfDocument.PageInfo page_2_settings = new PdfDocument.PageInfo.Builder((int)A4_WIDTH, (int)A4_HEIGHT,2).create();
                        PdfDocument.Page page_2 = document.startPage(page_2_settings);
                        Canvas page_2_canvas = page_2.getCanvas();
                        for(int i=2; i<=marker_list.size(); i++){
                            page_2_canvas.drawRect(new Rect(0, ((i - 2) * 500), (int) (A4_WIDTH),500), table_paint);
                            current_photo_marker_pdf = scaleCenterCrop(BitmapFactory.decodeFile(marker_list.get(i-1).getFile().getAbsolutePath()), 500, 500);
                            page_2_canvas.drawBitmap( current_photo_marker_pdf,0, ((i - 2) * 500), new Paint());
                            page_2_canvas.drawText(i+") "+marker_list.get(i-1).getDescription(), current_photo_marker_pdf.getWidth()+50, ((i - 2) * 500)+50, text_descriptions_paint);
                            if (i==marker_list.size()){
                                //***************************************************************  F O O T E R  ***********************************************************************************

                                addFooter(page_2_canvas, width_center, company_info, true);

                                //*************************************************************** E N D  F O O T E R  ***********************************************************************************
                                document.finishPage(page_2);}
                        }



                    }else if (marker_list.size()>4&&marker_list.size()<=7){
                        PdfDocument.PageInfo page_3_settings = new PdfDocument.PageInfo.Builder((int)A4_WIDTH, (int)A4_HEIGHT,3).create();
                        PdfDocument.Page page_3 = document.startPage(page_3_settings);
                        Canvas page_3_canvas = page_3.getCanvas();
                        for(int i=5; i<=marker_list.size(); i++){
                            page_3_canvas.drawRect(new Rect(0, ((i - 5) * 500), (int) (A4_WIDTH),500), table_paint);
                            current_photo_marker_pdf = scaleCenterCrop(BitmapFactory.decodeFile(marker_list.get(i-1).getFile().getAbsolutePath()), 500, 500);
                            page_3_canvas.drawBitmap( current_photo_marker_pdf,0, ((i - 5) * 500), new Paint());
                            page_3_canvas.drawText(i+") "+marker_list.get(i-1).getDescription(), current_photo_marker_pdf.getWidth()+20, ((i - 5) * 500), text_descriptions_paint);
                            if (i==marker_list.size()){
                                //***************************************************************  F O O T E R  ***********************************************************************************
                                addFooter(page_3_canvas, width_center, company_info, true);
                                //*************************************************************** E N D  F O O T E R  ***********************************************************************************
                                document.finishPage(page_3);}
                        }

                    }

                    else if (marker_list.size()>7&&marker_list.size()<=10){
                        PdfDocument.PageInfo page_4_settings = new PdfDocument.PageInfo.Builder((int)A4_WIDTH, (int)A4_HEIGHT,4).create();
                        PdfDocument.Page page_4 = document.startPage(page_4_settings);
                        Canvas page_4_canvas = page_4.getCanvas();
                        for(int i=8; i<=marker_list.size(); i++){
                            page_4_canvas.drawRect(new Rect(0, ((i - 8) * 500), (int) (A4_WIDTH),500), table_paint);
                            current_photo_marker_pdf = scaleCenterCrop(BitmapFactory.decodeFile(marker_list.get(i-1).getFile().getAbsolutePath()), 500, 500);
                            page_4_canvas.drawBitmap( current_photo_marker_pdf,0, ((i - 8) * 500), new Paint());
                            page_4_canvas.drawText(i+") "+marker_list.get(i-1).getDescription(), current_photo_marker_pdf.getWidth()+20, ((i - 8) * 500), text_descriptions_paint);
                            if (i==marker_list.size()){
                                //***************************************************************  F O O T E R  ***********************************************************************************
                                addFooter(page_4_canvas, width_center, company_info, true);
                                //*************************************************************** E N D  F O O T E R  ***********************************************************************************
                                document.finishPage(page_4);}
                        }

                    }else if (marker_list.size()>10&&marker_list.size()<=12) {
                        PdfDocument.PageInfo page_5_settings = new PdfDocument.PageInfo.Builder((int) A4_WIDTH, (int) A4_HEIGHT, 5).create();
                        PdfDocument.Page page_5 = document.startPage(page_5_settings);
                        Canvas page_5_canvas = page_5.getCanvas();
                        for (int i = 11; i <= marker_list.size(); i++) {
                            page_5_canvas.drawRect(new Rect(0, ((i - 11) * 500), (int) (A4_WIDTH), 500), table_paint);
                            current_photo_marker_pdf = scaleCenterCrop(BitmapFactory.decodeFile(marker_list.get(i-1).getFile().getAbsolutePath()), 500, 500);
                            page_5_canvas.drawBitmap(current_photo_marker_pdf, 0, ((i - 11) * 500), new Paint());
                            page_5_canvas.drawText(i+") "+marker_list.get(i-1).getDescription(), current_photo_marker_pdf.getWidth() + 20, ((i - 11) * 500), text_descriptions_paint);
                            if (i == marker_list.size()) {
                                //***************************************************************  F O O T E R  ***********************************************************************************
                                addFooter(page_5_canvas, width_center, company_info, true);
                                //*************************************************************** E N D  F O O T E R  ***********************************************************************************
                                document.finishPage(page_5);
                            }
                        }
                    }

                }else {
                    //***************************************************************  F O O T E R  ***********************************************************************************


                    boolean justOnePage;
                    if (marker_list.size()<2){
                        justOnePage =true;
                    }else {
                        justOnePage=false;
                    }
                    addFooter(page_1_canvas, width_center, company_info, justOnePage);
                    //*************************************************************** E N D  F O O T E R  ***********************************************************************************

                    document.finishPage(page_1);

                }


                // FINE DOCUMENTO - GENERAZIONE

                try {
                    document.writeTo(new FileOutputStream(file));
                    //salva doc sul cloud, se è già presente, sostituiscilo

                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReference();


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userID = user.getUid();
                    Log.d(TAG, "onSuccess: genero un'istanza sul cloud con nome: "+userID+"/"+file.getName());
                    StorageReference documentReference = storageRef.child(userID+"/"+file.getName());
                    //  COMMENTATO PER TEST 1250
                    UploadTask taskPdfUpload = documentReference.putFile(Uri.fromFile(file));
                    // Register observers to listen for when the download is done or if it fails
                    taskPdfUpload.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.d("uploadFail", "" + exception);

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.


                            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                            DocumentDownload documentDownload = new DocumentDownload(denominazione_opera.getText().toString()+ " " +getCurrentDateDay(),file.getName());

                            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.firebase_user_projects))
                                    .child(project_name)
                                    .child(denominazione_opera.getText().toString()+ " " +getCurrentDateDay())
                                    .setValue(documentDownload);

                            Log.d("downloadUrl", "" + downloadUrl);
                        }
                    });



                } catch (IOException e) {
                    Log.d(TAG, "createPDF: ERRORE CREAZIONE PDF");
                    e.printStackTrace();
                }

                document.close();
                if (marker_list.size()>0){
                    deleteRecursive(file_photo_directory);
                }

                Log.d(TAG, "createPDF: DOCUMENT CLOSED");
                addNotification(file);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e);
            }
        });


    }

    public void addFooter(Canvas pageToAddFooter, Integer pageCenter, CompanyInfo companyInfo, boolean signature){
        Log.d(TAG, "addFooter .--.........................................---...--.-   : signature? "+signature);

        Paint footer_paint = new Paint();
        footer_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
        footer_paint.setTextAlign(Paint.Align.CENTER);
        footer_paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        footer_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_footer));

        Paint table_paint = new Paint();
        table_paint.setColor(ContextCompat.getColor(context, R.color.black));
        table_paint.setStrokeWidth(3);
        table_paint.setStyle(Paint.Style.STROKE);

        pageToAddFooter.drawText(companyInfo.getCompany_data(), pageCenter, (float) (A4_HEIGHT-35), footer_paint);
        pageToAddFooter.drawText(companyInfo.getCompany_address() +" - "+companyInfo.getCompany_PIVA() +" - "+companyInfo.getCompany_number() , pageCenter, (float) (A4_HEIGHT-15), footer_paint);

        //  S I G N A T U R E
        if (signature){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("signature", Context.MODE_PRIVATE);
            //String path = Environment.getExternalStorageDirectory().toString()+"/app_signature";
            File newfile = new File(directory, "signature_cropped"+".jpg");
            try {
                Bitmap bitmap_signature = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(newfile));

                double sign_left = A4_WIDTH-bitmap_signature.getWidth()-2;
                double sign_top = A4_HEIGHT - bitmap_signature.getHeight()-2;

                Paint signature_paint = new Paint();
                signature_paint.setColor(ContextCompat.getColor(context, R.color.background_dark));
                signature_paint.setTextAlign(Paint.Align.LEFT);
                signature_paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                signature_paint.setTextSize((int) getResources().getDimension(R.dimen.dimens_text_signature));

                pageToAddFooter.drawBitmap(bitmap_signature, Math.round(sign_left),(float) sign_top, new Paint());
                pageToAddFooter.drawRect(new Rect((int) Math.round(sign_left), (int) Math.round(sign_top)-2,(int) Math.round(sign_left+bitmap_signature.getWidth()+2),(int) Math.round(sign_top+bitmap_signature.getHeight()+2)), table_paint);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userSignature = user.getUid().substring(0,6).toUpperCase()+getCurrentDate().replace(".","").replace("-","");

                pageToAddFooter.drawText(userSignature, (int) Math.round(sign_left), (float) (sign_top-10), signature_paint);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
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
        pinCounter++;
        Log.d(TAG, "onLongClick: pin counter: "+pinCounter);


        if (view.getId() == R.id.imageview_map) {


            if (pinCounter ==12){
                Toast.makeText(this, "Max number of Marker is 12!", Toast.LENGTH_SHORT).show();
            }else {
                arrayListPins.add(new Coordinates(lastKnownX, lastKnownY));
                mapPin1 = new MapPin(lastKnownX,lastKnownY,pinCounter);
                Log.d(TAG, "onLongClick: coordinates: X: "+lastKnownX+ " , Y: "+lastKnownY);

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

        if (enabled){

            dialog_background.setVisibility(View.VISIBLE);
            //marker_point_view.setVisibility(View.VISIBLE);
            create_pdf.setVisibility(View.GONE);
            scrollviewalertDialog.setVisibility(View.VISIBLE);
            dialog_background.animate().alpha(1.0f).setDuration(1000);
            //marker_point_view.animate().alpha(1.0f).setDuration(1000);
            enablePhotoTakenVIew(false);
            box_principale.setVisibility(View.GONE);

        }else {
            dialog_background.setVisibility(View.GONE);
            //marker_point_view.setVisibility(View.GONE);
            create_pdf.setVisibility(View.VISIBLE);
            scrollviewalertDialog.setVisibility(View.GONE);
            dialog_background.animate().alpha(0.0f).setDuration(1000);
            //marker_point_view.animate().alpha(0.0f).setDuration(1000);
            //button_add_picture.setVisibility(View.VISIBLE);
            //button_draw_circle.setVisibility(View.GONE);

            box_principale.setVisibility(View.VISIBLE);


        }

    }
}
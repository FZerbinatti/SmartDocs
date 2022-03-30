package com.dreamsphere.smartdocs.Documents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.dreamsphere.smartdocs.Adapters.RecyclerView_Marker_Adapter;
import com.dreamsphere.smartdocs.ImageModLibraries.GPSTracker;
import com.dreamsphere.smartdocs.ImageModLibraries.MapPin;
import com.dreamsphere.smartdocs.ImageModLibraries.PinView2;
import com.dreamsphere.smartdocs.Models.Coordinates;
import com.dreamsphere.smartdocs.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Sicurstudio_PrimoSopralluogo extends AppCompatActivity implements View.OnLongClickListener, View.OnTouchListener{
    public static final String TAG ="DocSPSActivity";

    Context context;
    TextView add_interest_point, create_pdf;
    GPSTracker gps;
    private static final int REQUEST = 112;
    PinView2 imageview_map;
    Bitmap bitmap_map_image, scaledMapImage, scaledPictureImage;
    Integer pageWidth = 1200;
    private static int RESULT_LOAD_IMAGE = 1;
    String CHANNEL_ID = "100";
    private static final int CAMERA_REQUEST = 1888;
    ImageView imgeview_picture, imgeview_dialog_image;
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

    Integer interestPointNumber;
    ImageButton automatic_coordinates;
    ImageButton button_upload, button_draw_circle, button_add_picture;
    EditText denominazione_opera, indirizzo_cantiere, coordinates_north, coordinates_est;

    double A4_WIDTH = 1240.0;
    double A4_HEIGHT = 1754.0;


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
        imgeview_picture = findViewById(R.id.imgeview_picture);
        marker_point_view = findViewById(R.id.marker_point_view);

        annulla_interest_point = findViewById(R.id.annulla_interest_point);
        save_interest_point = findViewById(R.id.save_interest_point);
        scrollviewalertDialog = findViewById(R.id.scrollviewalertDialog);
        dialog_background = findViewById(R.id.dialog_background);
        recyclerview_markers = findViewById(R.id.recyclerview_markers);
        imgeview_dialog_image = findViewById(R.id.imgeview_dialog_image);
        button_draw_circle = findViewById(R.id.button_draw_circle);
        button_add_picture = findViewById(R.id.button_add_picture);

        imageview_map.isLongClickable();
        imageview_map.isClickable();
        imageview_map.hasOnClickListeners();

        ArrayList mapPins = new ArrayList();
        imageview_map.setPins(mapPins);
        imageview_map.setOnLongClickListener(this);
        imageview_map.setOnTouchListener(this);


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

                imgeview_picture.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {



                        //draw rectangle on touch down-> on touch move -> on touch up

                        switch(motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                // touch down code
                                Log.d(TAG, "onTouchDown: coordinates: X,Y: (" +motionEvent.getX() +"," +motionEvent.getY()+")");
                                x_down=motionEvent.getX();
                                y_down=motionEvent.getY();
                                break;

                            case MotionEvent.ACTION_MOVE:
                                // touch move code
                                Log.d(TAG, "onTouchMove: coordinates: X,Y: (" +motionEvent.getX() +"," +motionEvent.getY()+")");
                                x_move=motionEvent.getX();
                                y_move=motionEvent.getY();
                                break;

                            case MotionEvent.ACTION_UP:
                                // touch up code
                                Log.d(TAG, "onTouchUp: coordinates: X,Y: (" +motionEvent.getX() +"," +motionEvent.getY()+")");
                                x_up=motionEvent.getX();
                                y_up=motionEvent.getY();

                                canvas.drawRect(x_down,y_down, x_up, y_up, paint);
                                Log.d(TAG, "onTouch: drawed");
                                imgeview_picture.post(new Runnable(){
                                    public void run(){
                                        imgeview_picture.getRootView().postInvalidate();
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                imgeview_picture.setOnClickListener(new View.OnClickListener() {
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
                //aggiungi l'immagine e la descrizione alla recycler view

                alertDialog(false);

            }
        });
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
            //prendi l'immagine e scalala in modo che sia in formato max HD: 1280x720 con il param H variabile
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d(TAG, "onActivityResult: bitmap size: "+photo.getWidth()+"x"+photo.getHeight());

            double imageScale = (float) (1280 / photo.getWidth());
            int scaled_height =(int) Math.round(photo.getHeight()*imageScale);

            button_add_picture.setVisibility(View.GONE);
            button_draw_circle.setVisibility(View.VISIBLE);

            scaledPictureImage = Bitmap.createScaledBitmap(photo, 1280, scaled_height,false);
            Log.d(TAG, "onActivityResult: scaled: "+scaledPictureImage.getWidth()+"x"+scaledPictureImage.getHeight());
            imgeview_picture.setImageBitmap(scaledPictureImage);
            Canvas canvas = new Canvas(scaledPictureImage);

            //canvas.drawBitmap( scaledPictureImage,0,0, new Paint());
            Log.d(TAG, "onActivityResult: canvas: "+canvas.getWidth()+"x"+canvas.getHeight());
            canvas.save();

            buttonDrawRectangleOnPicture(canvas);
        }
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

        Log.d(TAG, "createPDF: writing to: "+pdf_path);
        String pdf_name ="CompanyName_DocumentType12"+".pdf";
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
            //bitmap del numero nei drawable, scalato per la scala dell'immagine
            Bitmap bitmap_number = BitmapFactory.decodeResource(context.getResources(),R.drawable.pin_1);
            int marker_width = bitmap_number.getWidth();
            int marker_height = bitmap_number.getHeight();
            float pointerXscaled = (float) (lastKnownX*imageScale-marker_width/2);
            float pointerYscaled = (float) (lastKnownY*imageScale+marker_height/2);
            Log.d(TAG, "createPDF: pointer coordinates: X: "+pointerXscaled +" Y:"+pointerYscaled);
            canvas.drawBitmap(bitmap_number,pointerXscaled, pointerYscaled, null);
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
            e.printStackTrace();
        }

        document.close();
        Log.d(TAG, "createPDF: DOCUMENT CLOSED");
        addNotification(file);

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


    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawRect(30, 30, 80, 80, paint);
        paint.setStrokeWidth(0);
        paint.setColor(Color.CYAN);
        canvas.drawRect(33, 60, 77, 77, paint );
        paint.setColor(Color.YELLOW);
        canvas.drawRect(33, 33, 77, 60, paint );

    }

    public void alertDialog(Boolean enabled){

        if (enabled==true){

            dialog_background.setVisibility(View.VISIBLE);
            marker_point_view.setVisibility(View.VISIBLE);
            create_pdf.setVisibility(View.GONE);
            scrollviewalertDialog.setVisibility(View.VISIBLE);
            dialog_background.animate().alpha(1.0f).setDuration(1000);
            marker_point_view.animate().alpha(1.0f).setDuration(1000);

        }else {
            dialog_background.setVisibility(View.GONE);
            marker_point_view.setVisibility(View.GONE);
            create_pdf.setVisibility(View.VISIBLE);
            scrollviewalertDialog.setVisibility(View.GONE);
            dialog_background.animate().alpha(0.0f).setDuration(1000);
            marker_point_view.animate().alpha(0.0f).setDuration(1000);
            button_add_picture.setVisibility(View.VISIBLE);
            button_draw_circle.setVisibility(View.GONE);


        }

    }
}
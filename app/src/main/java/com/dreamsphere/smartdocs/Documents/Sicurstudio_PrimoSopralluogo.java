package com.dreamsphere.smartdocs.Documents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
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
    Bitmap bitmap_map_image, scaledMapImage;
    Integer pageWidth = 1200;
    private static int RESULT_LOAD_IMAGE = 1;

    Float lastKnownX;
    Float lastKnownY;
    ArrayList mapPinsArrayList;
    ArrayList<Coordinates> arrayListPins;
    Integer pinCounter;
    MapPin mapPin1;
    String picturePath;

    Integer interestPointNumber;
    ImageButton automatic_coordinates;
    ImageButton button_upload;
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
        }
    }



    private void buttonGeneratePDF() {

        create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_denominazione_opera = denominazione_opera.getText().toString();

                ActivityCompat.requestPermissions(Sicurstudio_PrimoSopralluogo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
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
        String pdf_name ="CompanyName_DocumentType7"+".pdf";
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

        //genera la notifica che porta alla cartella download
        generateNotification(pdf_path +pdf_name);

    }

    private void generateNotification(String document_path) {


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
}
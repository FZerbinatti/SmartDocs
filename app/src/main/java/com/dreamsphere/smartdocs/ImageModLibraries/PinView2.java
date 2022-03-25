package com.dreamsphere.smartdocs.ImageModLibraries;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dreamsphere.smartdocs.R;

import java.util.ArrayList;

public class PinView2 extends SubsamplingScaleImageView {

    private PointF sPin;

    ArrayList<MapPin> mapPins;
    ArrayList<DrawPin> drawnPins;
    Context context;
    String tag = getClass().getSimpleName();

    public PinView2(Context context) {
        this(context, null);
        this.context = context;
    }

    public PinView2(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        initialise();
    }

    public void setPins(ArrayList<MapPin> mapPins) {
        this.mapPins = mapPins;
        initialise();
        invalidate();
    }

    public void setPin(PointF pin) {
        this.sPin = pin;
    }

    public PointF getPin() {
        return sPin;
    }

    private void initialise() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during       setup.
        if (!isReady()) {
            return;
        }

        drawnPins = new ArrayList<>();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float density = getResources().getDisplayMetrics().densityDpi;


        for (int i = 0; i < mapPins.size(); i++) {

            Bitmap bmpPin = null;
            MapPin mPin = mapPins.get(i);
            //Bitmap bmpPin = Utils.getBitmapFromAsset(context, mPin.getPinImgSrc());

            if (i==0){ bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_1);}
            else if (i==1) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_2);}
            else if (i==2) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_3);}
            else if (i==3) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_4);}
            else if (i==4) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_5);}
            else if (i==5) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_6);}
            else if (i==6) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_7);}
            else if (i==7) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_8);}
            else if (i==8) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_9);}
            else if (i==9) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_10);}
            else if (i==10) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_11);}
            else if (i==11) {bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_12);}

            //Bitmap bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin_1);

            float w = (density / 420f) * bmpPin.getWidth();
            float h = (density / 420f) * bmpPin.getHeight();
            bmpPin = Bitmap.createScaledBitmap(bmpPin, (int) w, (int) h, true);

            PointF vPin = sourceToViewCoord(mPin.getPoint());
            //in my case value of point are at center point of pin image, so we need to adjust it here

            float vX = vPin.x - (bmpPin.getWidth() / 2);
            float vY = vPin.y - bmpPin.getHeight();

            canvas.drawBitmap(bmpPin, vX, vY, paint);

            //add added pin to an Array list to get touched pin
            DrawPin dPin = new DrawPin();
            dPin.setStartX(mPin.getX() - w / 2);
            dPin.setEndX(mPin.getX() + w / 2);
            dPin.setStartY(mPin.getY() - h / 2);
            dPin.setEndY(mPin.getY() + h / 2);
            dPin.setId(mPin.getId());
            drawnPins.add(dPin);
        }
    }

    public int getPinIdByPoint(PointF point) {
        for (int i = drawnPins.size() - 1; i >= 0; i--) {
            DrawPin dPin = drawnPins.get(i);
            if (point.x >= dPin.getStartX() && point.x <= dPin.getEndX()) {
                if (point.y >= dPin.getStartY() && point.y <= dPin.getEndY()) {
                    return dPin.getId();
                }
            }
        }
        return -1; //negative no means no pin selected
    }
}

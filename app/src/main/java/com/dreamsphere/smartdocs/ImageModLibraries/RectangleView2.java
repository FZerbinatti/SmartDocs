package com.dreamsphere.smartdocs.ImageModLibraries;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dreamsphere.smartdocs.R;

public class RectangleView2 extends SubsamplingScaleImageView {

    private PointF sPin;
    Rect rectangle;
    Context context;
    String tag = getClass().getSimpleName();

    public RectangleView2(Context context) {
        this(context, null);
        this.context = context;
    }

    public RectangleView2(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        initialise();
    }

    public void setRectangle(Rect rectangle) {
        Log.d("TAG", "setRectangle: "+rectangle);
        Log.d("TAG", "setRectangle: "+rectangle.left + ","+rectangle.top + ","+rectangle.right + ","+rectangle.bottom);
        this.rectangle = rectangle;
        initialise();
        invalidate();
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
        if (rectangle!=null){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rectangle.left,rectangle.top, rectangle.right,rectangle.bottom, paint);

        }else {
            Log.d("TAG", "onDraw: RECTANGLE IS NULL LMAO");
        }


    }



/*    public void deletePin(int pinNumber){
        Log.d("TAG", "deletePin: "+drawnPins.size());
        Log.d("TAG", "deletePin: "+mapPins.size());
        drawnPins.remove(pinNumber);
        mapPins.remove(pinNumber);
        initialise();
        invalidate();

    }*/
}

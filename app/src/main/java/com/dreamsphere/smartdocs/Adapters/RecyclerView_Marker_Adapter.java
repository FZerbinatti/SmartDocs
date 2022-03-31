package com.dreamsphere.smartdocs.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dreamsphere.smartdocs.Interfaces.RecyclerViewClickListener;
import com.dreamsphere.smartdocs.Models.Marker;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RecyclerView_Marker_Adapter extends RecyclerView.Adapter <RecyclerView_Marker_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<Marker> markersList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    ItemClickListener mClickListener;
    Marker thisMarker;



    public RecyclerView_Marker_Adapter() {
    }

    public RecyclerView_Marker_Adapter(Context context, ArrayList<Marker> markersList) {
        this.context = context;
        this.markersList = markersList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_recyclerview_marker, viewGroup,false);
        thisMarker = new Marker();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String marker_description = markersList.get(i).getDescription();
        Bitmap marker_image = markersList.get(i).getMarker_image();
        Log.d(TAG, "onBindViewHolder: recyclerview image: "+marker_image.getWidth()+"x"+marker_image.getHeight());

        thisMarker.setDescription(marker_description);
        thisMarker.setMarker_image(marker_image);

        viewHolder.textview_marker_description.setText(marker_description);
        viewHolder.textView_number.setText(String.valueOf(i+1));

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.square_blue_box);
                viewHolder.image_marker_photo.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        Glide
                .with(context)
                .load(marker_image)
                .apply(options)
                .into(viewHolder.image_marker_photo);
    }

    @Override
    public int getItemCount() {
        return markersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView image_marker_photo;
        private TextView textview_marker_description;
        private  TextView textView_number;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_marker_photo = (ImageView) itemView.findViewById(R.id.item_picture_marker);
            textview_marker_description = (TextView) itemView.findViewById(R.id.item_description_marker);
            textView_number = (TextView) itemView.findViewById(R.id.textView_number);

        }
    }
    // allows clicks events to be caught
    void setClickListener(RecyclerView_Marker_Adapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}


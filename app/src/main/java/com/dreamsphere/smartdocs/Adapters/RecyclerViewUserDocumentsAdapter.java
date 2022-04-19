package com.dreamsphere.smartdocs.Adapters;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.smartdocs.Models.DocumentDownload;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class RecyclerViewUserDocumentsAdapter extends RecyclerView.Adapter<RecyclerViewUserDocumentsAdapter.ViewHolder> {

    private List<DocumentDownload> userDocuments;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    String CHANNEL_ID = "100";

    // data is passed into the constructor
    public RecyclerViewUserDocumentsAdapter(Context context, List<DocumentDownload> data, String user_company) {
        this.mInflater = LayoutInflater.from(context);
        this.userDocuments = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.document_picker_item, parent, false);

        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DocumentDownload document = userDocuments.get(position);
        holder.myTextView.setText(document.getDocument_name());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return userDocuments.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.item_picker_project_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //download file into download folder
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userID = user.getUid();
                    StorageReference documentReference = storageRef.child(userID+"/"+userDocuments.get(getAdapterPosition()).getDocument_download_name());
                    Log.d("TAG", "onClick: documentReference: "+documentReference);


                    // Create a reference to a file from a Cloud Storage URI
                    FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
                    Log.d("TAG", "onClick: bucket: "+opts.getStorageBucket());
                    StorageReference gsReference = storage.getReferenceFromUrl(documentReference.toString());
                    Log.d("TAG", "onClick: gsReference: "+gsReference);
                    String pdf_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    String pdf_name =userDocuments.get(getAdapterPosition()).getDocument_download_name();
                    File file = new File(pdf_path, pdf_name);


                    File newfile = new File(pdf_path, pdf_name);
                    if (newfile.exists()) {
                        newfile.delete();
                    }
                    gsReference.getFile(file);

                    addNotification(file);


                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return userDocuments.get(id).getDocument_name();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        Log.d("TAG", "setClickListener: dioc");
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void addNotification(File file) {

        // Aggiunge una notifica che al tap porta agli ultimi download del telefono, dove è presente il file generato
        createNotificationChannel();
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

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
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

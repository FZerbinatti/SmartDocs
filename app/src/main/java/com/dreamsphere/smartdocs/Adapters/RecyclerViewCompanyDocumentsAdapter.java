package com.dreamsphere.smartdocs.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.smartdocs.Activities.DocumentsActivity;
import com.dreamsphere.smartdocs.Models.Document;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.dreamsphere.smartdocs.Activities.DocumentsActivity.TAG;

public class RecyclerViewCompanyDocumentsAdapter extends RecyclerView.Adapter<RecyclerViewCompanyDocumentsAdapter.ViewHolder> {

    private List<Document> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private String project_name;
    private Document document;

    // documents is passed into the constructor
    public RecyclerViewCompanyDocumentsAdapter(Context context, List<Document> documents, String project_name, Document document) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = documents;
        this.context = context;
        this.project_name = project_name;
        this.document = document;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.project_picker_item, parent, false);

        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String document_name = mData.get(position).getDocument_name();
        Log.d(TAG, "onBindViewHolder: "+document_name);
        document.setDocument_name(document_name);
        holder.myTextView.setText(document_name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
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

                    //se il documento_nome è uguale al nome Sicurstudio_primosopralluogo allora clicklistenr porta ad aprire quella classe
                }
            });

/*            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.confermare))
                            .setMessage(context.getString(R.string.alert_dialog_1)+document.getDocument_name() +context.getString(R.string.alert_dialog_2)+ project_name +"'' ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //se si aggiungi nel firebase utente il Documento

                                    FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(context.getString(R.string.firebase_user_projects))
                                            .child(project_name)
                                            .child(document.getDocument_name())
                                            .setValue(document);

                                    Intent intent = new Intent(context, DocumentsActivity.class);
                                    intent.putExtra(context.getString(R.string.extra_project_name),project_name);
                                    context.startActivity(intent);

                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.alert_light_frame)
                            .show();
                }
            });*/
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).getDocument_name();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

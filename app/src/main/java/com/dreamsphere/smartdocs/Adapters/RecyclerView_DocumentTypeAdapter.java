package com.dreamsphere.smartdocs.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.smartdocs.Activities.SelectDocumentActivity;
import com.dreamsphere.smartdocs.Interfaces.RecyclerViewClickListener;
import com.dreamsphere.smartdocs.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RecyclerView_DocumentTypeAdapter extends RecyclerView.Adapter <RecyclerView_DocumentTypeAdapter.ViewHolder> {

    private Context context;
    private List<String> projectsList;
    String user_company, project_name;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    ItemClickListener mClickListener;
    String document_type;
    String match_prediction;
    String imageTeamPath;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference teamReference = storage.getReference("team_img");

    public RecyclerView_DocumentTypeAdapter() {
    }

    public RecyclerView_DocumentTypeAdapter(Context context, List<String> displayMatchDetailsList, String user_company, String project_name) {
        this.context = context;
        this.projectsList = displayMatchDetailsList;
        this.user_company = user_company;
        this.project_name = project_name;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.project_picker_item, viewGroup,false);
        document_type = "";

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String item_picker_document_type = projectsList.get(i);
        Log.d(TAG, "onBindViewHolder: item_picker_project_name: "+ item_picker_document_type);

        document_type = item_picker_document_type;

        viewHolder.textview_match_timer.setText(item_picker_document_type);

    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {


        private TextView textview_match_timer;

        private ConstraintLayout cc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            textview_match_timer = (TextView) itemView.findViewById(R.id.item_picker_project_name);

            cc  = (ConstraintLayout) itemView.findViewById(R.id.project_item_cc);



            cc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, SelectDocumentActivity.class);

                    /*String projectName = projectsList.get(getAdapterPosition());
                    Log.d(TAG, "onClick: ééé== "+projectName);*/
                    //Log.d(TAG, "onClick: starting: "+user_company);
                    intent.putExtra( "PROJECT_NAME", project_name );
                    intent.putExtra("USER_COMPANY", user_company);
                    intent.putExtra("DOCUMENT_TYPE", document_type);




                    context.startActivity(intent);

                }
            });


        }




    }


    // allows clicks events to be caught
    void setClickListener(RecyclerView_DocumentTypeAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}


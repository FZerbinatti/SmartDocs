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

import com.dreamsphere.smartdocs.Activities.DocumentsActivity;
import com.dreamsphere.smartdocs.Interfaces.RecyclerViewClickListener;

import com.dreamsphere.smartdocs.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RecyclerView_ProjectAdapter extends RecyclerView.Adapter <RecyclerView_ProjectAdapter.ViewHolder> {

    private Context context;
    private List<String> projectsList;
    String user_company;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    ItemClickListener mClickListener;
    String thisMatch;
    String match_prediction;
    String imageTeamPath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference teamReference = storage.getReference("team_img");

    public RecyclerView_ProjectAdapter() {
    }

    public RecyclerView_ProjectAdapter(Context context, List<String> displayMatchDetailsList, String user_company) {
        this.context = context;
        this.projectsList = displayMatchDetailsList;
        this.user_company = user_company;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.project_picker_item, viewGroup,false);
        thisMatch = "";

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String item_picker_project_name = projectsList.get(i);
        Log.d(TAG, "onBindViewHolder: item_picker_project_name: "+item_picker_project_name);

        thisMatch = item_picker_project_name;

        viewHolder.textview_match_timer.setText(item_picker_project_name);

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

                    Intent intent = new Intent(context, DocumentsActivity.class);

                    String projectName = projectsList.get(getAdapterPosition());
                    Log.d(TAG, "onClick: ééé== "+projectName);

                    intent.putExtra( "PROJECT_NAME", projectName );
                    /*if (!user_company.equals("null")){
                        intent.putExtra("USER_COMPANY", user_company);
                        intent.putExtra("DOCUMENT_TYPE", thisMatch);
                    }*/

                    context.startActivity(intent);

                }
            });


        }




    }


    // allows clicks events to be caught
    void setClickListener(RecyclerView_ProjectAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}


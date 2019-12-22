package com.example.magisterka;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> implements Serializable  {

    public Context context;
    public List<Label> labelList;
    private DBHelper myDb;
    public Intent intent;

    public LabelAdapter(Context context, List<Label> labelList, DBHelper myDb) {
        this.context = context;
        this.labelList = labelList;
        this.myDb = myDb;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.label, parent, false);
        LabelViewHolder pvh = new LabelViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LabelAdapter.LabelViewHolder holder, int position) {

        Label l = labelList.get(position);
        holder.name.setText(l.getName());
        //holder.image.setText(String.valueOf(p.getQuantity()));
        int val = 1;
        holder.toDelete.setChecked(l.getToDelete());
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox toDelete;
        TextView name;
        Button choose;
        Intent intent;


        public LabelViewHolder(@NonNull final View itemView) {
            super(itemView);
            toDelete = itemView.findViewById(R.id.checkBox);
            name = itemView.findViewById(R.id.name);
            choose = itemView.findViewById(R.id.labelChoose);
            intent = new Intent(itemView.getContext(), MainActivity.class);

            itemView.setOnClickListener(this);   //???????

            toDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(b)
                        myDb.setToDelete(1, name.getText().toString());
                    else
                        myDb.setToDelete(0, name.getText().toString());
                }
            });

            choose.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDb.setChoosen(1, name.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });



        }

        @Override
        public void onClick(View view) {
            ////
        }
    }
}

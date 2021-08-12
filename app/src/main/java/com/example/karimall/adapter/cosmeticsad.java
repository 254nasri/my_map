package com.example.karimall.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.karimall.R;
import com.example.karimall.sellhelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class cosmeticsad extends RecyclerView.Adapter<cosmeticsad.ViewHolder> {
    private Context mcontext;
    Integer counts;
    private List<sellhelper> items;
    public cosmeticsad(Context context, List<sellhelper> items) {
        this.items=items;
        this.mcontext=context;
    }
    @NonNull
    @Override
    public cosmeticsad.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.activity_cosmeticsad,parent,false);
        return new cosmeticsad.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cosmeticsad.ViewHolder holder, int position) {
        final sellhelper item=items.get(position);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Items");
        holder.name.setText(item.getName());
        holder.cost.setText("Ksh "+item.getCost());
        Glide.with(mcontext).load(item.getImg()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,cost;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.itemnam);
            cost=itemView.findViewById(R.id.itemcos);
            img=itemView.findViewById(R.id.itemimg);
        }

    }
}

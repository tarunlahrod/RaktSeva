package com.example.raktseva;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    List<UserProfile> usersList;
    Context context;

    public RecyclerViewAdapter(List<UserProfile> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_rv_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.rvtv_userName.setText(usersList.get(position).getName());
        holder.rvtv_userPhoneNumber.setText(usersList.get(position).getPhoneNumber());
        holder.rvtv_userBloodGroup.setText(usersList.get(position).getBloodGroup());
        holder.rtrv_userState.setText(usersList.get(position).getState());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send the control to UserProfileActivity
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userPhoneNumber", usersList.get(position).getPhoneNumber());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView rvtv_userName;
        TextView rvtv_userPhoneNumber;
        TextView rvtv_userBloodGroup;
        TextView rtrv_userState;
        CardView parentLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rvtv_userName = itemView.findViewById(R.id.rvtv_userName);
            rvtv_userPhoneNumber = itemView.findViewById(R.id.rvtv_userPhoneNumber);
            rvtv_userBloodGroup = itemView.findViewById(R.id.rvtv_userBloodGroup);
            rtrv_userState = itemView.findViewById(R.id.rvtv_userState);
            parentLayout = itemView.findViewById(R.id.cardview_user_rv_row);
        }
    }
}

package com.example.android.scanfest.Models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.scanfest.AddChildActivity;
import com.example.android.scanfest.InfoActivity;
import com.example.android.scanfest.R;
import com.example.android.scanfest.ScanActivity;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private Context context;
    private List<Child>childList;

    public ChildAdapter(Context context, List<Child>childList){
        this.context = context;
        this.childList = childList;

    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_child,parent,false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.stName.setText(child.nameOfStudent);
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder{
        TextView stName;
        ImageView deleteSt,stInfo,stScan;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            stName = itemView.findViewById(R.id.child_name);
            deleteSt = itemView.findViewById(R.id.deleteChild);
            stInfo = itemView.findViewById(R.id.scanInfo);
            stScan = itemView.findViewById(R.id.barcodeScan);

            deleteSt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                            .setTitle("Delete Student")
                            .setMessage("Are You sure you want to delete the student")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            });

            stScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Child selectedChild = childList.get(position);
                    Intent intent = new Intent(view.getContext(),ScanActivity.class);
                    intent.putExtra("ChildKey",selectedChild);
                    view.getContext().startActivity(intent);
                }
            });

            stInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Child selectedKey = childList.get(position);
                    Intent intent = new Intent(view.getContext(), InfoActivity.class);
                    intent.putExtra("Key",selectedKey);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}

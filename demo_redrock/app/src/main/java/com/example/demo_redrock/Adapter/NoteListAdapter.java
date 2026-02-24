package com.example.demo_redrock.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_redrock.Infomation.NoteInfo;
import com.example.demo_redrock.R;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.myHolder> {
    List<NoteInfo> mNoteInfoList =new ArrayList<>();

    public List<NoteInfo> getmNoteInfoList() {
        return mNoteInfoList;
    }

    public void setmNoteInfoList(List<NoteInfo> mNoteInfoList) {
        this.mNoteInfoList = mNoteInfoList;
        notifyDataSetChanged();  //刷新适配器
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new myHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull myHolder holder, @SuppressLint("RecyclerView") int position) {
        NoteInfo noteInfo = mNoteInfoList.get(position);
        //设置数据
        holder.tv_date.setText(noteInfo.getNote_create_time());
        holder.tv_title.setText(noteInfo.getNote_title());
        holder.tv_content.setText(noteInfo.getNote_content());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monItemClickistener!=null){
                    monItemClickistener.onItemClick(position,noteInfo);
                }
            }
        });

        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monItemClickistener!=null){
                    monItemClickistener.onMore(position,noteInfo);
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return mNoteInfoList.size();
    }

    static  class myHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_content;
        TextView tv_date;
        ImageView iv_more;


        public myHolder(@NonNull View itemView) {
            super(itemView);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_content=itemView.findViewById(R.id.tv_content);
            tv_date=itemView.findViewById(R.id.tv_date);
            iv_more=itemView.findViewById(R.id.iv_more);
        }
    }

    //详情页回调


    private onItemClickistener monItemClickistener;

    public onItemClickistener getMonItemClickistener() {
        return monItemClickistener;
    }

    public void setMonItemClickistener(onItemClickistener monItemClickistener) {
        this.monItemClickistener = monItemClickistener;
    }

    public interface onItemClickistener{
        void onItemClick(int position,NoteInfo noteInfo);

        void onMore(int position,NoteInfo noteInfo);
    }

}

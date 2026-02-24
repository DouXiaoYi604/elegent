package com.example.demo_redrock.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_redrock.Adapter.NoteListAdapter;
import com.example.demo_redrock.CreateNewNote;
import com.example.demo_redrock.DataBase.NoteDbHelper;
import com.example.demo_redrock.Infomation.NoteInfo;
import com.example.demo_redrock.Infomation.UserInfo;
import com.example.demo_redrock.NoteMainActivity;
import com.example.demo_redrock.R;

import java.util.List;

public class MainFragment extends Fragment {
    private View createView;
    private RecyclerView myRecycleView;
    private NoteListAdapter myListAdapter;
    private String[] item1 = new String[]{"编辑", "删除"};
    private int currentIndex = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createView = inflater.inflate(R.layout.fragment_main, container, false);

        initView();

        myListAdapter = new NoteListAdapter();
        myRecycleView.setAdapter(myListAdapter);


        setListener();
        initData();

        // rv点击事件
        myListAdapter.setMonItemClickistener(new NoteListAdapter.onItemClickistener() {
            @Override
            public void onItemClick(int position, NoteInfo noteInfo) {
                // 空值防护
                if (noteInfo == null || getActivity() == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), NoteMainActivity.class);
                intent.putExtra("noteInfo", noteInfo);
                startActivity(intent);
            }

            @Override
            public void onMore(int position, NoteInfo noteInfo) {

                if (getActivity() == null || noteInfo == null) {
                    return;
                }

                new AlertDialog.Builder(getActivity()).setTitle("操作")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        })
                        .setSingleChoiceItems(item1, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentIndex = which;
                            }
                        })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (currentIndex == 1) {
                                    int row = NoteDbHelper.getInstance(getActivity()).deleteNoteById(noteInfo.getNote_id());
                                    if (row > 0) {
                                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                        initData(); // 删除后刷新数据
                                    } else {
                                        Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (currentIndex == 0) {
                                    Intent intent = new Intent(getActivity(), CreateNewNote.class);
                                    intent.putExtra("noteInfo", noteInfo);

                                    startActivityForResult(intent, 1000);

                                }
                                // 确认操作后关闭弹窗
                                dialog.dismiss();

                            }
                        })
                        .show();
            }
        });
        return createView;
    }

    private void initData() {
        // 空值防护
        UserInfo userInfo = UserInfo.getUserInfo();
        if (userInfo == null || userInfo.getUsername() == null || getActivity() == null) {
            return;
        }

        //
        List<NoteInfo> noteInfos = NoteDbHelper.getInstance(getActivity()).queryNoteListData(userInfo.getUsername());
        if (myListAdapter != null) {
            myListAdapter.setmNoteInfoList(noteInfos);
            myListAdapter.notifyDataSetChanged();
        }


    }

    private void setListener() {
        createView.findViewById(R.id.btn_new_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateNewNote.class);
                startActivityForResult(intent, 1000);
            }
        });
    }

    private void initView() {
        myRecycleView = createView.findViewById(R.id.rv);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            initData(); // 新建 编辑后刷新列表
        }
    }
}
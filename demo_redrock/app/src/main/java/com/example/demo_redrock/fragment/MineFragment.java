package com.example.demo_redrock.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_redrock.AboutApp;
import com.example.demo_redrock.ChangePassword;
import com.example.demo_redrock.Infomation.UserInfo;
import com.example.demo_redrock.LoginActivity;
import com.example.demo_redrock.R;
import com.example.demo_redrock.UseHelp;


public class MineFragment extends Fragment {

    private TextView tv_username;
    private TextView tv_nickname;
    private View rootView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_mine, container, false);


        initView();    //这里初始化的顺序不能变
        initData();
        setListener();
        return rootView;
    }



    private void setListener() {

        //修改密码点击事件
        rootView.findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ChangePassword.class);
                startActivityForResult(intent,2000);
            }
        });


        //使用帮助点击事件
        rootView.findViewById(R.id.tv_use_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), UseHelp.class);
                startActivity(intent);

            }
        });

        //关于app点击事件
        rootView.findViewById(R.id.tv_about).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(getActivity(), AboutApp.class);
               startActivity(intent);
           }
       });

        //确定退出登录点击事件
      rootView.findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("确定要退出登录吗？")
                      .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                          }
                      })
                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              Intent intent=new Intent(getActivity(),LoginActivity.class);
                              startActivity(intent);
                              Toast.makeText(getActivity(),"退出登录成功\n请重新登录",Toast.LENGTH_SHORT).show();
                              getActivity().finish();
                          }
                      }).show();
          }
      });



    }





    private int getLayoutResId(){
        return R.layout.fragment_mine;
    }




    private void initView(){
        tv_username=rootView.findViewById(R.id.tv_username);
        tv_nickname=rootView.findViewById(R.id.tv_nickname);

    }

    private void initData(){
        UserInfo userInfo=UserInfo.getUserInfo();
        if (userInfo!=null){
            tv_username.setText(userInfo.getUsername());
            tv_nickname.setText(userInfo.getNickname());

        }
    }




    //回传
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);







        //修改密码的回调
        if (requestCode==2000) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }



    }
    }





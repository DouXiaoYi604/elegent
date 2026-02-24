package com.example.demo_redrock;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_redrock.DataBase.UserDbHelper;
import com.example.demo_redrock.Infomation.UserInfo;

import kotlinx.coroutines.ChildHandleNode;

public class ChangePassword extends AppCompatActivity {


    private EditText et_change_password;
    private EditText et_confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        setListener();
        initData();


    }

    private void initData() {
    }

    private void setListener() {
        // 返回
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_password=et_change_password.getText().toString().trim();
                String confirm_password=et_confirm_password.getText().toString().trim();

                if (TextUtils.isEmpty(new_password)||TextUtils.isEmpty(confirm_password)){
                    Toast.makeText(ChangePassword.this,"请输入新密码或确认新密码",Toast.LENGTH_SHORT).show();
                }
                else if (!new_password.equals(confirm_password)){
                    Toast.makeText(ChangePassword.this,"新密码与确认密码不一致",Toast.LENGTH_SHORT).show();
                }
                else {
                    UserInfo userInfo=UserInfo.getUserInfo();
                    if (userInfo!=null){
                        int row = UserDbHelper.getInstance(ChangePassword.this).changePassword(userInfo.getUsername(),new_password);

                        if (row>0){
                            Toast.makeText(ChangePassword.this,"密码修改成功",Toast.LENGTH_SHORT).show();

                            setResult(2000);
                            finish();
                        }
                        else {
                            Toast.makeText(ChangePassword.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

    }

    private void initView() {
        et_change_password=findViewById(R.id.et_change_password);
        et_confirm_password=findViewById(R.id.et_confirm_password);

    }


}
package com.example.demo_redrock;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demo_redrock.DataBase.UserDbHelper;
import com.example.demo_redrock.Response.DataResponse;
import com.example.demo_redrock.Tools.OkHttpUtil;
import com.google.gson.Gson;

// 注册页面：无HashMap，直接传参数
public class RegisterActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    private EditText et_repassword;
    private static final String REGISTER_URL = "https://www.wanandroid.com/user/register";
    private Gson gson;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        setListener();
        gson=new Gson();
        mainHandler=new Handler(Looper.getMainLooper());
    }

    // 绑定控件ID
    private void initView() {
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        et_repassword=findViewById(R.id.et_repassword);
    }

    // 设置点击事件
    private void setListener(){
        // 返回按钮点击事件
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 注册点击事件
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=et_username.getText().toString().trim();
                String password=et_password.getText().toString().trim();
                String repassword=et_repassword.getText().toString().trim();

                // 空值校验
                if (TextUtils.isEmpty(username)| TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 密码一致性校验
                if (!password.equals(repassword)){
                    Toast.makeText(RegisterActivity.this,"密码不一致",Toast.LENGTH_SHORT).show();
                    et_repassword.requestFocus();
                    return;
                }

                // 发起注册请求，获取注册单例，调用post方法
                OkHttpUtil.getInstance().doRegisterPost(REGISTER_URL,username,password,repassword,new OkHttpUtil.HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DataResponse response=gson.fromJson(result, DataResponse.class);
                                if (response!=null&&response.getErrorCode()==0) {
                                    // 同步到本地数据库
                                    new Thread(new Runnable(){
                                        @Override
                                        public void run(){
                                            UserDbHelper.getInstance(RegisterActivity.this).register(username,password,"无");
                                        }
                                    }).start();

                                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else {
                                    localRegister(username, password,"注册失败");
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run(){
                                localRegister(username, password, "网络错误");
                            }
                        });
                    }
                });
            }
        });
    }

    // 本地注册兜底
    private void localRegister(String username,String password,String tip) {
        Toast.makeText(this,tip,Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int row = UserDbHelper.getInstance(RegisterActivity.this).register(username, password, "无");
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (row>0) {
                            Toast.makeText(RegisterActivity.this, "本地注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}
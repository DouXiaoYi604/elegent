package com.example.demo_redrock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_redrock.DataBase.UserDbHelper;
import com.example.demo_redrock.Infomation.UserInfo;
import com.example.demo_redrock.Response.DataResponse;
import com.example.demo_redrock.Tools.OkHttpUtil;
import com.google.gson.Gson;

// 登录页面：无HashMap，直接传参数
public class LoginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    private CheckBox checkBox;
    private boolean check_login;
    private SharedPreferences sp;
    private static final String LOGIN_URL = "https://www.wanandroid.com/user/login";
    private Gson gson;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        initView();
        setListener();
        //勾选保存记住账号密码，使用私有保存
        sp=getSharedPreferences("user_info",MODE_PRIVATE);
        initData();
        gson=new Gson();
        mainHandler=new Handler(Looper.getMainLooper());
    }

    // 恢复记住的账号密码
    private void initData() {

        //确定是否勾选记住密码，false自动不勾选
        check_login=sp.getBoolean("check_login", false);
        //获取输入的账户密码
        String username=sp.getString("username", "");
        String password=sp.getString("password", "");

        //如果勾选自动填入获取的账号密码a
        if (check_login) {
            et_username.setText(username);
            et_password.setText(password);
            checkBox.setChecked(true);
        }
    }

    // 绑定控件
    private void initView() {
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        checkBox=findViewById(R.id.checkbox);
    }

    // 设置监听
    private void setListener() {
        // 登录跳注册页面
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // 登录点击世家
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入 的账号 密码
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入账号密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 发起登录请求，获取登录单例，调用post   接口 用户名 密码 回调
                OkHttpUtil.getInstance().doLoginPost(LOGIN_URL, username, password, new OkHttpUtil.HttpCallback() {
                    @Override
                    public void onSuccess(String result) {

                        //切入主线程执行
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //gosn解析json为dataresponse
                                    DataResponse res=gson.fromJson(result,DataResponse.class);

                                    //判断登录是否成功 code为0就成功
                                    if (res!=null&&res.getErrorCode()==0) {
                                        // 同步到本地数据库，这里只能在子线程
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                //将成功登录注册的账号密码存到sql
                                                UserDbHelper.getInstance(LoginActivity.this).register(username, password, "");
                                            }
                                        }).start();

                                        // 保存记住密码
                                        SharedPreferences.Editor edit=sp.edit();
                                        edit.putString("username", username);
                                        edit.putString("password", password);
                                        edit.putBoolean("check_login", check_login);
                                        edit.apply();

                                        // 保存用户信息
                                        DataResponse.Data data=res.getData();
                                        if (data!=null) {
                                            //将用户信息存入全局的userinfo类
                                            UserInfo.setUserInfo(new UserInfo(data.getId(), data.getUsername(), password, ""));
                                        }

                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                        //登录成功进入主界面
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                    //如果网络登录失败用本地登录兜底
                                    else {
                                        localLogin(username, password);
                                    }
                                    //如果json解析有问题 用本地登录兜底
                                } catch (Exception e) {
                                    localLogin(username, password);
                                }
                            }
                        });
                    }

                    //如果网络失败就用本地

                    @Override
                    public void onFailure(String errorMsg) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                localLogin(username, password);
                            }
                        });
                    }
                });
            }
        });

        // 复选框监听
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                check_login=isChecked;
            }
        });
    }

    // 本地登录兜底
    private void localLogin(String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInfo info=UserDbHelper.getInstance(LoginActivity.this).login(username);

                //这里一定是主线程，子线程不能操作ui
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        //将信息存入user info
                        UserInfo.setUserInfo(info);
                        //判断密码输入的和用户名的密码是否一致
                        if (info!=null&&info.getPassword().equals(password)) {
                            SharedPreferences.Editor edit=sp.edit();
                            edit.putString("username", username);
                            edit.putString("password", password);
                            edit.putBoolean("check_login", check_login);
                            edit.apply();


                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this, "本地登录成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "账号密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}
package com.example.demo_redrock;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_redrock.DataBase.NoteDbHelper;
import com.example.demo_redrock.Infomation.NoteInfo;
import com.example.demo_redrock.Infomation.UserInfo;

public class CreateNewNote extends AppCompatActivity {
    private EditText et_title;
    private EditText et_content;
    private String currentUsername;
    private Toolbar toolbar;
    private Button btn_submit;
    private NoteInfo noteInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //这里顺序不能变
        initView();
        initData();
        setListener();
    }

    // 绑定控件 初始化用户名
    private void initView() {
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        toolbar = findViewById(R.id.btn_back);
        btn_submit = findViewById(R.id.btn_submit);

        // 初始化当前用户名
        UserInfo userInfo = UserInfo.getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getUsername())) {
            currentUsername = userInfo.getUsername();
        } else {
            Toast.makeText(this, "用户信息异常", Toast.LENGTH_SHORT).show();
            finish(); // 无用户名直接返回
        }

        // 设置Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(noteInfo == null ? "新建记事" : "编辑记事");
        }
    }

    // 加载编辑笔记数据
    private void initData() {
        noteInfo = (NoteInfo) getIntent().getSerializableExtra("noteInfo");
        if (noteInfo != null) {
            toolbar.setTitle("编辑记事");
            btn_submit.setText("保存修改");
            et_title.setText(noteInfo.getNote_title());
            et_content.setText(noteInfo.getNote_content());
        }
    }

    // 设置所有点击监听
    private void setListener() {
        // Toolbar返回按钮监听（点击关闭页面）
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 提交按钮监听（新建/编辑笔记）
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString().trim();
                String content = et_content.getText().toString().trim();

                // 空值校验
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Toast.makeText(CreateNewNote.this, "请输入标题和内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 新建笔记逻辑
                if (noteInfo == null) {
                    int row = NoteDbHelper.getInstance(CreateNewNote.this).createNote(currentUsername, title, content);
                    if (row > 0) {
                        Toast.makeText(CreateNewNote.this, "笔记创建成功", Toast.LENGTH_SHORT).show();
                        setResult(1000); // 通知列表刷新
                    } else {
                        Toast.makeText(CreateNewNote.this, "笔记创建失败", Toast.LENGTH_SHORT).show();
                    }
                }
                // 编辑笔记逻辑
                else {
                    int row = NoteDbHelper.getInstance(CreateNewNote.this).updateNote(noteInfo.getNote_id(), title, content);
                    if (row > 0) {
                        Toast.makeText(CreateNewNote.this, "笔记修改成功", Toast.LENGTH_SHORT).show();
                        setResult(1000); // 通知列表刷新
                    } else {
                        Toast.makeText(CreateNewNote.this, "笔记修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
                finish(); // 操作完成后关闭页面
            }
        });
    }
}
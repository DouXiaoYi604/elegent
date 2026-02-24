package com.example.demo_redrock;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_redrock.Infomation.NoteInfo;

public class NoteMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        setListener();
        initData();


    }
    private TextView tv_note_title;
    private TextView tv_note_content;
    private NoteInfo noteInfo;

    //加载布局
   private int getLayoutResId(){
        return R.layout.activity_note_main;
    }

   private void initView(){
        tv_note_title=findViewById(R.id.tv_title);
        tv_note_content=findViewById(R.id.tv_content);


        //去掉hint
        tv_note_title.setHint("");
        tv_note_content.setHint("");
    }

   private void setListener(){
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        //跳转传值
        noteInfo= (NoteInfo) getIntent().getSerializableExtra("noteInfo");

        if (noteInfo!=null){
            tv_note_title.setText(noteInfo.getNote_title());
            tv_note_content.setText(noteInfo.getNote_content());
        }
    }
}


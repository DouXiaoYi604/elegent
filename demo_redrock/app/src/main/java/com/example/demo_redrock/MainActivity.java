package com.example.demo_redrock;



import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demo_redrock.fragment.MainFragment;
import com.example.demo_redrock.fragment.MineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
public class MainActivity extends AppCompatActivity {

    private MainFragment mMainFragment;
    private MineFragment mMineFragment;

    private BottomNavigationView mBottomNavigationView;
    private TextView tvTitle;




    protected int getLayoutResId(){
        return R.layout.activity_main;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());


        initView();
        setListener();
        initData();
    }


   protected void initView(){
        mBottomNavigationView=findViewById(R.id.bottomNavigationView);

    }

    protected void setListener(){
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId()==R.id.main){
                    selectFragment(0);
                }
                else if (menuItem.getItemId()==R.id.mine){
                    selectFragment(1);
                }
                return true;
            }
        });
    }


   protected void initData(){
        selectFragment(0);

    }


   protected void selectFragment(int position){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        if (position==0){
            if (mMainFragment==null){
                mMainFragment=new MainFragment();
                fragmentTransaction.add(R.id.content,mMainFragment);
            }
            else {
                fragmentTransaction.show(mMainFragment);
            }
        } else if (position==1) {
            if (mMineFragment==null){
                mMineFragment=new MineFragment();
                fragmentTransaction.add(R.id.content,mMineFragment);
            }
            else {
                fragmentTransaction.show(mMineFragment);
            }


        }
        fragmentTransaction.commit();

    }



    protected void hideFragment(FragmentTransaction fragmentTransaction){
        if (mMainFragment!=null){
            fragmentTransaction.hide(mMainFragment);
        }
        if (mMineFragment!=null){
            fragmentTransaction.hide(mMineFragment);
        }


    }




}
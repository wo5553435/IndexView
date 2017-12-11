package com.zhou.sinner.indexnumview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {
    com.git.indexnum.IndexNumView view;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void change(View view){
        if(this.view==null) this.view= (com.git.indexnum.IndexNumView) view;
        this.view.changeText(""+count);
        count+=10;
    }
}

package com.svce.sparrowpro;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class TeamActivity extends AppCompatActivity {

    RelativeLayout cardLayout;
    CardView cardView;
    UserDetails userDetails;
    Bundle bundle,bundle1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Our Team");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();
        cardLayout=findViewById(R.id.cardlayout);
        cardView=findViewById(R.id.card);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Slide slide=new Slide();
                slide.setSlideEdge(Gravity.BOTTOM);
                slide.setDuration(2000);
                TransitionManager.beginDelayedTransition(cardLayout,slide);
                cardView.setVisibility(View.VISIBLE);
            }
        },100);
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        Intent intent=new Intent(TeamActivity.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }
}

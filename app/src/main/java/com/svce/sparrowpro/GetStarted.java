
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
import android.widget.TextView;

public class GetStarted extends AppCompatActivity {

    UserDetails userDetails;
    Bundle bundle,bundle1;
    TextView started,adoptnest;
    String text1;
    RelativeLayout cardLayout;
    CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Get Started");
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
                slide.setSlideEdge(Gravity.TOP);
                slide.setDuration(2000);
                TransitionManager.beginDelayedTransition(cardLayout,slide);
                cardView.setVisibility(View.VISIBLE);
            }
        },100);
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);
        started=findViewById(R.id.started);
        adoptnest=findViewById(R.id.adoptnest);
        text1="Once a common sight in the city, the sparrow is now restricted to just a few pockets due to rapid urbanisation over the past two decades. This bird has long survived alongside human settlements, feeding on grains strewn near our homes from leaking jute sacks & nesting within the crevices found in thatched & tiled roofs.\n" +
                "With the advent of plastic packaging for our grains that were essentially, an important food source was cut off.\n" +
                "Rampant use of pesticides & insect repellents has led to a decline of insect, another major food source of these tiny creatures.  \n" +
                "To top it off, the concrete jungles that our cities have transformed into have deprived the sparrows of ideal nesting grounds.\n" +
                "In recent years, there have been studies that tried to find if the electromagnetic radiation from cellphone towers interfere with the birds’ capacity to navigate thereby limiting its ability to search for food. It was initially done by an environmental science expert team attributes the disappearance of sparrows to the electromagnetic fields and radiation effects created by mobile towers and mobile phones. Though several years have gone, the study is still in the embryo stage and has not yet been proved scientifically authentic. \n" +
                "In the year 2000,Dr Summers-Smith an engineering consultant and former senior scientific adviser to ICI, stated that though there’s no scientific evidence as yet links MTBE directly with house sparrows. He claimed that the connection between sparrow decline and the introduction of unleaded petrol is strong. He further stated that MTBE does not directly affect birds. However, for example, it could reduce the number of insects the sparrows need to feed their chicks which would result in survival havoc for the sparrows to sustain its population in the system in metropolitan cities especially. \n" +
                "While the sparrow is still designated as endangered, its population in cities across the world has seen drastic declines. The Environment Monitoring & Action Initiative (EMAI), along with CARE,  has decided to launch this initiative in order to ensure that the sparrows in Chennai don’t becoming extinct endemically & also to work towards reviving the population. With this goal in mind, we hope to approach this issue in a multi-pronged manner & crucially, involve Chennaiites in this endeavour. Aspects of our approach include:\n" +
                "Phase 1:\n" +
                "1.       Place nest boxes & water bowls in known sparrow locales to promote nesting.\n" +
                "2.       Involve the local population & encourage them to scatter some grains for the birds.\n" +
                "3.       Map existing sparrow population & track nesting in the bird boxes through an interactive app designed for this purpose.\n" +
                "4.       Use the data generated to plan expansion of the initiative into new areas to revive the population.\n" +
                "We shall be installing nest boxes in a gradual phased manner as and when the occupancy rates approach satisfactory levels. A pilot project conducted with around 200 nest boxes, resulted in an almost 70% occupancy rate.  Those wishing to take an active role in the initiative may do so by:\n";
        started.setText(text1);
        text1="The box’s location will be updated in the app & you will be updated if there has been any nesting. With summer fast approaching, a water bowl may also be sponsored to provide for much needed relief for these creatures.\n" +
                "The supporter can also get involved further by assisting with the installation of the nest box and even with data collection. \n";
        adoptnest.setText(text1);

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
        Intent intent=new Intent(GetStarted.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }
}

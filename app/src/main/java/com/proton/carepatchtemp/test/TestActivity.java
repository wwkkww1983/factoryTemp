package com.proton.carepatchtemp.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity {
    private TestAdapter adapter;
    private List<TestBean> datum = new ArrayList<>();
    private RecyclerView recyclerView;
    private Timer mTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initData();
        recyclerView = findViewById(R.id.id_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TestAdapter(this, datum);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.id_start).setOnClickListener(v -> startTimer());
        findViewById(R.id.id_end).setOnClickListener(v -> stopTimer());
    }

    private void initData() {
        datum.add(new TestBean(0));
        datum.add(new TestBean(0));
    }

    private int progress;

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.w("定时器的当前线程: ", Thread.currentThread().getName());
                if (progress == 100) {
                    progress = 0;
                }
                progress++;
                runOnUiThread(() -> {
                    adapter.setProgress(progress, 0);
                    adapter.setProgress(progress, 1);
                });

            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


}

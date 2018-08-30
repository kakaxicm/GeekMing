package com.kakaxicm.geekming.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.barrage.BarrageView;
import com.kakaxicm.geekming.barrage.ClientBarrageAdapter;
import com.kakaxicm.geekming.barrage.ClientBarrageModel;

import java.util.Random;

/**
 * Created by chenming on 2018/8/30
 */
public class BarrageActivity extends BaseActivity {
    BarrageView barrageView;

    public String DANMU[] = {"LGD是不可战胜的", "你气不气", "这波不亏666", "FY GOD！！！！", "三个杀一个，被反杀，会不会玩?", "天火!天火!", "大巴黎 咚咚咚！", "好秀！"};
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrage);
        barrageView = findViewById(R.id.barrage_view);
        random = new Random();
        barrageView.setAdapter(new ClientBarrageAdapter(this));
    }

    public void sendBarrage(View v){
        ClientBarrageModel danmuEntity = new ClientBarrageModel();
        danmuEntity.setContent(DANMU[random.nextInt(8)]);
        danmuEntity.setTextColor(Color.YELLOW);
        barrageView.addDanmu(danmuEntity);
    }
}

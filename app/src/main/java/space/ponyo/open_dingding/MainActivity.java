package space.ponyo.open_dingding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(view -> startDingtalkApp(this));
    }

    @Override
    protected void onResume() {
        Timer timer = new Timer();
        Context _this = this;
        int delay = (int) (Math.random() * 10);
        btn.setText(String.valueOf(delay) + " 分钟");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startDingtalkApp(_this);
            }
        }, delay * 60 * 1000L);
        super.onResume();
    }

    /**
     * 打开钉钉客户端 并在钉钉客户端打开传入的指定url
     *
     * @param context 安卓上下文环境，推荐Activity Context
     */
    private void startDingtalkApp(Context context) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
        if (intent != null) {
            intent.putExtra("type", "110");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
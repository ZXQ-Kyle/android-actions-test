package space.ponyo.open_dingding;

import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                notifyElectricity();
            } catch (MalformedURLException e) {
                btn.setText(e.toString());
            }
        }
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifyElectricity() throws MalformedURLException {
        BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//        UNKNOWN=1，CHARGING=2，DISCHARGING=3，NOT_CHARGING=4，FULL=5
        int statusInt = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);///充电状态

        String status = "";
        switch (statusInt) {
            case 2: {
                status = "充电中";
                break;
            }
            case 3:
            case 4: {
                status = "未充电";
                break;
            }
            case 5: {
                status = "已充满";
                break;
            }
            default: {
                status = "未知状态";
                break;
            }
        }

        URL url = new URL("http://push.ponyo.space/wecomchan?sendkey=ponyo&&msg_type=text&&msg=充电状态：" + status + "，当前电量：" + capacity);
        new Thread(() -> {
            try {
                // 1.打开 URLConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.getResponseCode();
                runOnUiThread(() -> btn.setText("发送成功"));
            } catch (IOException e) {
                runOnUiThread(() -> btn.setText(e.toString()));
            }
        }).start();
    }
}
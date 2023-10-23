package space.ponyo.open_dingding;

import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btn;
    private ScheduledThreadPoolExecutor mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        mExecutor = new ScheduledThreadPoolExecutor(1);
        mExecutor.scheduleAtFixedRate(() -> {
            BatteryInfoBean batteryInfo = getBatteryInfo();
            System.out.println("mExecutor 执行中 电量信息：" + batteryInfo.toString());
            if (batteryInfo.capacity < 30 && batteryInfo.statusInt != 2) {
                notifyElectricity();
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    @Override
    protected void onDestroy() {
        mExecutor.shutdownNow();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mExecutor.execute(this::notifyElectricity);
        super.onResume();
    }

    private void notifyElectricity() {
        BatteryInfoBean batteryInfo = getBatteryInfo();
        URL url = null;
        try {
            url = new URL("http://push.ponyo.space/wecomchan?sendkey=ponyo&&msg_type=text&&msg=充电状态：" + getStatusStr(batteryInfo.statusInt) + "，当前电量：" + batteryInfo.capacity);
        } catch (MalformedURLException e) {
            runOnUiThread(() -> btn.setText(e.toString()));
            return;
        }
        try {
            // 1.打开 URLConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.getResponseCode();
            runOnUiThread(() -> btn.setText("发送成功"));
        } catch (Exception e) {
            runOnUiThread(() -> btn.setText(e.toString()));
        }
    }

    private BatteryInfoBean getBatteryInfo() {
        BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int statusInt = 1;
//        UNKNOWN=1，CHARGING=2，DISCHARGING=3，NOT_CHARGING=4，FULL=5
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            statusInt = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);///充电状态
        }
        return new BatteryInfoBean(capacity, statusInt);
    }

    private String getStatusStr(int statusInt) {
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
        return status;
    }
}

class BatteryInfoBean {
    public BatteryInfoBean(int capacity, int statusInt) {
        this.capacity = capacity;
        this.statusInt = statusInt;
    }

    int capacity;
    int statusInt;

    @NonNull
    @Override
    public String toString() {
        return "BatteryInfoBean{" + "capacity=" + capacity + ", statusInt=" + statusInt + '}';
    }
}
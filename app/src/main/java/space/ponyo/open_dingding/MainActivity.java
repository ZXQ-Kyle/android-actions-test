package space.ponyo.open_dingding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        startDingtalkApp(this, "");
        super.onResume()
    }

    /**
     * 打开钉钉客户端 并在钉钉客户端打开传入的指定url
     *
     * @param context 安卓上下文环境，推荐Activity Context
     * @param url     需要在钉钉客户端打开的页面地址
     */
    private void startDingtalkApp(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String jumpUrl = "dingtalk://dingtalkclient/page/link?url=" + URLEncoder.encode(url); //必须对url做encode
        Uri uri = Uri.parse(jumpUrl);
        intent.setData(uri);
        if (null != intent.resolveActivity(context.getPackageManager())) {
            context.startActivity(intent);
        }
    }
}
package com.sample.icontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.NetworkCapabilities;
import android.net.TrafficStats;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private TextView tvStart, tvEnd, tvUsage;
    private EditText etPkg;
    private Button btnCommit;

    private int uid = -1;

    private DataUsageTool.Usage startUsage;
    private DataUsageTool.Usage endUsage;
    private DataUsageTool.Usage tempUsage;

    private int networkType = 1;

    private boolean hasStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();

        etPkg.setText("com.huawei.appmarket");

        networkType = NetworkCapabilities.TRANSPORT_WIFI;

        if (!Utils.isAccessGranted(this)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void initView() {
        tvStart = findViewById(R.id.tv_start);
        tvEnd = findViewById(R.id.tv_end);
        tvUsage = findViewById(R.id.tv_usage);
        etPkg = findViewById((R.id.et_pkg));
        btnCommit = findViewById(R.id.btn_commit);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCommit();
            }
        });
    }

    private void onClickCommit() {
        if (!hasStarted) {
            String pkgName = etPkg.getText().toString().trim();
            if (pkgName.isEmpty()) {
                Toast.makeText(this, "请输入包名", Toast.LENGTH_SHORT).show();
                return;
            }
            uid = Utils.getUidByPackage(this, pkgName);
            if (uid == -1) {
                Toast.makeText(this, "未发现应用或异常", Toast.LENGTH_SHORT).show();
                return;
            }
            tvStart.setText("");
            tvEnd.setText("");
            tvUsage.setText("");
            btnCommit.setEnabled(false);
            btnCommit.setText("等待中...");
            catchUsageBytes();
        } else {
            btnCommit.setEnabled(false);
            btnCommit.setText("等待中...");
            catchUsageBytes();
        }
    }


    private void catchUsageBytes() {
        final boolean fromTotal = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 子线程中统计流量
                if (fromTotal) {
                    tempUsage = DataUsageTool.getUsageByUidFromSummary(
                            Main2Activity.this, uid, networkType);
                } else {
                    tempUsage = DataUsageTool.getUsageBytesByUid(Main2Activity.this,
                            0, System.currentTimeMillis(), uid, networkType);
                }
                // 切换到主线程更新数据和UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUsage();
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void updateUsage() {
        if (!hasStarted) {
            hasStarted = true;
            startUsage = tempUsage;
            btnCommit.setEnabled(true);
            btnCommit.setText("结束");
            tvStart.setText(StringUtil.getBytesString(startUsage.rxBytes));
        } else {
            hasStarted = false;
            endUsage = tempUsage;
            btnCommit.setEnabled(true);
            btnCommit.setText("开始");
            tvEnd.setText(StringUtil.getBytesString(endUsage.rxBytes));
            tvUsage.setText(StringUtil.getBytesString(endUsage.rxBytes - startUsage.rxBytes));
        }
    }

}

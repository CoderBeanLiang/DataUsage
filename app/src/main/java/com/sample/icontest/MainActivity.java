package com.sample.icontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvStart, tvEnd, tvUsage;
    private EditText etPkg;
    private Button btnCommit;

    private int uid = -1;

    private long startBytes = 0;
    private long endBytes = 0;

    private boolean hasStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        etPkg.setText("com.huawei.appmarket");

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
            hasStarted = catchStartBytes();
        } else {
            catchEndBytes();
            hasStarted = false;
        }
    }

    private boolean catchStartBytes() {
        String pkgName = etPkg.getText().toString().trim();
        if (pkgName.isEmpty()) {
            Toast.makeText(this, "请输入包名", Toast.LENGTH_SHORT).show();
            return false;
        }
        uid = Utils.getUidByPackage(this, pkgName);
        if (uid == -1) {
            Toast.makeText(this, "未发现应用或异常", Toast.LENGTH_SHORT).show();
            return false;
        }
        startBytes = TrafficStats.getUidRxBytes(uid);
        tvStart.setText(StringUtil.getBytesString(startBytes));
        tvEnd.setText("");
        tvUsage.setText("");
        btnCommit.setText("结束");
        return true;
    }

    private void catchEndBytes() {
        endBytes = TrafficStats.getUidRxBytes(uid);
        long usageBytes = endBytes - startBytes;
        tvEnd.setText(StringUtil.getBytesString(endBytes));
        tvUsage.setText(StringUtil.getBytesString(usageBytes));
        btnCommit.setText("开始");
    }

}

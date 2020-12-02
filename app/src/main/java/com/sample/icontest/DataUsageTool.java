package com.sample.icontest;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.RemoteException;
import android.util.Log;

import static android.content.Context.NETWORK_STATS_SERVICE;

/**
 * @author LiaoLiang
 * @date : 2020/12/2 17:06
 * Android 6.0+   API>=23
 */
public class DataUsageTool {

    public static class Usage {
        long rxBytes;
        long txBytes;
    }


    /**
     * 该方法刷新较慢，统计范围需要拉长
     */
    public static Usage getUsageBytesByUid(Context context, long startTime, long endTime, int uid, int networkType) {
        Usage usage = new Usage();
        usage.rxBytes = 0;
        usage.txBytes = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            assert nsm != null;
            NetworkStats ns = nsm.queryDetailsForUid(networkType, null, startTime, endTime, uid);

            do {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                ns.getNextBucket(bucket);
                usage.rxBytes += bucket.getRxBytes();
                usage.txBytes += bucket.getTxBytes();
            } while (ns.hasNextBucket());
        }
        return usage;
    }



    public static Usage getUsageByUidFromSummary(Context context, int uid, int networkType) {
        long startTime = 0;
        long endTime = System.currentTimeMillis();
        Usage usage = new Usage();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            assert nsm != null;
            NetworkStats ns = null;
            try {
                ns = nsm.querySummary(networkType,null, startTime, endTime);
                do {
                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                    ns.getNextBucket(bucket);
                    if (bucket.getUid() == uid) {
                        usage.rxBytes += bucket.getRxBytes();
                        usage.txBytes += bucket.getTxBytes();
                    }
                }while (ns.hasNextBucket());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return usage;
    }
}

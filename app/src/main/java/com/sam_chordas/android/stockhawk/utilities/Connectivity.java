package com.sam_chordas.android.stockhawk.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by sheshloksamal on 12/07/16.
 */
public class Connectivity {
    /**
     * Get the network info
     */

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     */
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    /**
     * Check if there is any connectivity to Wifi network
     * @param context
     * @return
     */

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to mobile network
     * @param context
     * @return
     */

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is Fast Connectivity
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() &&
                Connectivity.isConnectionFast(networkInfo.getType(), networkInfo.getSubtype()));
    }

    /**
     * Check if the connection is fast
     * @param type    Type of connection, i.e. Wifi/Mobile etc.
     * @param subType subType of connection, for e.g. in case of Mobile, 3G/GPRS/4G etc.
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if(type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE){
            switch(subType) {
                /* Slow Mobile networks, i.e. speed less than 100 kbps*/
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS: //2.5G
                    return false; // ~35 - 171 kbps (100 kbps)
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps

                /* Fast Mobile Networks, i.e. speed at least 100 kbps */
                case TelephonyManager.NETWORK_TYPE_UMTS: //3G
                    return true; // ~400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA: //3.5G
                    return true; // ~700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_LTE: //4G
                    return true; // ~10+ Mbps

                //Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}

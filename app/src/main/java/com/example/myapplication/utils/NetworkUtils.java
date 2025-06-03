package com.example.myapplication.utils;

package com.example.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {

    /**
     * Kiểm tra xem thiết bị có kết nối mạng không
     * @param context Context của ứng dụng
     * @return true nếu có kết nối mạng, false nếu không
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 trở lên
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }

            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network);

            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            // API thấp hơn 23
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * Kiểm tra loại kết nối mạng
     * @param context Context của ứng dụng
     * @return Loại kết nối mạng
     */
    public static NetworkType getNetworkType(Context context) {
        if (context == null) {
            return NetworkType.NONE;
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return NetworkType.NONE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return NetworkType.NONE;
            }

            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network);

            if (networkCapabilities == null) {
                return NetworkType.NONE;
            }

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkType.WIFI;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkType.MOBILE;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return NetworkType.ETHERNET;
            }
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                int type = activeNetworkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    return NetworkType.WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    return NetworkType.MOBILE;
                } else if (type == ConnectivityManager.TYPE_ETHERNET) {
                    return NetworkType.ETHERNET;
                }
            }
        }

        return NetworkType.NONE;
    }

    /**
     * Kiểm tra xem có kết nối WiFi không
     * @param context Context của ứng dụng
     * @return true nếu đang kết nối WiFi
     */
    public static boolean isWifiConnected(Context context) {
        return getNetworkType(context) == NetworkType.WIFI;
    }

    /**
     * Kiểm tra xem có kết nối dữ liệu di động không
     * @param context Context của ứng dụng
     * @return true nếu đang kết nối dữ liệu di động
     */
    public static boolean isMobileConnected(Context context) {
        return getNetworkType(context) == NetworkType.MOBILE;
    }

    /**
     * Enum định nghĩa các loại kết nối mạng
     */
    public enum NetworkType {
        NONE("Không có mạng"),
        WIFI("WiFi"),
        MOBILE("Dữ liệu di động"),
        ETHERNET("Ethernet");

        private final String displayName;

        NetworkType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
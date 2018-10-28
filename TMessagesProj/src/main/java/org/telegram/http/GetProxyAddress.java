package org.telegram.http;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.util.List;

/**
 * Created by sa on 2018/4/11.
 */

public class GetProxyAddress {

    public static final String ProxyAddress = "http://biying.im:443/index.php";
    public static final String ProxyAddressBesa = "http://bying.im:443/index.php";
    public static final String linkPrefixGroup = "http://biying.im:443/group/";
    public static final String linkPrefixGroupTest = "http://ce.biying.im/group/";
    public static final String ProxyTag = "ProxyTag";
    private static boolean isTest = true;

    public static String getDefProxyAddress() {
        SharedPreferences sp = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
        String proxy = sp.getString("ProxyAddress", "");
        Gson gson = new Gson();
        List<String> list = gson.fromJson(proxy, new TypeToken<List<String>>() {
        }.getType());
        if (list == null || list.size() == 0) {
            Log.i("TAG", "走默认代理默认");
            if (isTest)
                return LocaleController.getString("TelegramFindUrl", R.string.TelegramFindUrlTest);
            else
                return LocaleController.getString("TelegramFindUrl", R.string.TelegramFindUrl);
        } else {
            Log.i("TAG", "走默认代理不默认");
            return list.get(0);
        }
    }

    public static String getBaseProxyAddress() {
        SharedPreferences sp = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
        String proxy = sp.getString("ProxyAddress", "");
        Gson gson = new Gson();
        List<String> list = gson.fromJson(proxy, new TypeToken<List<String>>() {
        }.getType());
        if (list == null || list.size() == 0) {
            Log.i("TAG", "走备用代理默认");
            if (isTest)
                return LocaleController.getString("TelegramFindUrl", R.string.TelegramFindUrlTest);
            else
                return LocaleController.getString("TelegramFindUrl", R.string.TelegramFindUrl);
        } else {
            Log.i("TAG", "走备用代理不默认");
            return list.get(1);
        }
    }

    public static void setProxyAddress(List<String> proxy) {
        if (proxy != null && proxy.size() == 2) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            Gson gson = new Gson();
            editor.putString("ProxyAddress", gson.toJson(proxy));
            editor.commit();
        }
    }

    public static void setProxyID(String id) {
        if (id != null && !id.equals("")) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ProxyID", id);
            editor.commit();
        }
    }

    public static String getProxyID() {
        SharedPreferences sp = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
        String proxy = sp.getString("ProxyID", "");
        Log.i("TAG", "proxyId" + proxy);
        return proxy;
    }

    public static String getLinkPrefixGroup() {
        SharedPreferences sp = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
        String LinkPrefixGroup1 = null;
        if (isTest)
            LinkPrefixGroup1=   sp.getString("LinkPrefixGroup", linkPrefixGroupTest);
        else
            LinkPrefixGroup1=  sp.getString("LinkPrefixGroup", linkPrefixGroup);
        Log.i("TAG", "LinkPrefixGroup" + LinkPrefixGroup1);
        return LinkPrefixGroup1;
    }

    public static void setLinkPrefixGroup(String LinkPrefixGroup1) {
        Log.i("setLinkPrefixGroup:", LinkPrefixGroup1);
        if (LinkPrefixGroup1 != null && !LinkPrefixGroup1.equals("")) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LinkPrefixGroup", LinkPrefixGroup1);
            editor.commit();
        } else {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(GetProxyAddress.ProxyTag, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LinkPrefixGroup", linkPrefixGroupTest);
            editor.apply();
        }
        GetProxyAddress.getLinkPrefixGroup();

    }


}

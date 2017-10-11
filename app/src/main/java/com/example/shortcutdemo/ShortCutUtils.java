package com.example.shortcutdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

/**
 * @author: ZhaoYun
 * @date: 2017/9/26
 * @project: ShortCutDemo
 * @detail:
 */
public class ShortCutUtils {

    public static void installShortCut(Activity activity){
        Intent shortCutInstallIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortCutInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME , activity.getString(R.string.app_name));//快捷方式的名称
//        shortCutInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME , "123");//快捷方式的名称

//        shortCutInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE , Intent.ShortcutIconResource.fromContext(activity , R.mipmap.android));

        shortCutInstallIntent.putExtra("duplicate" , false);//不允许重复创建，实测要安卓5.0及5.0以上才有用，4.4及4.4以前的版本估计要用方法遍历
//        Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(activity , R.mipmap.ic_launcher_round);
//        Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(activity , R.mipmap.android);
//        shortCutInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE , iconResource);//快捷方式的图标

        Bitmap icon = BitmapFactory.decodeResource(activity.getResources() ,R.mipmap.android);
        shortCutInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON , icon);//快捷方式的图标

//        //注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
////        ComponentName componentName = new ComponentName(activity.getPackageName(), "." + activity.getLocalClassName());
//        //指定当前App的launcher为快捷方式启动的对象
        ComponentName componentName = new ComponentName(activity , WelcomeActivity.class);
        shortCutInstallIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT ,
                        new Intent(Intent.ACTION_MAIN)
                                .addCategory(Intent.CATEGORY_LAUNCHER)
                                .setComponent(componentName)
                                .setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                                .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)
                );

        activity.sendBroadcast(shortCutInstallIntent);
    }

    /**
     *
     * @param activity
     */
    //6.0及6.0以上无效  5.1及5.1以下有用，5.1及以下不光会比较shortcut_intent进行比较，也会针对快捷方式的名称(EXTRA_SHORTCUT_NAME)进行比对，名称不一样的UnInstall不会生效
    public static void unInstallShortCut(Activity activity){
        Intent shortcutUnInstallIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcutUnInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(R.string.app_name));//快捷方式的名称
//        shortcutUnInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "123");


        shortcutUnInstallIntent.putExtra("duplicate" , false);

        ComponentName componentName = new ComponentName(activity, WelcomeActivity.class);
        shortcutUnInstallIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(componentName));
        activity.sendBroadcast(shortcutUnInstallIntent);
    }

    /**
     判断是否已创建快捷方式
     首先需要获取Launcher的授权(此处理解为系统的Launcher.settings的包名),由于安卓的碎片化，每种机型的Laucher包名可能不与官方的相同，因此用以下方法来获取
     * @param context
     * @param permission
     * @return
     */
    private static String getAuthorityFromPermission(Context context, String permission){
        if (permission == null) return null;
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (provider.readPermission.equals(permission) || provider.writePermission.equals(permission)) return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isAddedShortCut(Context context){
        boolean isAdded = false;
        Cursor c = null;
        try {
            //调用Activity的getContentResolver();
            final ContentResolver cr = context.getContentResolver();
            String authority = getAuthorityFromPermission(context.getApplicationContext(), "com.android.launcher.permission.READ_SETTINGS");
            if(TextUtils.isEmpty(authority)){
                 int sdkInt = android.os.Build.VERSION.SDK_INT;
                 if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                     authority = "com.android.launcher.settings";
                 } else if (sdkInt < 19) {// Android 4.4以下
                     authority = "com.android.launcher2.settings";
                 } else {// 4.4以及以上
                     authority = "com.android.launcher3.settings";
                 }
            }
            if(TextUtils.isEmpty(authority)){
                return isAdded;
            }
            final Uri contentUri = Uri.parse("content://"+authority+"/favorites?notify=true");
            c = cr.query(contentUri , new String[]{"title"} , "title=?" , new String[]{c.getString(R.string.app_name)},null);
            if(c != null && c.getCount()>0){
                isAdded = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c!= null && !c.isClosed()) {
                c.close();
            }
            c = null;
        }
        return isAdded;
    }

}
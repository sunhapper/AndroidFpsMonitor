package me.sunhapper.monitor.fps.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by sunhapper on 2019/3/18 .
 */
public class PermissionUtil {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean isPermissionGranted(Context context, String permissionCode) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            Object[] arrayOfObject = new Object[3];
            arrayOfObject[0] = Integer.valueOf(permissionCode);
            arrayOfObject[1] = Binder.getCallingUid();
            arrayOfObject[2] = context.getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject);
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean checkOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }

    public static int getSupportParamType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0新特性
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }
}



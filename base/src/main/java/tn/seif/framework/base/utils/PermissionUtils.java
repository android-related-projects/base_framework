package tn.seif.framework.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

public final class PermissionUtils {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean allPermissionGranted(Context context, List<String> permissions) {
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static boolean allPermissionResultGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void goToDrawableOverlayPermissionScreen(FragmentActivity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermissions(FragmentActivity activity, List<String> permissions, int permissionRequestCode) {
        activity.requestPermissions(permissions.toArray(new String[0]), permissionRequestCode);
    }
}

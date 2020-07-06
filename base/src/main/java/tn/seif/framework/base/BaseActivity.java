package tn.seif.framework.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import tn.seif.framework.base.annotations.ActivityAnnotations;
import tn.seif.framework.base.utils.PermissionUtils;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 848;
    public static final int REQUEST_CODE = 849;

    protected abstract PermissionsListener getPermissionListener();

    protected abstract PermissionsListener getOverlayPermissionListener();

    protected void beforeView() {
    }

    protected void afterView() {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected abstract List<String> getPermissions();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeView();

        int layoutRes;
        if ((layoutRes = getLayoutRes()) > 0) {
            setContentView(layoutRes);
        }
        setUpViews();
        afterView();
        requestPermissions();
    }

    private @LayoutRes
    int getLayoutRes() {
        try {
            return ActivityAnnotations.Utils.getLayoutId(this.getClass());
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        return -1;
    }

    private void setUpViews() {
        try {
            ActivityAnnotations.Utils.setupViewsById(this);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected void requestPermissions() {
        List<String> permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (permissions = getPermissions()) != null) {
            if (!PermissionUtils.allPermissionGranted(this, permissions)) {
                PermissionUtils.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            } else {
                getPermissionListener().onFinished(true);
            }
        } else {
            getPermissionListener().onFinished(true);
        }
    }

    protected void requestOverlayPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                getOverlayPermissionListener().onFinished(true);
            } else {
                PermissionUtils.goToDrawableOverlayPermissionScreen(this, REQUEST_CODE);
            }
        } else {
            getOverlayPermissionListener().onFinished(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            getPermissionListener().onFinished(
                    grantResults.length > 0 && PermissionUtils.allPermissionResultGranted(grantResults)
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getOverlayPermissionListener().onFinished(Settings.canDrawOverlays(this));
        }
    }

    public interface PermissionsListener {
        void onFinished(boolean allGranted);
    }
}

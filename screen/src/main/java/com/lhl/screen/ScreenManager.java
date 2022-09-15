package com.lhl.screen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;


import com.lhl.screen.inter.BackColor;
import com.lhl.screen.inter.FullScreen;
import com.lhl.screen.inter.InvisibleStatusBar;
import com.lhl.screen.inter.NotScreenShot;
import com.lhl.screen.inter.StatusBarColor;
import com.lhl.screen.inter.StatusBarTextColorBlack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class ScreenManager {
    private static final String TAG = "FramworkManager";
    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    private static int statusBarHeight = -1;
    Application application;
    FramworkActivityLifecycleCallbacks framworkActivityLifecycleCallbacks;
    StatusBarColor color;


    public ScreenManager(Context context) {
        application = (Application) context.getApplicationContext();
        if (application instanceof StatusBarColor)
            color = (StatusBarColor) application;
        framworkActivityLifecycleCallbacks = new FramworkActivityLifecycleCallbacks(this);
        application.registerActivityLifecycleCallbacks(framworkActivityLifecycleCallbacks);
    }

    void setScreen(Activity activity) {
        if (activity == null)
            return;
        setScreen(activity, activity.getWindow());
    }


    void setScreen(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null)
            return;
        setScreen(fragment, activity.getWindow());
    }

    void setScreen(Object target, Window window) {
        if (target == null || window == null)
            return;
        int defaultColor = color != null ? color.statusBarColor() : Color.BLACK;
        if (target instanceof FullScreen) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (!(target instanceof StatusBarColor))
                defaultColor = Color.TRANSPARENT;
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        if (target instanceof NotScreenShot) {
            if ((window.getAttributes().flags & WindowManager.LayoutParams.FLAG_SECURE) != WindowManager.LayoutParams.FLAG_SECURE)
                window.getAttributes().flags |= WindowManager.LayoutParams.FLAG_SECURE;
        } else {
            if ((window.getAttributes().flags & WindowManager.LayoutParams.FLAG_SECURE) == WindowManager.LayoutParams.FLAG_SECURE)
                window.getAttributes().flags &= ~WindowManager.LayoutParams.FLAG_SECURE;
        }
        if (target instanceof StatusBarColor) {
            StatusBarColor statusBarColor = (StatusBarColor) target;
            setStatusBarColor(window, statusBarColor.statusBarColor());
        } else {
            setStatusBarColor(window, defaultColor);
        }
        if (target instanceof InvisibleStatusBar)
            invisibleStatusBar(window);
        if (target instanceof BackColor) {
            BackColor backColor = (BackColor) target;
            window.getDecorView().setBackgroundColor(backColor.backColor());
        }
        if (target instanceof StatusBarTextColorBlack)
            statusBarTextColorBlack(window);
        else
            statusBarTextColorWhite(window);
    }


    public static void statusBarTextColorWhite(Window window) {
        setTextDark(window, false);
    }

    public static void statusBarTextColorBlack(Window window) {
        setTextDark(window, true);
    }

    /**
     * 设置状态栏是否为黑色文字
     *
     * @param window 窗口，可用于Activity和全屏Dialog
     * @param isDark 是否为黑色文字
     */
    private static void setTextDark(Window window, boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (isDark) {
                decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
                setOPPOStatusTextColor(window, isDark);
            } else if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
                setMIUIDark(window, isDark);
            else if (Build.MANUFACTURER.equalsIgnoreCase("Meizu"))
                setFlymeDark(window, isDark);
        }
    }

    /**
     * 设置MIUI系统状态栏是否为黑色文字
     *
     * @param window 窗口，仅可用于Activity
     * @param isDark 是否为黑色文字
     */
    private static void setMIUIDark(Window window, boolean isDark) {
        try {
            Class<? extends Window> clazz = window.getClass();
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, isDark ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Flyme系统状态栏是否为黑色文字
     *
     * @param window 窗口
     * @param isDark 是否为黑色文字
     */
    private static void setFlymeDark(Window window, boolean isDark) {
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void setOPPOStatusTextColor(Window window, boolean isDark) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isDark)
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            else
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isDark)
                vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            else
                vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
        }
        window.getDecorView().setSystemUiVisibility(vis);
    }


    public static void invisibleStatusBar(Window window) {
        int vis = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(vis
                | View.INVISIBLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    public static int getStatusBarHeight() {
        if (statusBarHeight > 0)
            return statusBarHeight;
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier(
                "status_bar_height", "dimen", "android");
        statusBarHeight = resources
                .getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    private static int width = 0;
    private static int height = 0;

    public static int getWindowWidth() {
        if (width > 0)
            return width;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        return width;
    }

    public static int getWindowHeight() {
        if (height > 0)
            return width;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        return height;
    }

    public static void setStatusBarColor(Window window, int color) {
        Context context = window.getContext();
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(color);
        else {
            View statusView = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
            statusView.setLayoutParams(params);
            statusView.setBackgroundColor(color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            decorView.addView(statusView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) decorView.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * dp 的单位 转成为 px(像素)
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        return scale(Resources.getSystem().getDisplayMetrics().density, dpValue);
    }

    /**
     * px(像素) 的单位 转成为 dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        return scale(1 / Resources.getSystem().getDisplayMetrics().density, pxValue);
    }

    /**
     * 像素转为sp
     *
     * @param pxValue
     * @return int
     */
    public static int px2sp(float pxValue) {
        return scale(1 / Resources.getSystem().getDisplayMetrics().scaledDensity, pxValue);
    }

    /**
     * sp转为像素
     *
     * @param spValue
     * @return int
     */
    public static int sp2px(float spValue) {
        return scale(Resources.getSystem().getDisplayMetrics().scaledDensity, spValue);
    }

    public static int scale(float scale, float value) {
        return BigDecimal.valueOf(scale * value).setScale(0, BigDecimal.ROUND_CEILING).intValue();
    }
}

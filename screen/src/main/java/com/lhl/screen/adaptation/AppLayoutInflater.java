package com.lhl.screen.adaptation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;


/**
 * @hide
 */
public class AppLayoutInflater extends LayoutInflater {
    private static final String DESIGN_SCREEN_WIDTH = "design_screen_width";
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };
    private Context mContext;
    private static float scale = -1;

    /**
     * Instead of instantiating directly, you should retrieve an instance
     * through {@link Context#getSystemService}
     *
     * @param context The Context in which in which to find resources and other
     *                application-specific things.
     * @see Context#getSystemService
     */
    public AppLayoutInflater(Context context) {
        super(context);
        init(context);
    }

    protected AppLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
        init(newContext);
    }

    private void init(Context context) {
        mContext = context;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (scale < 0) {
            try {
                int designWidth = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA).metaData.getInt(DESIGN_SCREEN_WIDTH);
                if (designWidth > 0)
                    scale = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f / designWidth;
                else
                    scale = 1;
            } catch (Exception e) {
                scale = 1;
            }
        }
    }

    /**
     * Override onCreateView to instantiate names that correspond to the
     * widgets known to the Widget factory. If we don't find a match,
     * call through to our super class.
     */
    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = null;
        for (String prefix : sClassPrefixList) {
            try {
                view = createView(name, prefix, attrs);
                if (view != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }
        if (view == null)
            view = super.onCreateView(name, attrs);
        if (mContext instanceof CancelAdapt || view == null)
            return view;
        scaleView(view);
        return view;
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        View view = super.inflate(parser, root,attachToRoot);
        scaleRoot(view);
        return view;
    }


    private void scaleRoot(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            if (params.height > 0)
                params.height *= scale;
            if (params.width > 0)
                params.width *= scale;
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) params;
                mlp.leftMargin *= scale;
                mlp.rightMargin *= scale;
                mlp.topMargin *= scale;
                mlp.bottomMargin *= scale;
            }
            view.setLayoutParams(params);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
                scaleRoot(group.getChildAt(i));
        }
    }

    private void scaleView(View view) {
        view.setPadding((int) (view.getPaddingLeft() * scale), (int) (view.getPaddingTop() * scale), (int) (view.getPaddingRight() * scale), (int) (view.getPaddingBottom() * scale));
        if (view instanceof TextView)
            scaleTextView((TextView) view);
    }

    private void scaleTextView(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() * scale);
    }

    public LayoutInflater cloneInContext(Context newContext) {
        return new AppLayoutInflater(this, newContext);
    }
}

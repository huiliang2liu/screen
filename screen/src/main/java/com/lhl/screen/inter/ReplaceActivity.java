package com.lhl.screen.inter;

import android.app.Activity;

public interface ReplaceActivity {
    Class<? extends Activity> onReplaceActivity(Class<? extends Activity> clazz);
}

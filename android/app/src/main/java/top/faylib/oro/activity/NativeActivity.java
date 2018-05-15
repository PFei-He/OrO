package top.faylib.oro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import top.faylib.oro.R;

public class NativeActivity extends Activity {

    //region Views Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        setTitle("Native");
    }

    //endregion


    //region Events Management

    //endregion


    //region Public Methods

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NativeActivity.class);
        activity.startActivity(intent);
    }

    //endregion
}

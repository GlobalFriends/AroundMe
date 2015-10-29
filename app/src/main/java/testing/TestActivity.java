package testing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.globalfriends.com.aroundme.R;

import Logging.Logger;

public class TestActivity extends Activity {
    private static final String TAG = "TestActivity";

    EditText mEditBox;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, ">>>>>>>>> OnCreate");
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mEditBox = (EditText)findViewById(R.id.edit_text);
        mButton = (Button)findViewById(R.id.button_action);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i(TAG, "ojclick");
                Toast.makeText(TestActivity.this, "onCLick", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

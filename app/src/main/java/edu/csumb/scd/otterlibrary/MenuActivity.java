package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {
    private Button bCreateAccount;
    private Button bPlaceHold;
    private Button bCancelHold;
    private Button bManageSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bCreateAccount = (Button)findViewById(R.id.bCreateAccount);
        bPlaceHold = (Button)findViewById(R.id.bPlaceHold);
        bCancelHold = (Button)findViewById(R.id.bCancelHold);
        bManageSystem = (Button)findViewById(R.id.bManageSystem);
    }

    public void launchActivity(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.bCreateAccount:
                intent = new Intent(this, CreateAccount.class);
                break;
            default:
                return;
        }

        startActivity(intent);
    }
}

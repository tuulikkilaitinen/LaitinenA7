package sdccd.edu.laitinena7.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import sdccd.edu.laitinena7.R;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAfterLogin);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



}

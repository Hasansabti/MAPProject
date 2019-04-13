package com.hzstore.mapproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ThankyouActivity extends AppCompatActivity {
TextView msgtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        msgtv = findViewById(R.id.order_finish);
        if(getIntent() !=null){
            String msg = getIntent().getExtras().getString("msg");
            msgtv.setText(msg);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

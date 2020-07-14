package com.solojet.dialup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("About");
        }

        TextView txtDevFull = findViewById(R.id.txtDevFull);
        txtDevFull.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txtPolicy = findViewById(R.id.txtPolicy);
        txtPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instant-codes.web.app/"));
            startActivity(intent);
        });

        TextView txtContact = findViewById(R.id.txtContactFull);
        txtContact.setOnClickListener(view -> {
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setData(Uri.parse("mailto:swiftqube@gmail.com"));
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Dailup Feedback");
            startActivity(Intent.createChooser(sendIntent, "Send via:"));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}

package com.solojet.dialup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import holders.BrandViewHolder;
import models.Brand;
import models.GlideApp;

import static models.Commons.BRANDTAG;
import static models.Commons.CLICKS;
import static models.Commons.INFO;
import static models.Commons.LOGO;
import static models.Commons.NAME;
import static models.Commons.SECTOR;
import static models.Commons.getBrandReference;
import static models.Commons.getDatabase;
import static models.Commons.getNetworkAvailability;
import static models.Commons.getStorage;

public class BrandActivity extends AppCompatActivity {
    String country = "NG";
    private RequestOptions requestOptions = new RequestOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);
        String sector = getIntent().getStringExtra(SECTOR);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(sector.toUpperCase());
        }

        RecyclerView lstBrand = findViewById(R.id.lstBrand);
        lstBrand.setLayoutManager(new LinearLayoutManager(this));

        ((DefaultItemAnimator) lstBrand.getItemAnimator()).setSupportsChangeAnimations(false);

        ImageView imgAlert = findViewById(R.id.imgAlert);
        Query query = getBrandReference(country).orderBy(NAME).whereEqualTo(SECTOR, sector);

        query.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()||task.getResult() == null ||
                    (task.getResult().getDocuments().size()==0 && !getNetworkAvailability(getApplicationContext())))
            {
                imgAlert.setImageResource(R.drawable.badinternet);
                imgAlert.setVisibility(View.VISIBLE);
            }
            else if (task.getResult().getDocuments().size()==0&& getNetworkAvailability(getApplicationContext()))
            {
                imgAlert.setImageResource(R.drawable.smthgwrong);
                imgAlert.setVisibility(View.VISIBLE);
            }
            else
                imgAlert.setVisibility(View.GONE);
        });

        FirestoreRecyclerOptions<Brand> options = new FirestoreRecyclerOptions.Builder<Brand>()
                .setQuery(query, Brand.class)
                .build();

        requestOptions.placeholder(R.drawable.icn);
        BrandAdapter adapter = new BrandAdapter(options);
        lstBrand.setAdapter(adapter);
        adapter.startListening();
        lstBrand.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if(imgAlert.getVisibility()==View.VISIBLE)
                    imgAlert.setVisibility(View.GONE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if(adapter.getItemCount()==0 && imgAlert.getVisibility()== View.GONE) {
                    imgAlert.setImageResource(R.drawable.smthgwrong);
                    imgAlert.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


    private class BrandAdapter extends FirestoreRecyclerAdapter<Brand, BrandViewHolder> {

        BrandAdapter(@NonNull FirestoreRecyclerOptions<Brand> options) {
            super(options);
        }



        @Override
        protected void onBindViewHolder(@NonNull BrandViewHolder holder, int position, @NonNull final Brand model) {
            Log.i("MyTest", "onCreateView: "+ model.getName());
            holder.txtBrand.setText(model.getName());
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(BrandActivity.this, CodeActivity.class);
                intent.putExtra(BRANDTAG, model.getBrandTag());
                intent.putExtra(LOGO, model.getLogo());
                intent.putExtra(INFO, model.getInfo());
                startActivity(intent);
                //Toast.makeText(getContext(), model.getName(), Toast.LENGTH_SHORT).show();

                getDatabase().runTransaction((Transaction.Function<Void>) transaction -> {
                    final DocumentReference brandRef =  getBrandReference(country).document(model.getId());
                    DocumentSnapshot snapshot = transaction.get(brandRef);
                    //check if post still exists
                    if(!snapshot.exists()){
                        Log.i("BrandAct", "apply: brand doesn't exist");
                        return null;
                    }

                    long count = snapshot.contains(CLICKS)? snapshot.getLong(CLICKS): 0;
                    transaction.update(brandRef, CLICKS, count + 1);
                    return null;
                });
            });

            String logo_url = model.getLogo();
            if(logo_url==null || logo_url.isEmpty()|| logo_url.equals("null")|| logo_url.equals("non"))
                Glide.with(BrandActivity.this).setDefaultRequestOptions(requestOptions).load(R.drawable.icn).into(holder.imgLogo);
            else
                GlideApp.with(BrandActivity.this).setDefaultRequestOptions(requestOptions)
                        .load(getStorage().getReferenceFromUrl(logo_url)).into(holder.imgLogo);

        }

        @NonNull
        @Override
        public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
            return new BrandViewHolder(view);
        }
    }
}

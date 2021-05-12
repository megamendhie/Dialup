package com.solojet.dialup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import java.util.List;

import holders.CodeViewHolder;
import models.Code;
import models.Commons;
import models.GlideApp;
import roomdb.DbViewModel;

import static models.Commons.BRANDTAG;
import static models.Commons.CLICKS;
import static models.Commons.CODES;
import static models.Commons.INFO;
import static models.Commons.LOGO;
import static models.Commons.PRIORITY;
import static models.Commons.actionButtonOnClick;
import static models.Commons.copyToClipboard;
import static models.Commons.getCodeReference;
import static models.Commons.getDatabase;
import static models.Commons.getNetworkAvailability;
import static models.Commons.getStorage;

public class CodeActivity extends AppCompatActivity {

    DbViewModel viewModel;
    List<Code> favorites;
    CodeAdapter adapter;

    String logo_url, info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Codes");
        }

        RecyclerView lstCodes = findViewById(R.id.lstCodes);
        lstCodes.setLayoutManager(new LinearLayoutManager(this));

        TextView txtinfo = findViewById(R.id.txtInfo);

        ImageView imgLogo = findViewById(R.id.imgLogo);
        ImageView imgAlert = findViewById(R.id.imgAlert);
        viewModel = new ViewModelProvider(this).get(DbViewModel.class);
        viewModel.getAllFavorite().observe(this, codes -> {
            favorites = codes;
            if(adapter!=null)
                adapter.setFavText();
            });

        String country = "NG";
        String brandTag = getIntent().getStringExtra(BRANDTAG);
        logo_url = getIntent().getStringExtra(LOGO);
        info = getIntent().getStringExtra(INFO);

        if((info!=null) && (!info.isEmpty()))
            txtinfo.setText(info);

        if(logo_url==null || logo_url.isEmpty()|| logo_url.equals("null")|| logo_url.equals("non"))
            Glide.with(CodeActivity.this).load(R.drawable.icn).into(imgLogo);
        else
            GlideApp.with(CodeActivity.this).load(getStorage().getReferenceFromUrl(logo_url)).into(imgLogo);

        Query query = getCodeReference(country).orderBy(PRIORITY, Query.Direction.ASCENDING).whereEqualTo(BRANDTAG, brandTag);

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

        FirestoreRecyclerOptions<Code> options = new FirestoreRecyclerOptions.Builder<Code>()
                .setQuery(query, Code.class)
                .build();
        adapter = new CodeAdapter(options);
        lstCodes.setAdapter(adapter);
        adapter.startListening();

        lstCodes.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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

    private class CodeAdapter extends FirestoreRecyclerAdapter<Code, CodeViewHolder> {
        CodeViewHolder holder;
        Code model;

        CodeAdapter(@NonNull FirestoreRecyclerOptions<Code> options) {
            super(options);
        }

        void setHolder(CodeViewHolder holder, Code model){
            this.holder = holder;
            this.model = model;
        }

        void setFavText(){
            if(holder!=null){
                holder.txtFav.setText(favorites.contains(model)?"Remove favorite":"Add to favorite");
                holder.imgFav.setVisibility(favorites.contains(model)? View.VISIBLE: View.GONE);

                if(favorites.contains(model))
                    holder.txtFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp,0,0,0);
                else
                    holder.txtFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp,0,0,0);

            }
        }

        private void addToLog(Code model) {
            viewModel.addHistory(new models.Log(model));
        }

        void setOptions(Code model, CodeViewHolder holder){
            if(favorites.contains(model))
                holder.txtFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp,0,0,0);
            else
                holder.txtFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp,0,0,0);

            holder.txtFav.setText(favorites.contains(model)?"Remove favorite":"Add to favorite");
            holder.imgFav.setVisibility(favorites.contains(model)? View.VISIBLE: View.GONE);

            holder.txtCopy.setOnClickListener(view -> {
                copyToClipboard(CodeActivity.this, model.getCode());
                Toast.makeText(CodeActivity.this, "copied to clipboard!", Toast.LENGTH_SHORT).show();
            });

            holder.txtFav.setOnClickListener(view -> {
                setHolder(holder, model);
                if(favorites.contains(model)) {
                    viewModel.removeFavoriteById(model.getId());
                    Snackbar.make(holder.txtFav, "Removed from favorite", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    viewModel.addFavorite(model);
                    Snackbar.make(holder.txtFav, "Added to favorite", Snackbar.LENGTH_SHORT).show();
                }
            });

            holder.txtShare.setOnClickListener(view -> {
                Commons.share(model.getCode(), model.getPurpose(), CodeActivity.this);
            });
        }

        @Override
        protected void onBindViewHolder(@NonNull CodeViewHolder holder, int position, @NonNull final Code model) {
            holder.txtCode.setText(model.getCode());
            holder.txtPurpose.setText(model.getPurpose());
            String action = model.getAction();
            switch (action){
                case "call":
                    holder.imgAction.setImageResource(R.drawable.ic_call_black_24dp);
                    break;
                case "sms":
                    holder.imgAction.setImageResource(R.drawable.ic_message_black_24dp);
                    break;
                default:
                    holder.imgAction.setVisibility(View.GONE);
                    break;
            }
            if(logo_url==null || logo_url.isEmpty()|| logo_url.equals("null")|| logo_url.equals("non"))
                Glide.with(CodeActivity.this).load(R.drawable.icn).into(holder.imgLogo);
            else
                GlideApp.with(CodeActivity.this).load(getStorage().getReferenceFromUrl(logo_url)).into(holder.imgLogo);

            setOptions(model, holder);
            holder.itemView.setOnClickListener(view -> holder.toggleOptions());

            holder.imgAction.setOnClickListener(view -> {
                actionButtonOnClick(CodeActivity.this, model);
                if(model.getAction().equals("call")||model.getAction().equals("sms"))
                    addToLog(model);

                getDatabase().runTransaction((Transaction.Function<Void>) transaction -> {
                    final DocumentReference codeRef =  getCodeReference(model.getCountry()).document(model.getId());
                    DocumentSnapshot snapshot = transaction.get(codeRef);
                    //check if post still exists
                    if(!snapshot.exists()){
                        Log.i("CodeAct", "apply: code doesn't exist");
                        return null;
                    }
                    long count = snapshot.contains(CLICKS)? snapshot.getLong(CLICKS): 0;
                    transaction.update(codeRef, CLICKS, count + 1);
                    return null;
                });
            });

            String ref = model.getBrandRef();

            /*
            use this to grab the logo url
            FirebaseFirestore.getInstance().document(model.getBrandRef()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //put clide img
                }
            });

             */
        }

        @NonNull
        @Override
        public CodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_code, parent, false);
            return new CodeViewHolder(view);
        }
    }
}

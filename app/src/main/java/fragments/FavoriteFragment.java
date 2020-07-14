package fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.solojet.dialup.R;

import java.util.List;

import holders.CodeViewHolder;
import models.Code;
import models.Commons;
import models.GlideApp;
import roomdb.DbViewModel;

import static models.Commons.CODES;
import static models.Commons.CLICKS;
import static models.Commons.actionButtonOnClick;
import static models.Commons.copyToClipboard;
import static models.Commons.getDatabase;
import static models.Commons.getStorage;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    private DbViewModel viewModel;
    private RequestOptions requestOptions = new RequestOptions();

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        RecyclerView lstFavorite = view.findViewById(R.id.lstFavorite);
        lstFavorite.setLayoutManager(new LinearLayoutManager(getContext()));

        final FavoriteAdapter adapter = new FavoriteAdapter();
        lstFavorite.setAdapter(adapter);

        requestOptions.placeholder(R.drawable.icn);
        viewModel = new ViewModelProvider(getActivity()).get(DbViewModel.class);
        viewModel.getAllFavorite().observe(getActivity(), codes -> adapter.setFavoriteList(codes));

        return view;
    }


    public class FavoriteAdapter extends RecyclerView.Adapter<CodeViewHolder>{

        private List<Code> favoriteList;

        @NonNull
        @Override
        public CodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_code, parent, false);
            return new CodeViewHolder(view);
        }


        void setOptions(Code model, CodeViewHolder holder){
            holder.txtFav.setText(favoriteList.contains(model)?"Remove favorite":"Add to favorite");

            holder.txtCopy.setOnClickListener(view -> {
                copyToClipboard(getContext(), model.getCode());
                Toast.makeText(getContext(), "copied to clipboard!", Toast.LENGTH_SHORT).show();
            });

            holder.txtFav.setOnClickListener(view -> {
                if(favoriteList.contains(model)) {
                    holder.toggleOptions();
                    viewModel.removeFavoriteById(model.getId());
                    Snackbar.make(holder.txtFav, "Removed from favorite", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    viewModel.addFavorite(model);
                    Snackbar.make(holder.txtFav, "Added to favorite", Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Favorite added", Toast.LENGTH_SHORT).show();
                }
            });

            holder.txtShare.setOnClickListener(view -> {
                Commons.share(model.getCode(), model.getPurpose(), getContext());
            });
        }

        private void addToLog(Code model) {
            viewModel.addHistory(new models.Log(model));
        }

        @Override
        public void onBindViewHolder(@NonNull CodeViewHolder holder, int position) {
            Code model = favoriteList.get(position);
            Log.i("MyTest", "onCreateView: "+ model.getCode());
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

            getDatabase().document(model.getBrandRef()).get().addOnCompleteListener(task -> {
                        if(task.getResult()!=null && task.getResult().contains("logo")){
                            String logo_url = task.getResult().get("logo", String.class);
                            if(logo_url==null || logo_url.isEmpty()|| logo_url.equals("null")|| logo_url.equals("non"))
                                Glide.with(getActivity()).load(R.drawable.icn).into(holder.imgLogo);
                            else
                                GlideApp.with(getActivity()).setDefaultRequestOptions(requestOptions)
                                        .load(getStorage().getReferenceFromUrl(logo_url)).into(holder.imgLogo);
                        }
                        else
                            Glide.with(getActivity()).load(R.drawable.icn).into(holder.imgLogo);
                    });
            setOptions(model, holder);
            holder.itemView.setOnClickListener(view -> holder.toggleOptions());

            holder.imgAction.setOnClickListener(view -> {
                actionButtonOnClick(getContext(), model);
                if(model.getAction().equals("call")||model.getAction().equals("sms"))
                    addToLog(model);

                getDatabase().runTransaction((Transaction.Function<Void>) transaction -> {
                    final DocumentReference codeRef =  getDatabase().collection(model.getCountry()+"_"+CODES)
                            .document(model.getId());
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

        }

        @Override
        public int getItemCount() {
            if (favoriteList != null)
                return favoriteList.size();
            else return 0;
        }

        void setFavoriteList(List<Code> favoriteList) {
            this.favoriteList = favoriteList;
            notifyDataSetChanged();
        }
    }

}
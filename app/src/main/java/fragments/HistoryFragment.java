package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import holders.HeaderViewHolder;
import holders.HistoryViewHolder;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import models.Code;
import models.Commons;
import models.CustomLog;
import models.GlideApp;
import models.Log;
import roomdb.DbViewModel;

import static models.Commons.CODES;
import static models.Commons.CLICKS;
import static models.Commons.LOGO;
import static models.Commons.actionButtonOnClick;
import static models.Commons.copyToClipboard;
import static models.Commons.getDatabase;
import static models.Commons.getStorage;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private DbViewModel viewModel;
    private List<Code> favorites;
    private RequestOptions requestOptions = new RequestOptions();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        RecyclerView lstHistory = view.findViewById(R.id.lstHistory);
        lstHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        requestOptions.placeholder(R.drawable.icn);
        final SectionedRecyclerViewAdapter sectionedAdapter = new SectionedRecyclerViewAdapter(){
            @Override
            public long getItemId(int position){
                return position;
            }

            @Override
            public void setHasStableIds(boolean hasStableIds) {
                super.setHasStableIds(hasStableIds);
            }
        };

        viewModel = new ViewModelProvider(getActivity()).get(DbViewModel.class);

        viewModel.getAllFavorite().observe(getActivity(), codes -> {
            favorites = codes;
            sectionedAdapter.notifyDataSetChanged();
        });

        viewModel.getAllHistory().observe(getActivity(), logs -> {
            lstHistory.setAdapter(sectionedAdapter);
            HashMap<String, List<Log>> logsPerDay = getLogsPerDay(logs);
            HashMap<String, List<CustomLog>> customLogsPerDay = getCustomLogsPerDay(logsPerDay);
            for(Map.Entry log: customLogsPerDay.entrySet()){
                String date = (String) log.getKey();
                List<CustomLog> customLog = (List<CustomLog>) log.getValue();
                Collections.sort(customLog);
                sectionedAdapter.addSection(date, new HistorySection(customLog, date));
            }
            sectionedAdapter.notifyDataSetChanged();
        });
        return view;
    }

    private HashMap<String, List<CustomLog>> getCustomLogsPerDay(HashMap<String, List<Log>> logsPerDay){
        HashMap<String, List<CustomLog>> listCustomHashMap = new HashMap<>();

        for (String date: logsPerDay.keySet()){

            HashMap<String, CustomLog> customLogHashMap = new HashMap<>(); //hasMap of the log's id and body
            List<Log> logs = logsPerDay.get(date); //logs for each day

            assert logs != null;
            for(Log log: logs){
                final String log_id = log.getId();
                if(customLogHashMap.containsKey(log_id)){
                    long initialTime = customLogHashMap.get(log_id).getTime();
                    customLogHashMap.get(log_id).setCount();
                    customLogHashMap.get(log_id).setTime(Math.max(initialTime, log.getTime()));
                }
                else {
                    CustomLog customLog = new CustomLog(log);
                    customLogHashMap.put(log_id, customLog);
                }
            }

            List<CustomLog> customLogs = new ArrayList<>();
            for (String log_id: customLogHashMap.keySet()){
                customLogs.add(customLogHashMap.get(log_id));
            }

            listCustomHashMap.put(date, customLogs);
        }
        return listCustomHashMap;
    }

    private HashMap<String, List<Log>> getLogsPerDay(List<Log> logs) {
        HashMap<String, List<Log>> allLogsPerDay = new HashMap<>();

        for (Log log: logs){
            String date = log.getDate();

            if (allLogsPerDay.containsKey(date)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                allLogsPerDay.get(date).add(log);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<Log> list = new ArrayList<>();
                list.add(log);
                allLogsPerDay.put(date, list);
            }
        }
        return allLogsPerDay;
    }

    class HistorySection extends Section{
        /**
         * Create a Section object based on {@link SectionParameters}.
         *
         * @param sectionParameters section parameters
         */

        List<CustomLog> customLogs;
        String date;

        HistorySection(List<CustomLog> customLogs, String date) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.item_history)
                    .headerResourceId(R.layout.item_header)
                    .build());
            this.customLogs = customLogs;
            this.date = date;
        }

        @Override
        public int getContentItemsTotal() {
            return customLogs==null? 0: customLogs.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new HistoryViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.txtDate.setText(date);
        }

        private void addToLog(Code code) {
            viewModel.addHistory(new models.Log(code));
        }

        void setOptions(CustomLog model, HistoryViewHolder holder){
            Code code = new Code(model);
            holder.imgFav.setVisibility(favorites.contains(code)? View.VISIBLE: View.GONE);
            holder.imgFavorite.setImageResource(favorites.contains(code)? R.drawable.ic_favorite_black_24dp:
                    R.drawable.ic_favorite_border_black_24dp);

            holder.imgCopy.setOnClickListener(view -> {
                copyToClipboard(getContext(), model.getCode());
                Toast.makeText(getContext(), "copied to clipboard!", Toast.LENGTH_SHORT).show();
            });

            holder.imgFavorite.setOnClickListener(view -> {
                if(favorites.contains(code)) {
                    viewModel.removeFavoriteById(code.getId());
                    Snackbar.make(holder.imgFavorite, "Removed from favorite", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    viewModel.addFavorite(code);
                    Snackbar.make(holder.imgFavorite, "Added to favorite", Snackbar.LENGTH_SHORT).show();
                }
            });

            holder.imgShare.setOnClickListener(view -> {
                Commons.share(model.getCode(), model.getPurpose(), getContext());
            });

            holder.imgAction.setOnClickListener(view -> {
                actionButtonOnClick(getContext(), code);
                if(model.getAction().equals("call")||model.getAction().equals("sms"))
                    addToLog(code);

                getDatabase().runTransaction((Transaction.Function<Void>) transaction -> {
                    final DocumentReference codeRef =  getDatabase().collection(model.getCountry()+"_"+CODES)
                            .document(model.getId());
                    DocumentSnapshot snapshot = transaction.get(codeRef);
                    //check if post still exists
                    if(!snapshot.exists()){
                        android.util.Log.i("CodeAct", "apply: code doesn't exist");
                        return null;
                    }

                    long count = snapshot.contains(CLICKS)? snapshot.getLong(CLICKS): 0;
                    transaction.update(codeRef, CLICKS, count + 1);
                    return null;
                });
            });
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomLog model = customLogs.get(position);
            HistoryViewHolder viewHolder = (HistoryViewHolder) holder;

            if(customLogs.size()==1)
                viewHolder.itemView.setBackgroundResource(R.drawable.curved_bottom_top_bg);
            if(customLogs.size()>1 && position == 0)
                viewHolder.itemView.setBackgroundResource(R.drawable.curved_top_bg);
            if(customLogs.size()>1 && position == customLogs.size()-1)
                viewHolder.itemView.setBackgroundResource(R.drawable.curved_bottom_bg);

            getDatabase().document(model.getBrandRef()).get().addOnCompleteListener(task -> {
                        if(task.getResult()!=null && task.getResult().contains(LOGO)){
                            String logo_url = task.getResult().get(LOGO, String.class);
                            if(logo_url==null || logo_url.isEmpty()|| logo_url.equals("null")|| logo_url.equals("non"))
                                Glide.with(viewHolder.imgLogo).load(R.drawable.icn).into(viewHolder.imgLogo);
                            else
                                GlideApp.with(viewHolder.imgLogo).setDefaultRequestOptions(requestOptions)
                                        .load(getStorage().getReferenceFromUrl(logo_url)).into(viewHolder.imgLogo);
                        }
                        else
                            Glide.with(viewHolder.imgLogo).load(R.drawable.icn).into(viewHolder.imgLogo);
                    });

            String time = DateFormat.format("h:mm a", model.getTime()).toString();
            viewHolder.txtPurpose.setText(model.getCount()>1?model.getPurpose()+" ("+model.getCount()+")" : model.getPurpose());
            viewHolder.txtCode.setText(model.getCode());
            viewHolder.txtTime.setText(time);

            setOptions(model, viewHolder);
            viewHolder.itemView.setOnClickListener(view -> viewHolder.toggleOptions());
        }
    }
}
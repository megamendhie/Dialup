package holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solojet.dialup.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgLogo;
    public TextView txtCode, txtPurpose, txtTime;
    private LinearLayout lnrOptions;
    public ImageView imgCopy, imgFav, imgFavorite, imgShare, imgAction;
    private boolean optionsVisible = false;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        imgLogo = itemView.findViewById(R.id.imgLogo);

        imgAction = itemView.findViewById(R.id.imgAction);
        imgFav = itemView.findViewById(R.id.imgFav);
        imgFavorite = itemView.findViewById(R.id.imgFavorite);
        imgShare = itemView.findViewById(R.id.imgShare);
        imgCopy = itemView.findViewById(R.id.imgCopy);

        txtCode = itemView.findViewById(R.id.txtCode);
        txtPurpose = itemView.findViewById(R.id.txtPurpose);
        txtTime = itemView.findViewById(R.id.txtTime);
        lnrOptions = itemView.findViewById(R.id.lnrOptions);
    }
     public void toggleOptions(){
         lnrOptions.setVisibility(optionsVisible? View.GONE: View.VISIBLE);
         optionsVisible = !optionsVisible;
     }


}

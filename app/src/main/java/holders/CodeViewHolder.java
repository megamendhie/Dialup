package holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solojet.dialup.R;

public class CodeViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgLogo, imgAction, imgFav;
    public TextView txtCode, txtPurpose;
    private LinearLayout lnrOptions;
    public TextView txtCopy, txtFav, txtShare;
    private boolean optionsVisible = false;

    public CodeViewHolder(@NonNull View itemView) {
        super(itemView);

        imgLogo = itemView.findViewById(R.id.imgLogo);
        imgAction = itemView.findViewById(R.id.imgAction);
        imgFav = itemView.findViewById(R.id.imgFav);
        txtCode = itemView.findViewById(R.id.txtCode);
        txtPurpose = itemView.findViewById(R.id.txtPurpose);

        txtCopy = itemView.findViewById(R.id.txtCopy);
        txtFav = itemView.findViewById(R.id.txtFavorite);
        txtShare = itemView.findViewById(R.id.txtShare);
        lnrOptions = itemView.findViewById(R.id.lnrOptions);
    }
     public void toggleOptions(){
         lnrOptions.setVisibility(optionsVisible? View.GONE: View.VISIBLE);
         optionsVisible = !optionsVisible;
     }


}

package holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solojet.dialup.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView txtDate;
    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtDate = itemView.findViewById(R.id.txtDate);
    }
}

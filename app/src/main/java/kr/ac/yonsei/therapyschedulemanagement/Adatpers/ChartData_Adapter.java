package kr.ac.yonsei.therapyschedulemanagement.Adatpers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.Chart_CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class ChartData_Adapter extends RecyclerView.Adapter<ChartData_Adapter.CustomViewHolder> {

    private ArrayList<Chart_CardItem> cardItems;
    ChartData_Adapter.OnItemClickedListener listener;


    public static interface OnItemClickedListener {
        public void onItemClick(CalendarDaySchdule_Adapter.CustomViewHolder holder, View view, int position);

        void onDeleteButtonClick(int position);
    }

    public ChartData_Adapter(ArrayList<Chart_CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    @NonNull
    @Override
    public ChartData_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_list, parent, false);
        ChartData_Adapter.CustomViewHolder customViewHolder = new ChartData_Adapter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(ChartData_Adapter.CustomViewHolder holder, int position) {
        holder.txt_date.setText(cardItems.get(position).getChartDate());
        holder.txt_status.setText(cardItems.get(position).getChartStatus());
        holder.txt_contents.setText(cardItems.get(position).getChartContents());

    }
    @Override
    public int getItemCount() {
        return (null != cardItems ? cardItems.size() : 0);
    }

    public void remove(int pos) {
        try {
            cardItems.remove(pos);
            notifyItemRemoved(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_date;
        private TextView txt_status;
        private TextView txt_contents;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_date = itemView.findViewById(R.id.txt_chart_date);
            this.txt_status = itemView.findViewById(R.id.txt_chart_status);
            this.txt_contents = itemView.findViewById(R.id.txt_chart_contents);

        }
    }
}
package kr.ac.yonsei.therapyschedulemanagement.Adatpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.yonsei.therapyschedulemanagement.HomeMonth_CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class HomeMonthSchedule_Adapter extends RecyclerView.Adapter<HomeMonthSchedule_Adapter.CustomViewHolder> {

    private ArrayList<HomeMonth_CardItem> cardItemsMonth;

    public HomeMonthSchedule_Adapter(ArrayList<HomeMonth_CardItem> cardItems) {
        this.cardItemsMonth = cardItems;
    }

    @NonNull
    @Override
    public HomeMonthSchedule_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_month_schedule, parent,  false);
        HomeMonthSchedule_Adapter.CustomViewHolder customViewHolder = new HomeMonthSchedule_Adapter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMonthSchedule_Adapter.CustomViewHolder holder, int position) {
        holder.txt_start_time_month.setText(cardItemsMonth.get(position).getStartTimeMonth());
        holder.txt_end_time_month.setText(cardItemsMonth.get(position).getEndTimeMonth());
        holder.txt_day_month.setText(cardItemsMonth.get(position).getDayMonth());
        if (cardItemsMonth.get(position).getTherapyMonth().equals("1")) {
            holder.txt_therapy_month.setText("감각통증치료");
        }else if (cardItemsMonth.get(position).getTherapyMonth().equals("2")) {
            holder.txt_therapy_month.setText("언어치료");
        }else if (cardItemsMonth.get(position).getTherapyMonth().equals("3")) {
            holder.txt_therapy_month.setText("놀이치료");
        }else if (cardItemsMonth.get(position).getTherapyMonth().equals("4")) {
            holder.txt_therapy_month.setText("물리치료");
        }else if (cardItemsMonth.get(position).getTherapyMonth().equals("5")) {
            holder.txt_therapy_month.setText("작업치료");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String therapy = holder.txt_therapy_month.getText().toString();
                Toast.makeText(v.getContext(), therapy, Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != cardItemsMonth ? cardItemsMonth.size() : 0);
    }

    public void remove(int pos) {
        try {
            cardItemsMonth.remove(pos);
            notifyItemRemoved(pos);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_therapy_month, txt_start_time_month, txt_end_time_month, txt_day_month;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_therapy_month = itemView.findViewById(R.id.txt_therapy_month);
            this.txt_start_time_month = itemView.findViewById(R.id.txt_start_time_month);
            this.txt_end_time_month = itemView.findViewById(R.id.txt_end_time_month);
            this.txt_day_month = itemView.findViewById(R.id.txt_day_month);
        }
    }
}
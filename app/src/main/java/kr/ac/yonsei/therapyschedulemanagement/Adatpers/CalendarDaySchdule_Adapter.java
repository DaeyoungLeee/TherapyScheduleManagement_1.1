package kr.ac.yonsei.therapyschedulemanagement.Adatpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class CalendarDaySchdule_Adapter extends RecyclerView.Adapter<CalendarDaySchdule_Adapter.CustomViewHolder> {

    private ArrayList<CardItem> cardItems;

    public CalendarDaySchdule_Adapter(ArrayList<CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    @NonNull
    @Override
    public CalendarDaySchdule_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_dayschedule, parent,  false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarDaySchdule_Adapter.CustomViewHolder holder, int position) {
        holder.txt_therapy.setText(cardItems.get(position).getTherapy());
        holder.txt_start_time.setText(cardItems.get(position).getStartTime());
        holder.txt_end_time.setText(cardItems.get(position).getEndTime());
        String a = cardItems.get(position).getTherapy();
        if (a.equals("SensoryPain")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_red_icon);
        }else if (a.equals("Language")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_blue_icon);
        }else if (a.equals("Play")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_yellow_icon);
        }else if (a.equals("Physical")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_green_icon);
        }else if (a.equals("Occupation")) {
            holder.img_calendar_dot.setImageResource(R.drawable.dot_orrange_icon);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String therapy = holder.txt_therapy.getText().toString();
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
        return (null != cardItems ? cardItems.size() : 0);
    }

    public void remove(int pos) {
        try {
            cardItems.remove(pos);
            notifyItemRemoved(pos);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_therapy, txt_start_time, txt_end_time;
        private ImageView img_calendar_dot;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_therapy = itemView.findViewById(R.id.txt_therapy);
            this.txt_start_time = itemView.findViewById(R.id.txt_start_time);
            this.txt_end_time = itemView.findViewById(R.id.txt_end_time);
            this.img_calendar_dot = itemView.findViewById(R.id.img_calendar_dot);
        }
    }
}
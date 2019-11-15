package kr.ac.yonsei.therapyschedulemanagement;

import android.widget.TextView;

public class HomeMonth_CardItem {
    String therapyMonth;
    String dayMonth;
    String startTimeMonth;
    String endTimeMonth;

    public String getTherapyMonth() {
        return therapyMonth;
    }

    public void setTherapyMonth(String therapyMonth) {
        this.therapyMonth = therapyMonth;
    }

    public String getDayMonth() {
        return dayMonth;
    }

    public void setDayMonth(String dayMonth) {
        this.dayMonth = dayMonth;
    }

    public String getStartTimeMonth() {
        return startTimeMonth;
    }

    public void setStartTimeMonth(String startTimeMonth) {
        this.startTimeMonth = startTimeMonth;
    }

    public String getEndTimeMonth() {
        return endTimeMonth;
    }

    public void setEndTimeMonth(String endTimeMonth) {
        this.endTimeMonth = endTimeMonth;
    }

    public HomeMonth_CardItem(String therapyMonth, String dayMonth, String startTimeMonth, String endTimeMonth) {
        this.therapyMonth = therapyMonth;
        this.dayMonth = dayMonth;
        this.startTimeMonth = startTimeMonth;
        this.endTimeMonth = endTimeMonth;
    }
}

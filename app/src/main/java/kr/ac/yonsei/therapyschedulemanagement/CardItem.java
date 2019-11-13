package kr.ac.yonsei.therapyschedulemanagement;

public class CardItem {
    private String therapy, startTime, endTime;

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public CardItem(String therapy, String startTime, String endTime) {
        this.therapy = therapy;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CardItem() {
        this.therapy = therapy;
        this.startTime = startTime;
        this.endTime = endTime;

    }
}

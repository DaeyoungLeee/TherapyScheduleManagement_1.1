package kr.ac.yonsei.therapyschedulemanagement;

public class CardItem {
    private String startTime, endTime;

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

    public CardItem(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CardItem() {
        this.startTime = startTime;
        this.endTime = endTime;

    }
}

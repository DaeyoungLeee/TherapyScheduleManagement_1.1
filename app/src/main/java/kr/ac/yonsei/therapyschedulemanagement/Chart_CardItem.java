package kr.ac.yonsei.therapyschedulemanagement;

public class Chart_CardItem {
    private String chartDate;
    private String chartStatus;
    private String chartContents;

    public String getChartDate() {
        return chartDate;
    }

    public void setChartDate(String chartDate) {
        this.chartDate = chartDate;
    }

    public String getChartStatus() {
        return chartStatus;
    }

    public void setChartStatus(String chartStatus) {
        this.chartStatus = chartStatus;
    }

    public String getChartContents() {
        return chartContents;
    }

    public void setChartContents(String chartContents) {
        this.chartContents = chartContents;
    }

    public Chart_CardItem(String chartDate, String chartStatus, String chartContents) {
        this.chartDate = chartDate;
        this.chartStatus = chartStatus;
        this.chartContents = chartContents;
    }
}

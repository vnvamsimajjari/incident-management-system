public class DashboardCountsDTO {

    private long total;
    private long open;
    private long closed;
    private long inProgress;

    public DashboardCountsDTO(long total, long open, long closed, long inProgress) {
        this.total = total;
        this.open = open;
        this.closed = closed;
        this.inProgress = inProgress;
    }

    public long getTotal() { return total; }
    public long getOpen() { return open; }
    public long getClosed() { return closed; }
    public long getInProgress() { return inProgress; }
}
public class Profiler {
    private long[] start = {0,0,0,0,0,0,0,0,0,0};
    private long[] diff   = {0,0,0,0,0,0,0,0,0,0};
    private int length = start.length;
    private int current = 0;

    public Profiler() {
    }
    private void advance() {
        current++;
        if (current >= length) {
            current = 0;
        }
    }
    public void start() {
        start[current] = System.currentTimeMillis();
    }
    public void stop() {
        diff[current] = System.currentTimeMillis() - start[current]; 
        advance();
    }
    public long average() {
        long total = 0;
        for(long diffElement : diff) {
            total = total + diffElement;
        }
        return total / length;
    }
}
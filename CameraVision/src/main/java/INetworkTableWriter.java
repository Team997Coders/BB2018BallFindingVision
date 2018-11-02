/**
 * This interface defines what a network table writer for this ball finding
 * project needs to do.  We separate the interface from the implementation
 * to make this more testable.
 */
public interface INetworkTableWriter {
    public void write();
}
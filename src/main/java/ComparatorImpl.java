import java.util.Comparator;

public class ComparatorImpl implements Comparator<Command> {
    @Override
    public int compare(Command o1, Command o2) {
        return o1.getId() > o2.getId()?1:-1;
    }
}

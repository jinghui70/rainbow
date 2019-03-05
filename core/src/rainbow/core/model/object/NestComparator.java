package rainbow.core.model.object;

import java.util.Comparator;

public class NestComparator implements Comparator<INestObject<?>> {

    @Override
    public int compare(INestObject<?> o1, INestObject<?> o2) {
        return o1.getLeft() - o2.getLeft();
    }

    public static NestComparator instance = new NestComparator();
}

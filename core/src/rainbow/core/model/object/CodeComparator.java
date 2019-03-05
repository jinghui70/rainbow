package rainbow.core.model.object;

import java.util.Comparator;

public class CodeComparator implements Comparator<ICodeObject> {

    @Override
    public int compare(ICodeObject o1, ICodeObject o2) {
        return o1.getCode().compareTo(o2.getCode());
    }

    public static CodeComparator instance = new CodeComparator();
}

package rainbow.core.model.object;

import java.util.List;

public interface ITreeObject<T> {

	List<T> getChildren();

	void setChildren(List<T> children);

}

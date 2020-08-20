package rainbow.core.util.encrypt;

import rainbow.core.model.object.INameObject;

public interface Cipher extends INameObject {

	String encode(String source);

	String decode(String source);

}

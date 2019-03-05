package rainbow.core.bundle;

import java.io.IOException;
import java.io.InputStream;

/**
 * bundle中资源的描述
 * 
 * @author lijinghui
 * 
 */
public interface Resource {

    public String getName();

    /**
     * 返回一个输入流，调用者应该负责销毁
     * 
     * @return
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException;

    /**
     * 返回长度
     * 
     * @return
     */
    public long getSize() throws IOException;

}

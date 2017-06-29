package common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by guanxiaoda on 2017/6/29.
 */
public class SerializeUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(SerializeUtil.class);

    private final static String CHARSET = "ISO-8859-1";
    public static String object2String(Object obj)
    {
        String objBody = null;
        ByteArrayOutputStream baops = null;
        ObjectOutputStream oos = null;

        try
        {
            baops = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baops);
            oos.writeObject(obj);
            return baops.toString(CHARSET);
        } catch (IOException e)
        {
            LOGGER.error("serialize object failure", e);
            throw new RuntimeException(e);
        } finally
        {
            try
            {
                if (oos != null)
                    oos.close();
                if (baops != null)
                    baops.close();
            } catch (IOException e)
            {
                LOGGER.error("output stream err",e);
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T getObjectFromString
            (String objBody, Class<T> clazz)

    {
        byte[] bytes = objBody.getBytes(Charset.forName(CHARSET));
        ObjectInputStream ois = null;
        T obj = null;
        try
        {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            obj = (T) ois.readObject();
        } catch (IOException e)
        {

            LOGGER.error("deserialize object failure", e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e)
        {
            LOGGER.error("deserialize object failure", e);
            throw new RuntimeException(e);
        } finally
        {

            try
            {
                if (ois != null)
                    ois.close();
            } catch (IOException e)
            {
                LOGGER.error("input stream err",e);
                throw new RuntimeException(e);
            }
        }

        return obj;
    }

}

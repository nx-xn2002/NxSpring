package com.nx.nxspring.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * input stream callback
 *
 * @author nx-xn2002
 */
@FunctionalInterface
public interface InputStreamCallback<T> {

    /**
     * do with input stream
     *
     * @param stream stream
     * @return {@link T }
     * @throws IOException ioexception
     */
    T doWithInputStream(InputStream stream) throws IOException;
}

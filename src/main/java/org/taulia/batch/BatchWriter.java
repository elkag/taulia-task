package org.taulia.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taulia.util.FileUtil;

import java.io.IOException;

class BatchWriter {

    private static volatile BatchWriter INSTANCE = null;

    private BatchWriter() {
    }

    public static BatchWriter getInstance()
    {
        if (INSTANCE == null) {
            synchronized (BatchWriter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BatchWriter();
                }
            }
        }
        return INSTANCE;
    }

    void write(String filePath, String content) throws IOException {
        FileUtil.write(filePath, content);
    }
}

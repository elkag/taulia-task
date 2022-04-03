package org.taulia.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BatchProcessor {

    private final String columnSeparator;

    ExecutorService executor = Executors.newFixedThreadPool(10);

    private final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);

    public BatchProcessor(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    public void process(Map<String, List<String[]>> toProcess) throws IOException {
        List<Future<Boolean>> futures = new ArrayList<>();
        logger.info("Start writing batch");
        toProcess.forEach((key, value) -> futures.add(executor.submit(() -> {
            String content = value.stream().map(e -> String.join(columnSeparator, e)).collect(Collectors.joining(System.lineSeparator()));
            try {
                logger.info(String.format("Batch initialized %s", key));
                BatchWriter.getInstance().write(key, content);
                logger.info(String.format("Write end: %s", key));
            } catch (IOException e) {
                logger.error(e.getMessage());
                return false;
            }
            return true;
        })));

        for (Future<Boolean> future : futures) {
            boolean success = false;
            try {
                success = future.get(10, TimeUnit.MINUTES);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            if(!success) throw new IOException("Can not write the file.");
        }
        logger.info("End writing batch");
    }
}

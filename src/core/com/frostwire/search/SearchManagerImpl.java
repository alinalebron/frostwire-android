package com.frostwire.search;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchManagerImpl implements SearchManager {

    private static final Logger LOG = LoggerFactory.getLogger(SearchManagerImpl.class);

    private ExecutorService executor;

    private SearchResultListener listener;

    public SearchManagerImpl() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void registerListener(SearchResultListener listener) {
        this.listener = listener;
    }

    @Override
    public void perform(SearchPerformer performer) {
        if (performer != null) {
            performer.registerListener(new PerformerResultListener(this));
            executor.execute(new SearchTask(this, performer));
        } else {
            LOG.warn("Search performer is null, review your logic");
        }
    }

    @Override
    public boolean shutdown(long timeout, TimeUnit unit) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeout, unit)) {
                executor.shutdownNow();
                // wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(timeout, unit)) {
                    LOG.error("Pool did not terminate");
                    return false;
                }
            }
        } catch (InterruptedException ie) {
            // (re-)cancel if current thread also interrupted
            executor.shutdownNow();
            // preserve interrupt status
            Thread.currentThread().interrupt();
        }

        return true;
    }

    protected void onResults(SearchPerformer performer, List<? extends SearchResult<?>> results) {
        try {
            if (listener != null) {
                listener.onResults(performer, results);
            }
        } catch (Throwable e) {
            LOG.warn("Error sending results back to receiver: " + e.getMessage());
        }
    }

    protected void onFinished(SearchPerformer performer) {
        try {
            if (listener != null) {
                listener.onFinished(performer);
            }
        } catch (Throwable e) {
            LOG.warn("Error sending finished signal to receiver: " + e.getMessage());
        }
    }

    private static final class PerformerResultListener implements SearchResultListener {

        private final SearchManagerImpl manager;

        public PerformerResultListener(SearchManagerImpl manager) {
            this.manager = manager;
        }

        @Override
        public void onResults(SearchPerformer performer, List<? extends SearchResult<?>> results) {
            manager.onResults(performer, results);
        }

        @Override
        public void onFinished(SearchPerformer performer) {
            // no need to call here, since it will be managed in the async task
        }
    }

    private static final class SearchTask implements Runnable {

        private final SearchManagerImpl manager;
        private final SearchPerformer performer;

        public SearchTask(SearchManagerImpl manager, SearchPerformer performer) {
            this.manager = manager;
            this.performer = performer;
        }

        @Override
        public void run() {
            try {
                performer.perform();
            } catch (Throwable e) {
                LOG.warn("Error performing search: " + performer);
            } finally {
                manager.onFinished(performer);
            }
        }
    }
}

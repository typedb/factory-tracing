package grabl.tracing.client;

import grabl.tracing.client.GrablTracing.Analysis;
import grabl.tracing.client.GrablTracing.Trace;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class StaticThreadTracing {

    private static final ThreadTrace NOOP = new NoopThreadTrace();
    private static final AtomicBoolean ENABLED = new AtomicBoolean(false);

    private static Analysis singletonAnalysis;
    private static final ThreadStack<ThreadContext> contextStack = new ThreadStack<>();
    private static final ThreadStack<ThreadTrace> traceStack = new ThreadStack<>();

    public static void setAnalysis(Analysis analysis) {
        if (ENABLED.compareAndSet(false, true)) {
            singletonAnalysis = analysis;
        } else {
            throw new IllegalStateException("Tried to set analysis twice");
        }
    }

    public static ThreadTrace traceOnThread(String name) {
        if (!ENABLED.get()) {
            return NOOP;
        }

        ThreadTrace stacked = traceStack.peek();
        if (stacked != null) {
            return stacked.traceOnThread(name);
        }

        ThreadContext context = contextStack.peek();
        if (context == null) {
            throw new IllegalStateException("No context found: must be in a ThreadContext");
        }

        return new ThreadTraceImpl(singletonAnalysis.trace(name, context.tracker, context.iteration));
    }

    public static ThreadContext contextOnThread(String tracker, int iteration) {
        return new ThreadContext(tracker, iteration);
    }

    public static ThreadContext replaceTrackerOnThread(String tracker) {
        ThreadContext stacked = contextStack.peek();
        if (stacked == null) {
            throw new IllegalStateException("Cannot replace tracker when not in a context");
        }
        return new ThreadContext(tracker, stacked.iteration);
    }


    public interface ThreadTrace extends Trace, AutoCloseable {
        ThreadTrace traceOnThread(String name);
    }


    private static class ThreadTraceImpl implements ThreadTrace {
        private final Trace trace;

        private ThreadTraceImpl(Trace inner) {
            trace = inner;
            traceStack.push(this);
        }

        @Override
        public ThreadTrace traceOnThread(String name) {
            return new ThreadTraceImpl(trace.trace(name));
        }

        @Override
        public Trace trace(String name) {
            return trace.trace(name);
        }

        @Override
        public Trace data(String data) {
            trace.data(data);
            return this;
        }

        @Override
        public Trace labels(String... labels) {
            trace.labels(labels);
            return this;
        }

        @Override
        public Trace end() {
            trace.end();
            return this;
        }

        @Override
        public UUID getRootId() {
            return trace.getRootId();
        }

        @Override
        public UUID getId() {
            return trace.getId();
        }

        @Override
        public void close() {
            end();
            Trace stackedTrace = traceStack.pop();
            if (this != stackedTrace) {
                throw new IllegalStateException("Traces were ended in the wrong order");
            }
        }
    }


    private static class NoopThreadTrace implements ThreadTrace {

        @Override
        public ThreadTrace traceOnThread(String name) {
            return NOOP;
        }

        @Override
        public Trace trace(String name) {
            return NOOP;
        }

        @Override
        public Trace data(String data) {
            return NOOP;
        }

        @Override
        public Trace labels(String... labels) {
            return NOOP;
        }

        @Override
        public Trace end() {
            return NOOP;
        }

        @Override
        public UUID getRootId() {
            return null;
        }

        @Override
        public UUID getId() {
            return null;
        }

        @Override
        public void close() {
        }
    }


    public static class ThreadContext implements AutoCloseable {
        private final String tracker;
        private final int iteration;

        private ThreadContext(String tracker, int iteration) {
            this.tracker = tracker;
            this.iteration = iteration;
            contextStack.push(this);
        }

        @Override
        public void close() {
            ThreadContext stacked = contextStack.pop();
            if (stacked != this) {
                throw new IllegalStateException("Contexts were ended in the wrong order");
            }
        }
    }


    private static class ThreadStack<T> {
        private final ThreadLocal<Deque<T>> stack = new ThreadLocal<>();

        private void push(T item) {
            Deque<T> deque = stack.get();
            if (deque == null) {
                deque = new ArrayDeque<>();
                stack.set(deque);
            }
            deque.push(item);
        }

        private T pop() {
            Deque<T> deque = stack.get();
            if (deque == null) {
                throw new NoSuchElementException();
            } else  {
                T item = deque.pop();
                if (deque.isEmpty()) {
                    stack.set(null);
                }
                return item;
            }
        }

        private T peek() {
            Deque<T> deque = stack.get();
            if (deque == null) {
                return null;
            } else {
                return deque.peek();
            }
        }
    }
}

package grabl.tracing.client;

import grabl.tracing.client.GrablTracing.Analysis;
import grabl.tracing.client.GrablTracing.Trace;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.UUID;

public class StaticThreadTracing {

    private static Analysis singletonAnalysis;
    private static final ThreadStack<ThreadContext> contextStack = new ThreadStack<>();
    private static final ThreadStack<ThreadTrace> traceStack = new ThreadStack<>();

    public static void setAnalysis(Analysis analysis) {
        synchronized (StaticThreadTracing.class) {
            singletonAnalysis = analysis;
        }
    }

    public static ThreadTrace traceOnThread(String name) {
        ThreadTrace stacked = traceStack.peek();
        if (stacked != null) {
            return stacked.traceOnThread(name);
        }

        ThreadContext context = contextStack.peek();
        if (context == null) {
            throw new IllegalStateException("No context found: must be in a ThreadContext");
        }

        synchronized (StaticThreadTracing.class) {
            if (singletonAnalysis != null) {
                return new ThreadTrace(singletonAnalysis.trace(name, context.tracker, context.iteration));
            }
        }

        throw new IllegalStateException("Cannot use static tracing without setting an analysis");
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

    public static class ThreadTrace implements Trace, AutoCloseable {
        private final Trace trace;

        private ThreadTrace(Trace inner) {
            trace = inner;
            traceStack.push(this);
        }

        public ThreadTrace traceOnThread(String name) {
            return new ThreadTrace(trace.trace(name));
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

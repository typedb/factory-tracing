package grabl.tracing.client;

import grabl.tracing.client.GrablTracing.Analysis;
import grabl.tracing.client.GrablTracing.Trace;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides singleton static thread-contextual tracing. The limitation is that only one Analysis can be
 * ongoing in the application, but the advantage is that the application does not have to pass around trace parameters
 * or extend internal APIs, making adding tracing to an existing application as a cross-cutting concern much easier.
 */
public class StaticThreadTracing {

    private static final String EMPTY = "";

    private static final ThreadTrace NOOP = new ThreadTraceNoOp();
    private static final AtomicBoolean ENABLED = new AtomicBoolean(false);

    private static Analysis singletonAnalysis;
    private static final ThreadStack<ThreadContext> contextStack = new ThreadStack<>();
    private static final ThreadStack<ThreadTrace> traceStack = new ThreadStack<>();

    /**
     * Set the Analysis for the application and enable tracing globally beyond this point.
     *
     * @param analysis The Grabl tracing analysis to set.
     */
    public synchronized static void setAnalysis(Analysis analysis) {
        if (ENABLED.compareAndSet(false, true)) {
            singletonAnalysis = analysis;
        } else {
            throw new IllegalStateException("Tried to set analysis twice");
        }
    }

    /**
     * Create a try-with-resources resource that can behaves as a tracing object but also pushes itself onto a
     * {@link ThreadLocal} stack. When the resource is closed, the object is popped from the stack.
     *
     * If no trace is currently present, uses the current thread's context to provide the parameters necessary to start
     * a new root trace. Use {@link #contextOnThread(String, int)} to supply context.
     *
     * @throws IllegalStateException if there is no current trace on the trace stack and not context on the context
     * stack.
     *
     * @param name The trace name.
     * @return A try-with-resources representation of the Trace and its existence on the thread's stack.
     */
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

        return new ThreadTraceImpl(singletonAnalysis.trace(name, context.getTracker(), context.getIteration()));
    }

    /**
     * Gets the current trace for the thread.
     *
     * @return The current trace for the thread, null if none exists.
     */
    public static ThreadTrace currentThreadTrace() {
        if (!ENABLED.get()) {
            return NOOP;
        }

        return traceStack.peek();
    }

    /**
     * Places context onto this thread's {@link ThreadLocal} stack, which is used to create new root traces.
     *
     * @param tracker The tracker for this trace.
     * @param iteration The iteration for this trace.
     * @return A try-with-resources instance to control the lifetime of this context information on the thread's stack.
     */
    public static ThreadContext contextOnThread(String tracker, int iteration) {
        return new ThreadContextImpl(tracker, iteration);
    }

    /**
     * A class to control the lifetime of a thread's trace.
     */
    public interface ThreadTrace extends Trace, AutoCloseable {
        /**
         * Behaves the same way as {@link StaticThreadTracing#traceOnThread(String)} but specifically creates a child
         * of this {@link ThreadTrace} for the local thread rather than a child of the current thread trace.
         *
         * @param name The trace name.
         * @return A try-with-resources representation of the Trace and its existence on the thread's stack.
         */
        ThreadTrace traceOnThread(String name);

        @Override
        void close();
    }

    /**
     * A class to control the lifetime of a thread's tracing context.
     */
    public interface ThreadContext extends AutoCloseable {
        String getTracker();
        int getIteration();

        @Override
        void close();
    }

    // Implementation

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


    private static class ThreadTraceNoOp implements ThreadTrace {

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


    private static class ThreadContextImpl implements ThreadContext {
        private final String tracker;
        private final int iteration;

        private ThreadContextImpl(String tracker, int iteration) {
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

        @Override
        public String getTracker() {
            return tracker;
        }

        @Override
        public int getIteration() {
            return iteration;
        }
    }


    private class ThreadContextNoOp implements ThreadContext {

        @Override
        public String getTracker() {
            return EMPTY;
        }

        @Override
        public int getIteration() {
            return 0;
        }

        @Override
        public void close() {
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

/*
 * Copyright (C) 2021 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.factory.tracing.client;

import com.vaticle.factory.tracing.client.FactoryTracing.Analysis;
import com.vaticle.factory.tracing.client.FactoryTracing.Trace;

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
public class FactoryTracingThreadStatic {

    private static final String EMPTY = "";

    private static final ThreadTrace THREAD_TRACE_NO_OP = new ThreadTraceNoOp();
    private static final ThreadContext THREAD_CONTEXT_NO_OP = new ThreadContextNoOp();

    private static final AtomicBoolean ENABLED = new AtomicBoolean(false);
    private static final AtomicBoolean ANALYSIS_SET = new AtomicBoolean(false);

    private static FactoryTracing singletonClient;
    private static Analysis singletonAnalysis;

    private static final ThreadStack<ThreadContext> contextStack = new ThreadStack<>();
    private static final ThreadStack<ThreadTrace> traceStack = new ThreadStack<>();

    /**
     * Set the Analysis for the application and enable tracing globally beyond this point.
     *
     * @param client The Vaticle Factory tracing client to set.
     */
    public synchronized static void setGlobalTracingClient(FactoryTracing client) {
        if (ENABLED.compareAndSet(false, true)) {
            singletonClient = client;
        } else {
            throw new IllegalStateException("Tried to set global Vaticle Factory tracing client twice");
        }
    }

    /**
     * Get the current singleton Vaticle Factory tracing client. The user should always call {@link #isTracingEnabled()} first.
     *
     * @return the singleton Vaticle Factory tracing client
     */
    public static FactoryTracing getFactoryTracing() {
        return singletonClient;
    }

    /**
     * Set the Analysis for the application and enable tracing globally beyond this point.
     *
     * @param owner  The Vaticle Factory tracing repo owner to set.
     * @param repo   The Vaticle Factory tracing repo to set.
     * @param commit The Vaticle Factory tracing commit to set.
     */
    public synchronized static void openGlobalAnalysis(String owner, String repo, String commit, String analysisName) {
        if (!ENABLED.get()) {
            throw new IllegalStateException("Tried to open analysis without setting a global tracing client");
        }
        if (ANALYSIS_SET.compareAndSet(false, true)) {
            singletonAnalysis = singletonClient.analysis(owner, repo, commit, analysisName);
        } else {
            throw new IllegalStateException("Tried to open global analysis twice");
        }
    }

    /**
     * Create a try-with-resources resource that can behaves as a tracing object but also pushes itself onto a
     * {@link ThreadLocal} stack. When the resource is closed, the object is popped from the stack.
     *
     * If no trace is currently present, uses the current thread's context to provide the parameters necessary to start
     * a new root trace. Use {@link #contextOnThread(String, int)} to supply context.
     *
     * @param name The trace name.
     * @return A try-with-resources representation of the Trace and its existence on the thread's stack.
     * @throws IllegalStateException if there is no current trace on the trace stack and not context on the context
     *                               stack.
     */
    public static ThreadTrace traceOnThread(String name) {
        if (!ENABLED.get()) {
            return THREAD_TRACE_NO_OP;
        }

        ThreadTrace stacked = traceStack.peek();
        if (stacked != null) {
            return stacked.traceOnThread(name);
        }

        if (!ANALYSIS_SET.get()) {
            return THREAD_TRACE_NO_OP;
        }

        ThreadContext context = contextStack.peek();
        if (context == null) {
            return THREAD_TRACE_NO_OP;
        }

        return new ThreadTraceImpl(singletonAnalysis.trace(name, context.getTracker(), context.getIteration()));
    }

    /**
     * Open a trace continuing from a parent that may have been distributed across a network.
     *
     * @param rootId   The parent trace rootId (identifies the trace tree).
     * @param parentId The parent trace id.
     * @param name     The trace name.
     * @return A try-with-resources representation of the Trace and its existence on the thread's stack.
     */
    public static ThreadTrace continueTraceOnThread(UUID rootId, UUID parentId, String name) {
        if (!ENABLED.get()) {
            return THREAD_TRACE_NO_OP;
        }

        return new ThreadTraceImpl(singletonClient.trace(rootId, parentId, name));
    }

    /**
     * Gets the current trace for the thread.
     *
     * @return The current trace for the thread, null if none exists.
     */
    public static ThreadTrace currentThreadTrace() {
        if (!ENABLED.get()) {
            return THREAD_TRACE_NO_OP;
        }

        return traceStack.peek();
    }

    /**
     * Places context onto this thread's {@link ThreadLocal} stack, which is used to create new root traces.
     *
     * @param tracker   The tracker for this trace.
     * @param iteration The iteration for this trace.
     * @return A try-with-resources instance to control the lifetime of this context information on the thread's stack.
     */
    public static ThreadContext contextOnThread(String tracker, int iteration) {
        if (ENABLED.get()) {
            return new ThreadContextImpl(tracker, iteration);
        } else {
            return THREAD_CONTEXT_NO_OP;
        }
    }

    /**
     * Discover whether or not tracing is enabled, useful to decide whether or not to perform some calculations that
     * are specific to tracing.
     *
     * @return true if tracing is enabled, false if running in no-op mode
     */
    public static boolean isTracingEnabled() {
        return ENABLED.get();
    }

    /**
     * A class to control the lifetime of a thread's trace.
     */
    public interface ThreadTrace extends Trace, AutoCloseable {
        /**
         * Behaves the same way as {@link FactoryTracingThreadStatic#traceOnThread(String)} but specifically creates a child
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
            return THREAD_TRACE_NO_OP;
        }

        @Override
        public Trace trace(String name) {
            return THREAD_TRACE_NO_OP;
        }

        @Override
        public Trace data(String data) {
            return THREAD_TRACE_NO_OP;
        }

        @Override
        public Trace labels(String... labels) {
            return THREAD_TRACE_NO_OP;
        }

        @Override
        public Trace end() {
            return THREAD_TRACE_NO_OP;
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


    private static class ThreadContextNoOp implements ThreadContext {

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
            } else {
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

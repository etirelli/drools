/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.drools.core.util.sequence.ExecutionContext.MatchResult;

/**
 * A virtual machine implementation that executes a
 * compiled sequence against input tokens. This
 * algorithm supports stepped execution as the 
 * input tokens might arrive asynchronously. 
 * 
 * @author etirelli
 *
 */
public class ThompsonsVM {

    public ExecutionContext createContext(CompiledSequence sequence) {
        return new ThompsonsContext( sequence );
    }

    public ExecutionContext execute(ExecutionContext context,
                                    Token input) {
        ThompsonsContext ctx = (ThompsonsContext) context;
        for ( ListIterator<ThreadContext> it = ctx.getThreads().listIterator(); it.hasNext(); ) {
            ThreadContext thread = it.next();
            thread.setStatus( ThreadStatus.RUNNING );
            thread.setInput( input );
            while ( thread.getStatus() == ThreadStatus.RUNNING ) {
                ctx.getSequence().getInstructions()[thread.ic].execute( thread );
            }
            if ( thread.getResult() == MatchResult.ACCEPT ) {
                ctx.setResult( thread.getResult() );
                break;
            }
            if ( thread.getStatus() == ThreadStatus.KILLED ) {
                it.remove();
            }
            for( ThreadContext tctx : thread.getNewContexts() ) {
                it.add( tctx );
                it.previous(); // in order to take the new thread into account
            }
            thread.setNewContexts( null );
        }
        if( ctx.getThreads().isEmpty() ) {
            ctx.setResult( MatchResult.REJECT );
        }
        return context;
    }

    public static class ThompsonsContext
            implements
            ExecutionContext {
        private CompiledSequence          sequence;
        private List<ThreadContext>       threads;
        private MatchResult               result;

        public ThompsonsContext(CompiledSequence sequence) {
            this.sequence = sequence;
            this.threads = new LinkedList<ThompsonsVM.ThreadContext>();
            this.threads.add( new ThreadContext( 0 ) );
            this.result = MatchResult.UNDEFINED;
        }

        public CompiledSequence getSequence() {
            return sequence;
        }

        public List<ThreadContext> getThreads() {
            return threads;
        }

        public void setResult(MatchResult result) {
            this.result = result;
        }

        public MatchResult getResult() {
            return this.result;
        }
    }

    public static class ThreadContext {
        public int            ic;
        private Token         input;
        private ThreadStatus  status;
        private MatchResult   result;
        private List<ThreadContext> newContexts;

        public ThreadContext(int ic) {
            this.ic = ic;
            this.status = ThreadStatus.BLOCKED;
            this.result = MatchResult.UNDEFINED;
        }

        public Token getInput() {
            return input;
        }

        public void setInput(Token input) {
            this.input = input;
        }

        public ThreadStatus getStatus() {
            return status;
        }

        public void setStatus(ThreadStatus status) {
            this.status = status;
        }

        public MatchResult getResult() {
            return result;
        }

        public void setResult(MatchResult result) {
            this.result = result;
        }

        public int getInstructionCounter() {
            return this.ic;
        }

        public void setInstructionCounter(int ic) {
            this.ic = ic;
        }

        public void incInstructionCounter() {
            this.ic++;
        }

        @SuppressWarnings("unchecked")
        public List<ThreadContext> getNewContexts() {
            return (List<ThreadContext>) (newContexts != null ? newContexts : Collections.emptyList());
        }

        public void addNewContext(ThreadContext newContext) {
            if( this.newContexts == null ) {
                this.newContexts = new ArrayList<ThompsonsVM.ThreadContext>();
            }
            this.newContexts.add( newContext );
        }

        public void setNewContexts(List<ThreadContext> contexts) {
            this.newContexts = contexts;
        }

        @Override
        public String toString() {
            return "ThreadContext [ic=" + ic + ", input=" + input + ", status=" + status + ", result=" + result + ", newContext=" + newContexts + "]";
        }
    }

    public static enum ThreadStatus {
        RUNNING, BLOCKED, KILLED
    }

}

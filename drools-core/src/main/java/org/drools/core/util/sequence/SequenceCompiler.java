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
import java.util.List;

/**
 * A helper builder to compile a sequence
 * matcher.
 * 
 * @author etirelli
 */
public class SequenceCompiler {
    
    private List<Instruction> instructions;
    
    private SequenceCompiler() {
        instructions = new ArrayList<Instruction>();
    }
    
    public static SequenceCompiler newSequence() {
        return new SequenceCompiler();
    }
    
    public static Label newLabel() {
        return new Label();
    }

    public SequenceCompiler match( Token token ) {
        instructions.add( new MatchInstruction( token ) );
        return this;
    }
    
    public SequenceCompiler match( Label label, Token token ) {
        // set the label index
        label.setIndex( instructions.size() );
        instructions.add( new MatchInstruction( label, token ) );
        return this;
    }
    
    public SequenceCompiler accept() {
        instructions.add( new AcceptInstruction() );
        return this;
    }

    public SequenceCompiler accept( Label label ) {
        // set the label index
        label.setIndex( instructions.size() );
        instructions.add( new AcceptInstruction( label ) );
        return this;
    }

    public SequenceCompiler split(Label toLabel1,
                                  Label toLabel2) {
        instructions.add( new SplitInstruction( toLabel1, toLabel2 ) );
        return this;
    }

    public SequenceCompiler split(Label label,
                                  Label toLabel1, 
                                  Label toLabel2 ) {
        // set the label index
        label.setIndex( instructions.size() );
        instructions.add( new SplitInstruction( label, toLabel1, toLabel2 ) );
        return this;
    }
    
    public SequenceCompiler split(int index1,
                                  int index2 ) {
        instructions.add( new SplitInstruction( index1, index2 ) );
        return this;
    }

    public SequenceCompiler split(Label label,
                                  int index1,
                                  int index2 ) {
        // set the label index
        label.setIndex( instructions.size() );
        instructions.add( new SplitInstruction( label, index1, index2 ) );
        return this;
    }
    
    public SequenceCompiler jump( Label toLabel ) {
        instructions.add( new JumpInstruction( toLabel ) );
        return this;
    }

    public SequenceCompiler jump( Label label, Label toLabel ) {
        // set the label index
        label.setIndex( instructions.size() );
        instructions.add( new JumpInstruction( label, toLabel ) );
        return this;
    }

    public SequenceCompiler jump( int index1 ) {
        instructions.add( new JumpInstruction( index1 ) );
        return this;
    }

    public SequenceCompiler jump( Label label, int index1 ) {
        // set the label index
        label.setIndex( instructions.size() );
        instructions.add( new JumpInstruction( label, index1 ) );
        return this;
    }

    public CompiledSequence compile() {
        return new CompiledSequence( instructions.toArray( new Instruction[ instructions.size()] ) );
    }


}

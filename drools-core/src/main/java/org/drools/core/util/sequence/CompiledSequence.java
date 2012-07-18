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

/**
 * The sequencing feature's state machine is implemented 
 * by using a variant of the Thompson's algorithm designed
 * as a virtual machine. 
 * 
 * This class represents a compiled list of instructions
 * to match a given event sequence. It is stateless to data,
 * in a similar way a traditional computer program is. The
 * context/state is stored externally. 
 * 
 * Related information can be found in the following link:
 * 
 * {@link http://swtch.com/~rsc/regexp/regexp2.html}
 * 
 * @author etirelli
 *
 */
public class CompiledSequence {
    
    private Instruction[] instructions;

    public CompiledSequence(Instruction[] instructions ) {
        this.instructions = instructions;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        for( int i = 0; i < instructions.length; i++ ) {
            builder.append( i );
            builder.append( ": " );
            builder.append( instructions[i].toString() );
            builder.append( "\n" );
        }
        return builder.toString();
    }

}

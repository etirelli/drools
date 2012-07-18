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

import org.drools.core.util.sequence.ThompsonsVM.ThreadContext;

/**
 * The sequencing feature's state machine is implemented 
 * by using a variant of the Thompson's algorithm designed
 * as a virtual machine. 
 * 
 * This class represents a single instruction in a given
 * compiled sequence. It is in many ways similar to what 
 * an op code is in a language like java. It is, though, 
 * stateless in a similar way a traditional computer instruction
 * is.  
 * 
 * Related information can be found in the following link:
 * 
 * {@link http://swtch.com/~rsc/regexp/regexp2.html}
 * 
 * @author etirelli
 *
 */
public interface Instruction {
    
    public Label getLabel(); 

    public void execute( ThreadContext thread );

}

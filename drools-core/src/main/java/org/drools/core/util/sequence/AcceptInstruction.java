package org.drools.core.util.sequence;

import org.drools.core.util.sequence.ExecutionContext.MatchResult;
import org.drools.core.util.sequence.ThompsonsVM.ThreadContext;
import org.drools.core.util.sequence.ThompsonsVM.ThreadStatus;

public class AcceptInstruction extends BaseInstruction
        implements
        Instruction {
    
    public AcceptInstruction() {
        this( null );
    }

    public AcceptInstruction(Label label) {
        super( label );
    }

    public void execute( ThreadContext ctx ) {
        ctx.setResult( MatchResult.ACCEPT );
        ctx.setStatus( ThreadStatus.KILLED );
    }
    
    @Override
    public String toString() {
        return "ACCEPT";
    }

}

package org.drools.core.util.sequence;

import org.drools.core.util.sequence.ExecutionContext.MatchResult;
import org.drools.core.util.sequence.ThompsonsVM.ThreadContext;
import org.drools.core.util.sequence.ThompsonsVM.ThreadStatus;

public class MatchInstruction extends BaseInstruction {
    
    private final Token token;
    
    public MatchInstruction( Token token ) {
        this( null, token );
    }

    public MatchInstruction(Label label,
                            Token token) {
        super( label );
        this.token = token;
    }

    public void execute( ThreadContext ctx ) {
        if( ctx.getInput() == null ) {
            ctx.setStatus( ThreadStatus.BLOCKED );
            return;
        } 
        
        if( ! ctx.getInput().equals( this.token ) ) {
            ctx.setResult( MatchResult.REJECT );
            ctx.setStatus( ThreadStatus.KILLED );
        }
        // consumes the input token
        ctx.setInput( null );
        ctx.incInstructionCounter();
    }
    
    public Token getToken() {
        return this.token;
    }

    @Override
    public String toString() {
        return "MATCH " + token;
    }

}

package org.drools.core.util.sequence;

import org.drools.core.util.sequence.ThompsonsVM.ThreadContext;

public class JumpInstruction extends BaseInstruction {
    
    private int instr;

    public JumpInstruction( int instr ) {
        this( null, 
              instr );
    }

    public JumpInstruction( Label toLabel ) {
        this( null, 
              toLabel );
    }

    public JumpInstruction(Label label,
                           int index1) {
        super( label );
        this.instr = index1;
    }

    public JumpInstruction(Label label,
                           Label toLabel) {
        super( label );
        this.instr = toLabel.getIndex();
        toLabel.addDependency( new JumpIndexUpdater() );
    }

    public void execute( ThreadContext ctx ) {
        ctx.setInstructionCounter( instr );
    }
    
    public int getInstrIndex() {
        return this.instr;
    }
    
    @Override
    public String toString() {
        return "JUMP " + instr;
    }
    
    private class JumpIndexUpdater implements LabelUpdater {
        public void update( Label lbl ) {
            instr = lbl.getIndex();
        }
    }

}

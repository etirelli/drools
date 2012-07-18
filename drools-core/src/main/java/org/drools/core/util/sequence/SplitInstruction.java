package org.drools.core.util.sequence;

import org.drools.core.util.sequence.ThompsonsVM.ThreadContext;

public class SplitInstruction extends BaseInstruction {
    
    private int instr1;
    private int instr2;

    public SplitInstruction( int instr1, int instr2 ) {
        this( null, instr1, instr2 );
    }

    public SplitInstruction( Label label, int instr1, int instr2 ) {
        super( label );
        this.instr1 = instr1;
        this.instr2 = instr2;
    }

    public SplitInstruction( Label toLabel1, Label toLabel2 ) {
        this( null, toLabel1, toLabel2 );
    }
    
    public SplitInstruction( Label label, Label toLabel1, Label toLabel2 ) {
        super( label );
        this.instr1 = toLabel1.getIndex();
        this.instr2 = toLabel2.getIndex();
        toLabel1.addDependency( new SplitIndex1Updater() );
        toLabel2.addDependency( new SplitIndex2Updater() );
    }

    public void execute( ThreadContext ctx ) {
        ctx.setInstructionCounter( instr1 );
        ctx.addNewContext( new ThreadContext( this.instr2 ) );
    }
    
    public int getInstrIndex1() {
        return this.instr1;
    }
    
    public int getInstrIndex2() {
        return this.instr1;
    }
    
    @Override
    public String toString() {
        return "SPLIT " + instr1 + ", "+instr2;
    }
    
    private class SplitIndex1Updater implements LabelUpdater {
        public void update( Label lbl ) {
            instr1 = lbl.getIndex();
        }
    }
    
    private class SplitIndex2Updater implements LabelUpdater {
        public void update( Label lbl ) {
            instr2 = lbl.getIndex();
        }
    }
}

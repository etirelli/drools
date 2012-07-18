package org.drools.core.util.sequence;

public abstract class BaseInstruction implements Instruction {
    private Label label;
    
    public BaseInstruction() {
        this( null );
    }
    
    public BaseInstruction( Label label ) {
        this.label = label;
    }

    public Label getLabel() {
        return this.label;
    }
}

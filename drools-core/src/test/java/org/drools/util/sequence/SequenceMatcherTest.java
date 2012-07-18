package org.drools.util.sequence;

import static org.junit.Assert.assertEquals;

import org.drools.core.util.sequence.CompiledSequence;
import org.drools.core.util.sequence.ExecutionContext;
import org.drools.core.util.sequence.ExecutionContext.MatchResult;
import org.drools.core.util.sequence.Label;
import org.drools.core.util.sequence.MatchInstruction;
import org.drools.core.util.sequence.SequenceCompiler;
import org.drools.core.util.sequence.ThompsonsVM;
import org.drools.core.util.sequence.Token;
import org.junit.Test;

public class SequenceMatcherTest {

    @Test
    public void testMatch() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );

        // T1 T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .match( tk1 )
                .match( tk2 )
                .accept()
                .compile();

        assertEquals( 3, sequence.getInstructions().length );
        assertEquals( tk1, ((MatchInstruction) sequence.getInstructions()[0]).getToken() );
        assertEquals( tk2, ((MatchInstruction) sequence.getInstructions()[1]).getToken() );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk1 );
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk2 );
        assertEquals( MatchResult.ACCEPT, ctx.getResult() );
    }

    @Test
    public void testReject() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );

        // T1 T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .match( tk1 )
                .match( tk2 )
                .accept()
                .compile();

        assertEquals( 3, sequence.getInstructions().length );
        assertEquals( tk1, ((MatchInstruction) sequence.getInstructions()[0]).getToken() );
        assertEquals( tk2, ((MatchInstruction) sequence.getInstructions()[1]).getToken() );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk1 );
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk1 );
        assertEquals( MatchResult.REJECT, ctx.getResult() );
    }
    
    @Test
    public void testOrAcceptBranch1() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );
        
        Label lb1 = SequenceCompiler.newLabel();
        Label lb2 = SequenceCompiler.newLabel();
        Label lb3 = SequenceCompiler.newLabel();

        // T1 | T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .split( lb1, lb2 )
                .match( lb1, tk1 )
                .jump( lb3 )
                .match( lb2, tk2 )
                .accept( lb3 )
                .compile();

        assertEquals( 5, sequence.getInstructions().length );
        assertEquals( tk1, ((MatchInstruction) sequence.getInstructions()[lb1.getIndex()]).getToken() );
        assertEquals( tk2, ((MatchInstruction) sequence.getInstructions()[lb2.getIndex()]).getToken() );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk1 );
        assertEquals( MatchResult.ACCEPT, ctx.getResult() );
    }
    
    @Test
    public void testOrAcceptBranch2() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );
        
        Label lb1 = SequenceCompiler.newLabel();
        Label lb2 = SequenceCompiler.newLabel();
        Label lb3 = SequenceCompiler.newLabel();

        // T1 | T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .split( lb1, lb2 )
                .match( lb1, tk1 )
                .jump( lb3 )
                .match( lb2, tk2 )
                .accept( lb3 )
                .compile();

        assertEquals( 5, sequence.getInstructions().length );
        assertEquals( tk1, ((MatchInstruction) sequence.getInstructions()[lb1.getIndex()]).getToken() );
        assertEquals( tk2, ((MatchInstruction) sequence.getInstructions()[lb2.getIndex()]).getToken() );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk2 );
        assertEquals( MatchResult.ACCEPT, ctx.getResult() );
    }
    
    @Test
    public void testOrReject() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );
        Token tk3 = new Token( 3 );
        
        Label lb1 = SequenceCompiler.newLabel();
        Label lb2 = SequenceCompiler.newLabel();
        Label lb3 = SequenceCompiler.newLabel();

        // T1 | T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .split( lb1, lb2 )
                .match( lb1, tk1 )
                .jump( lb3 )
                .match( lb2, tk2 )
                .accept( lb3 )
                .compile();

        assertEquals( 5, sequence.getInstructions().length );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk3 );
        assertEquals( MatchResult.REJECT, ctx.getResult() );
    }

    @Test
    public void testOptionalAccept() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );
        
        Label lb1 = SequenceCompiler.newLabel();
        Label lb2 = SequenceCompiler.newLabel();

        // T1? T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .split( lb1, lb2 )
                .match( lb1, tk1 )
                .match( lb2, tk2 )
                .accept()
                .compile();

        assertEquals( 4, sequence.getInstructions().length );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk1 );
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk2 );
        assertEquals( MatchResult.ACCEPT, ctx.getResult() );
    }
    
    @Test
    public void testOptionalSkipAccept() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );
        
        Label lb1 = SequenceCompiler.newLabel();
        Label lb2 = SequenceCompiler.newLabel();

        // T1? T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .split( lb1, lb2 )
                .match( lb1, tk1 )
                .match( lb2, tk2 )
                .accept()
                .compile();

        assertEquals( 4, sequence.getInstructions().length );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk2 );
        assertEquals( MatchResult.ACCEPT, ctx.getResult() );
    }
    
    @Test
    public void testOptionalReject() {
        Token tk1 = new Token( 1 );
        Token tk2 = new Token( 2 );
        Token tk3 = new Token( 3 );
        
        Label lb1 = SequenceCompiler.newLabel();
        Label lb2 = SequenceCompiler.newLabel();

        // T1? T2
        CompiledSequence sequence = SequenceCompiler.newSequence()
                .split( lb1, lb2 )
                .match( lb1, tk1 )
                .match( lb2, tk2 )
                .accept()
                .compile();

        assertEquals( 4, sequence.getInstructions().length );
        
        ThompsonsVM vm = new ThompsonsVM();
        ExecutionContext ctx = vm.createContext( sequence );
        
        assertEquals( MatchResult.UNDEFINED, ctx.getResult() );
        vm.execute( ctx, tk3 );
        assertEquals( MatchResult.REJECT, ctx.getResult() );
    }
}

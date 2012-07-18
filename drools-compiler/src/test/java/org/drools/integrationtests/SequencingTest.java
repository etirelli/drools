package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.StockTick;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class SequencingTest extends CommonTestMethodBase {

    @Test
    public void testSimpleSequence() {
        String drl = "package org.drools\n" +
                     "declare StockTick\n" +
                     "   @role( event ) \n" +
                     "end\n" +
                     "rule SimpleSequence\n" +
                     "when\n" +
                     "    $e1 : StockTick( seq == 1 ) ->\n" +
                     "    $e2 : StockTick( seq == 2 )\n" +
                     "then\n" +
                     "    // do something\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        ksession.insert( new StockTick( 2, "RHT1", 10, 1000 ) );
        ksession.insert( new StockTick( 1, "RHT2", 10, 1000 ) );
        ksession.insert( new StockTick( 2, "RHT3", 10, 1000 ) );
        ksession.insert( new StockTick( 1, "RHT4", 10, 1000 ) );
        
        int fired = ksession.fireAllRules();
        
        assertEquals( 1, fired );
    }

}

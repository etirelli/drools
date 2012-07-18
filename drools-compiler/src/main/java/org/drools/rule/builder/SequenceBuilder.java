/*
 * Copyright 2006 JBoss Inc
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

package org.drools.rule.builder;

import org.drools.RuntimeDroolsException;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.SequenceDescr;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.Sequence;

public class SequenceBuilder
        implements
        RuleConditionBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {
        final SequenceDescr seqDescr = (SequenceDescr) descr;

        final Sequence seq = this.newSequenceFor( seqDescr );
        context.getBuildStack().push( seq );

        if ( prefixPattern != null ) {
            seq.addChild( prefixPattern );
        }

        // iterate over child descriptors
        for ( final BaseDescr child : seqDescr.getDescrs() ) {
            // gets child to build
            child.setResource( context.getRuleDescr().getResource() );
            child.setNamespace( context.getRuleDescr().getNamespace() );

            // gets corresponding builder
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( child.getClass() );

            if ( builder != null ) {
                final RuleConditionElement element = builder.build( context,
                                                                    child );
                // in case there is a problem with the building,
                // builder will return null. Ex: ClassNotFound for the pattern type
                if ( element != null ) {
                    seq.addChild( element );
                }
            } else {
                throw new RuntimeDroolsException( "BUG: no builder found for descriptor class " + child.getClass() );
            }
        }

        context.getBuildStack().pop();
        return seq;
    }

    protected Sequence newSequenceFor(final SequenceDescr seqDescr) {
        switch ( seqDescr.getType() ) {
            case FOLLOWED_BY :
                return new Sequence( Sequence.FOLLOWED_BY );
            case STRICTLY_FOLLOWED_BY :
                return new Sequence( Sequence.STRICTLY_FOLLOWED_BY );
            case LOOSELY_FOLLOWED_BY :
                return new Sequence( Sequence.LOOSELY_FOLLOWED_BY );
            case IND_FOLLOWED_BY :
                return new Sequence( Sequence.INDEPENDENTLY_FOLLOWED_BY );
        }
        throw new RuntimeDroolsException( "BUG: unknown sequence type: " + seqDescr.getType() );
    }

}

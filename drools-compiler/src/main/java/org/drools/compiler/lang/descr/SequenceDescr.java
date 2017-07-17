package org.drools.compiler.lang.descr;

/*
 * Copyright 2005 JBoss Inc
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


import java.util.ArrayList;
import java.util.List;

public class SequenceDescr extends AnnotatedBaseDescr
        implements
        ConditionalElementDescr {

    public static enum SequenceType {
        FOLLOWED_BY("->"),
        STRICTLY_FOLLOWED_BY("=>"),
        LOOSELY_FOLLOWED_BY("~>"),
        IND_FOLLOWED_BY("\\\\");

        private final String operator;
        SequenceType( String operator ) {
            this.operator = operator;
        }
        @Override
        public String toString() {
            return operator;
        }

        public static SequenceType resolve( String operator ) {
            if( FOLLOWED_BY.operator.equals( operator ) ) {
                return FOLLOWED_BY;
            } else if( STRICTLY_FOLLOWED_BY.operator.equals( operator ) ) {
                return STRICTLY_FOLLOWED_BY;
            } else if( LOOSELY_FOLLOWED_BY.operator.equals( operator ) ) {
                return LOOSELY_FOLLOWED_BY;
            } else if( IND_FOLLOWED_BY.operator.equals( operator ) ) {
                return IND_FOLLOWED_BY;
            }
            return null;
        }
    }

    private static final long  serialVersionUID = 550l;
    private SequenceType       type             = SequenceType.FOLLOWED_BY;
    private List<BaseDescr>    descrs           = new ArrayList<BaseDescr>();

    public SequenceDescr() { }

    public SequenceType getType() {
        return type;
    }

    public void setType(SequenceType type) {
        this.type = type;
    }

    public void addDescr(final BaseDescr baseDescr) {
        this.descrs.add( baseDescr );
    }

    public void insertDescr(int index,
                            final BaseDescr baseDescr) {
        this.descrs.add( index,
                         baseDescr );
    }

    public void insertBeforeLast(final Class<?> clazz,
                                 final BaseDescr baseDescr) {
        if ( this.descrs.isEmpty() ) {
            addDescr( baseDescr );
            return;
        }

        for ( int i = this.descrs.size() - 1; i >= 0; i-- ) {
            if ( clazz.isInstance( this.descrs.get( i ) ) ) {
                insertDescr( i,
                             baseDescr );
                return;
            }
        }

        addDescr( baseDescr );
    }

    public List<BaseDescr> getDescrs() {
        return this.descrs;
    }

    public void addOrMerge(final BaseDescr baseDescr) {
        if ( baseDescr instanceof SequenceDescr ) {
            SequenceDescr and = (SequenceDescr) baseDescr;
            for( BaseDescr descr : and.getDescrs() ) {
                addDescr( descr );
                for ( String annKey : and.getAnnotationNames() ) {
                    addAnnotation( and.getAnnotation( annKey ) );
                }
            }
        } else {
            addDescr( baseDescr );
        }
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        return baseDescr == null ? false : descrs.remove(baseDescr);
    }

    public String toString() {
        return "["+type+" "+descrs+" ]";
    }

}
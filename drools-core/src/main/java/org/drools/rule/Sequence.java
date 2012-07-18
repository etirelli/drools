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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sequence extends ConditionalElement
        implements
        Externalizable {

    private static final long          serialVersionUID          = 510l;

    public static final Type           FOLLOWED_BY               = Type.FOLLOWED_BY;
    public static final Type           STRICTLY_FOLLOWED_BY      = Type.STRICTLY_FOLLOWED_BY;
    public static final Type           LOOSELY_FOLLOWED_BY       = Type.LOOSELY_FOLLOWED_BY;
    public static final Type           INDEPENDENTLY_FOLLOWED_BY = Type.INDEPENDENTLY_FOLLOWED_BY;

    private Type                       type                      = null;
    private List<RuleConditionElement> children                  = new ArrayList<RuleConditionElement>();

    private Map<String, Declaration>   outerDeclrarations;

    public Sequence() {
        this( Type.FOLLOWED_BY );
    }

    public Sequence(final Type type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.type = (Type) in.readObject();
        children = (List<RuleConditionElement>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( type );
        out.writeObject( children );
    }

    /**
     * Adds a child to the current Sequence.
     *
     * @param child
     */
    public void addChild(final RuleConditionElement child) {
        // clear the declarations cache
        this.outerDeclrarations = null;
        this.children.add( child );
    }

    /**
     * Adds the given child as the (index)th child of the this GroupElement
     * @param index
     * @param rce
     */
    public void addChild(final int index,
                         final RuleConditionElement rce) {
        // clear the declarations cache
        this.children.add( index,
                           rce );
    }

    public List<RuleConditionElement> getChildren() {
        return this.children;
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    public Map<String, Declaration> getInnerDeclarations() {
        if ( children.isEmpty() ) {
            return Collections.EMPTY_MAP;
        } else if ( children.size() == 1 ) {
            return children.get( 0 ).getOuterDeclarations();
        } else {
            Map<String, Declaration> declarations = new HashMap<String, Declaration>();
            for ( RuleConditionElement re : children ) {
                declarations.putAll( re.getOuterDeclarations() );
            }
            return declarations;
        }
    }

    /**
     * @inheritDoc
     */
    public Map<String, Declaration> getOuterDeclarations() {
        if ( outerDeclrarations != null ) {
            return outerDeclrarations;
        }
        // sequences are not scope delimiters and do not add declarations by themselves, so 
        // outer declarations are the same as inner ones
        outerDeclrarations = getInnerDeclarations();
        return outerDeclrarations;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return getOuterDeclarations().get( identifier );
    }

    /**
     * Optimize the group element subtree by removing redundancies
     * like an AND inside another AND, OR inside OR, single branches
     * AND/OR, etc.
     *
     * LogicTransformer does further, more complicated, transformations
     */
    public void pack(final RuleConditionElement parent) {
        // we must clone, since we want to iterate only over the original list
        // we must clone, since we want to iterate only over the original list
        final RuleConditionElement[] clone = this.children.toArray( new RuleConditionElement[this.children.size()] );
        for ( RuleConditionElement aClone : clone ) {
            aClone.pack( this );
        }
    }

    protected void mergeGroupElements(Sequence parent,
                                      Sequence child) {
        parent.type = child.getType();
        parent.children.clear();
        parent.children.addAll( child.getChildren() );
    }

    public boolean equals(final Object object) {
        // Return false if its null or not an instance of ConditionalElement
        if ( object == null || !(object instanceof Sequence) ) {
            return false;
        }

        // Return true if they are the same reference
        if ( this == object ) {
            return true;
        }

        // Now try a recurse manual check
        final Sequence e2 = (Sequence) object;
        if ( !this.type.equals( e2.type ) ) {
            return false;
        }

        final List<RuleConditionElement> e1Children = this.getChildren();
        final List<RuleConditionElement> e2Children = e2.getChildren();
        if ( e1Children.size() != e2Children.size() ) {
            return false;
        }

        for ( int i = 0; i < e1Children.size(); i++ ) {
            final Object e1Object1 = e1Children.get( i );
            final Object e2Object1 = e2Children.get( i );
            if ( !e1Object1.equals( e2Object1 ) ) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        return this.type.hashCode() + this.children.hashCode();
    }

    /**
     * Clones all Conditional Elements but references the non ConditionalElement
     * children
     *
     * @return
     */
    public Sequence clone() {
        return clone( true );
    }

    public Sequence cloneOnlyGroup() {
        return clone( false );
    }

    protected Sequence clone(boolean deepClone) {
        Sequence cloned = new Sequence();
        cloned.setType( this.getType() );
        for ( RuleConditionElement re : children ) {
            cloned.addChild( deepClone && (re instanceof Sequence || re instanceof Pattern) ? re.clone() : re );
        }
        return cloned;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String toString() {
        return this.type.toString() + this.children.toString();
    }

    public List<RuleConditionElement> getNestedElements() {
        return this.children;
    }

    public boolean isPatternScopeDelimiter() {
        return false;
    }

    /**
     * A public enum for Sequence types
     */
    public enum Type {

        FOLLOWED_BY,
        STRICTLY_FOLLOWED_BY,
        LOOSELY_FOLLOWED_BY,
        INDEPENDENTLY_FOLLOWED_BY;

    }
}

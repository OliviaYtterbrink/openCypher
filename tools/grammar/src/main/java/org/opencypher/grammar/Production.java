package org.opencypher.grammar;

import java.util.Objects;
import java.util.function.Function;

import org.opencypher.tools.xml.Attribute;
import org.opencypher.tools.xml.Child;
import org.opencypher.tools.xml.Element;
import org.opencypher.tools.xml.LocationAware;

@Element(uri = Grammar.XML_NAMESPACE, name = "production")
final class Production implements LocationAware
{
    final String vocabulary;
    @Attribute
    String name;
    private Node definition;
    String description;

    public Production( Root root )
    {
        this.vocabulary = root.language;
    }

    @Child({Alternatives.class, Sequence.class, Literal.class, Characters.class, NonTerminal.class, Optional.class, Repetition.class})
    void add( Node node )
    {
        definition = Sequence.implicit( definition, node.replaceWithVerified() );
    }

    @Child
    void add( Description description )
    {
        if ( this.description != null )
        {
            this.description = description.appendTo( this.description );
        }
        else
        {
            this.description = description.toString();
        }
    }

    @Child
    final void literal( char[] buffer, int start, int length )
    {
        Literal.fromCharacters( buffer, start, length, this::add );
    }

    <EX extends Exception> void accept( GrammarVisitor<EX> visitor ) throws EX
    {
        visitor.visitProduction( name, definition == null ? Node.EPSILON : definition );
    }

    <EX extends Exception> void nonTerminalVisit( GrammarVisitor<EX> visitor ) throws EX
    {
        visitor.visitNonTerminal( name, definition );
    }

    void resolve( Function<String, Production> productions, Dependencies dependencies )
    {
        if ( definition != null )
        {
            definition.resolve( this, productions, dependencies );
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name ) + Objects.hashCode( definition ) * 31;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj.getClass() != Production.class )
        {
            return false;
        }
        Production that = (Production) obj;
        return Objects.equals( this.name, that.name ) && Objects.equals( this.definition, that.definition );
    }

    @Override
    public String toString()
    {
        return "Production{" + vocabulary + " / " + name + " = " + definition + "}";
    }

    @Override
    public void location( String systemId, int lineNumber, int columnNumber )
    {
        throw new UnsupportedOperationException( "not implemented" );
    }
}

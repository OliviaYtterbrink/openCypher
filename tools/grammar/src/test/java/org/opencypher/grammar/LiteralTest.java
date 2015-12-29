package org.opencypher.grammar;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LiteralTest
{
    @Test
    public void shouldReplaceControlCharacterLiteralWithCharacterReference() throws Exception
    {
        // given
        StringBuilder value = new StringBuilder( 1 );
        for ( int cp = 0; cp < 0x20; cp++ )
        {
            value.setLength( 0 );
            value.appendCodePoint( cp );
            Literal literal = new Literal();
            literal.value = value.toString();

            // when
            Node replaced = literal.replaceWithVerified();

            // then
            assertThat( replaced, instanceOf( Characters.class ) );
            int codepoint = Characters.codePoint( ((Characters) replaced).set );
            assertEquals( cp, codepoint );
        }
    }
}

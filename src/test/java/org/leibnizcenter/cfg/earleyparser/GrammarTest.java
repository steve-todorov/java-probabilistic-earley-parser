
package org.leibnizcenter.cfg.earleyparser;

import org.junit.Assert;
import org.junit.Test;
import org.leibnizcenter.cfg.algebra.semiring.dbl.ProbabilitySemiring;
import org.leibnizcenter.cfg.category.Category;
import org.leibnizcenter.cfg.category.nonterminal.NonTerminal;
import org.leibnizcenter.cfg.category.terminal.stringterminal.ExactStringTerminal;
import org.leibnizcenter.cfg.grammar.Grammar;
import org.leibnizcenter.cfg.rule.LexicalErrorRule;
import org.leibnizcenter.cfg.rule.Rule;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 */
public class GrammarTest {
    private static final ProbabilitySemiring sr = ProbabilitySemiring.get();

    private final static NonTerminal A = Category.nonTerminal("A");
    private final static NonTerminal B = Category.nonTerminal("B");
    private final static NonTerminal C = Category.nonTerminal("C");
    private final static NonTerminal D = Category.nonTerminal("D");
    private final static NonTerminal E = Category.nonTerminal("E");
    private final static NonTerminal X = Category.nonTerminal("X");
    private final static NonTerminal Y = Category.nonTerminal("Y");
    private final static NonTerminal Z = Category.nonTerminal("Z");
    private final static Category e = new ExactStringTerminal("e");
    private final static Category a = new ExactStringTerminal("a");

    private final static Rule rule1 = Rule.create(sr, 0.5, A, B, C, D, E);
    private final static Rule rule2 = Rule.create(sr, 0.5, A, e);
    private final static Rule rule3 = Rule.create(sr, 1.0, X, Y, Z);
    private static final Rule ruleB = Rule.create(sr, 0.5, B, C);
    private static final Rule ruleC = Rule.create(sr, 0.5, C, D);
    private static final Rule ruleD = Rule.create(sr, 0.5, D, E);
    private static final Rule ruleDe = Rule.create(sr, 0.2, D, E, e);
    private static final Rule ruleDa = Rule.create(sr, 0.5, D, a);
    private static final Rule ruleEE = Rule.create(sr, 0.5, E, E, E);
    private static final Rule ruleE = Rule.create(sr, 0.5, E, e);
    private static final Rule ruleEC = Rule.create(sr, 0, E, C);

    @Test
    public final void testContainsRules() {
        final Grammar<String> g = makeDefaultGrammar();

        assertTrue(g.containsRules(rule1.left));
        assertTrue(g.getRules(rule2.left).contains(rule2));
        Assert.assertFalse(g.getRules(rule3.left).contains(rule2));

        assertEquals(ruleB, Rule.create(sr, 0.5, B, C));
        assertEquals(ruleC, Rule.create(sr, 0.5, C, D));
        assertEquals(ruleD, ruleD);
        assertEquals(ruleE, ruleE);
        assertEquals(rule1, rule1);
        assertEquals(rule2, rule2);
        assertEquals(rule3, rule3);

        Assert.assertNotEquals(Rule.create(sr, 1.0, X, e), Rule.create(sr, 1.0, A, e));
        Assert.assertNotEquals(Rule.create(sr, 1.0, X, e), Rule.create(sr, 0.5, X, e));
        assertEquals(Rule.create(sr, 1.0, X, e), Rule.create(sr, 1.0, X, e));
    }

    private static Grammar<String> makeDefaultGrammar() {
        return new Grammar.Builder<String>("test")
                .withSemiring(sr)
                .addRule(ruleB)
                .addRule(ruleC)
                .addRule(ruleD)
                .addRule(ruleDa)
                .addRule(ruleE)
                .addRule(ruleEE)
                .addRule(ruleEC)
                .addRule(rule1)
                .addRule(rule2)
                .addRule(rule3)
                .build(true);
    }

    @Test
    public final void testLeftRelation() {
        final Grammar<String> g = new Grammar.Builder<String>("test")
                .withSemiring(sr)
                .addRule(ruleB)
                .addRule(ruleC)
                .addRule(ruleD)
                .addRule(ruleDe)
                .addRule(ruleDa)
                .addRule(ruleE)
                .addRule(ruleEE)
                .addRule(ruleEC)
                .addRule(rule1)
                .addRule(rule2)
                .addRule(rule3)
                .build(true);
        assertEquals(g.getLeftScore(A, B), 0.5, 0.001);
        assertEquals(g.getLeftScore(A, D), 0.0, 0.001);
        assertEquals(g.getLeftScore(A, X), 0.0, 0.001);
        assertEquals(g.getLeftScore(B, C), 1.0, 0.001);
        assertEquals(g.getLeftScore(D, E), .07/.12, 0.001);
    }

    @Test
    public final void testLeftStarRelation() {
        final Grammar<String> g = makeDefaultGrammar();
        assertEquals(g.getLeftStarScore(A, B), 0.5, 0.01);
        assertEquals(g.getLeftStarScore(B, C), 1.0, 0.01);
        assertEquals(g.getLeftStarScore(B, D), 1.0, 0.01);
        assertEquals(g.getLeftStarScore(A, D), 0.5, 0.01);
        assertEquals(sr.toProbability(g.getLeftStarScore(A, X)), 0.0, 0.01);
    }

//    @Test public final void testGetPreterminal() {
//		Assert.assertEquals(rule2,
//				g.getPreterminals(rule2, rule2.right[0].name, true));
//		Assert.assertEquals(null,
//				g.getPreterminals(rule2, rule2.right[0].name.toUpperCase(),
//						false));
//}

    /**
     * Test method for {@link Grammar#getRules(NonTerminal)}.
     */
    @Test
    public final void testGetRules() {
        final Grammar<String> g = makeDefaultGrammar();
        final Set<Rule> setOfrules = new HashSet<>();
        setOfrules.add(rule1);
        setOfrules.add(rule2);
        assertEquals(setOfrules, new HashSet<>(g.getRules(rule1.left)));
        assertEquals(setOfrules, new HashSet<>(g.getRules(rule2.left)));

        setOfrules.clear();
        setOfrules.add(rule3);
        assertEquals(setOfrules, new HashSet<>(g.getRules(rule3.left)));
    }

    @Test
    public final void parseErrorRule() throws IOException {
        final Grammar<String> grammar = Grammar.fromString(
                "S->NP(1.0)#comment\n#comment\n\n#\n   #  com\n  \n\n VP -> eat <error> clar (1.0)\n"
        );
        final Collection<Rule> errorRules = grammar.getRules(NonTerminal.of("VP"));
        assertNotNull(errorRules);
        assertEquals(1, errorRules.size());
        final Rule errorRule = errorRules.iterator().next();
        assertTrue(errorRule instanceof LexicalErrorRule);
    }

    @Test
    public final void parseRightRecursive() throws IOException {
        final Grammar<String> grammar = new Grammar.Builder<String>("test")
                .withSemiring(sr)
                .addRule(Rule.create(sr, 0.9999999999, A, A, a))
                .addRule(Rule.create(sr, 0.0000000001, A, a))
                .addRule(Rule.create(sr, 0.8, B, B, a, e))
                .addRule(Rule.create(sr, 0.2, B, e, a, e))
                .build();

        final Collection<Rule> errorRules = grammar.getRules(NonTerminal.of("VP"));
        assertNull(errorRules);
    }

}

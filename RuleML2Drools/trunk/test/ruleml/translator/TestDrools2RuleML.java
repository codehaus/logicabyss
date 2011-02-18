package ruleml.translator;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.compiler.DrlParser;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ruleml.translator.drl2ruleml.Drools2RuleMLTranslator;

public class TestDrools2RuleML extends TestCase{

	@Test
	public void test_Assert() {
		System.out
				.println("------------------------   Drl -> RuleML : TEST ASSERT ------------------------");
		KnowledgeRuntimeLogger logger = null;
		try {
			Resource resource = ResourceFactory
					.newClassPathResource("drools/test_assert.drl");
			final DrlParser parser = new DrlParser();
			final PackageDescr pkgDescr = parser.parse(resource
					.getInputStream());

			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test_assert.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			String result = Drools2RuleMLTranslator.translate(kbase, pkgDescr);

			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
					"<RuleML xmlns=\"http://www.ruleml.org/1.0/xsd\">\n" + 
					"    <Assert>\n" + 
					"        <Rule>\n" + 
					"            <if>\n" + 
					"                <And>\n" + 
					"                    <And>\n" + 
					"                        <Atom>\n" + 
					"                            <op>\n" + 
					"                                <Rel>LessThan</Rel>\n" + 
					"                            </op>\n" + 
					"                            <Var>VAR1</Var>\n" + 
					"                            <Ind>13</Ind>\n" + 
					"                        </Atom>\n" + 
					"                        <Atom>\n" + 
					"                            <op>\n" + 
					"                                <Rel>Person</Rel>\n" + 
					"                            </op>\n" + 
					"                            <slot>\n" + 
					"                                <Ind>name</Ind>\n" + 
					"                                <Ind>john</Ind>\n" + 
					"                            </slot>\n" + 
					"                            <slot>\n" + 
					"                                <Ind>age</Ind>\n" + 
					"                                <Var>VAR1</Var>\n" + 
					"                            </slot>\n" + 
					"                        </Atom>\n" + 
					"                    </And>\n" + 
					"                    <Atom>\n" + 
					"                        <op>\n" + 
					"                            <Rel>Buy</Rel>\n" + 
					"                        </op>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>buyer</Ind>\n" + 
					"                            <Var>$person</Var>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>seller</Ind>\n" + 
					"                            <Var>$merchant</Var>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>item</Ind>\n" + 
					"                            <Var>$object</Var>\n" + 
					"                        </slot>\n" + 
					"                    </Atom>\n" + 
					"                    <Atom>\n" + 
					"                        <op>\n" + 
					"                            <Rel>Keep</Rel>\n" + 
					"                        </op>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>keeper</Ind>\n" + 
					"                            <Var>$person</Var>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>item</Ind>\n" + 
					"                            <Ind>test</Ind>\n" + 
					"                        </slot>\n" + 
					"                    </Atom>\n" + 
					"                </And>\n" + 
					"            </if>\n" + 
					"            <do>\n" + 
					"                <Assert>\n" + 
					"                    <Atom>\n" + 
					"                        <Rel>Own</Rel>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>item</Ind>\n" + 
					"                            <Var>$person</Var>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>owner</Ind>\n" + 
					"                            <Var>$object</Var>\n" + 
					"                        </slot>\n" + 
					"                    </Atom>\n" + 
					"                </Assert>\n" + 
					"            </do>\n" + 
					"        </Rule>\n" + 
					"    </Assert>\n" + 
					"</RuleML>\n";
			
			
			System.out.println(result);
			
			assertEquals(expected, result);
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		} finally {
			logger.close();
		}
	}

	@Test
	public void test_Retract() {

		System.out
				.println("------------------------   Drl -> RuleML : TEST RETRACT ------------------------");
		KnowledgeRuntimeLogger logger = null;
		try {
			Resource resource = ResourceFactory
					.newClassPathResource("drools/test_retract.drl");
			final DrlParser parser = new DrlParser();
			final PackageDescr pkgDescr = parser.parse(resource
					.getInputStream());

			// load up the knowledge base
			KnowledgeBase kbase = Drools2RuleMLTranslator
					.readKnowledgeBase("drools/test_retract.drl");
			StatefulKnowledgeSession ksession = kbase
					.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory
					.newFileLogger(ksession, "test");

			String result = Drools2RuleMLTranslator.translate(kbase, pkgDescr);
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
					"<RuleML xmlns=\"http://www.ruleml.org/1.0/xsd\">\n" + 
					"    <Assert>\n" + 
					"        <Rule>\n" + 
					"            <if>\n" + 
					"                <And>\n" + 
					"                    <Atom>\n" + 
					"                        <op>\n" + 
					"                            <Rel>Buy</Rel>\n" + 
					"                        </op>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>buyer</Ind>\n" + 
					"                            <Ind>Ti6o</Ind>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>seller</Ind>\n" + 
					"                            <Ind>Dealer</Ind>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>item</Ind>\n" + 
					"                            <Ind>Objective</Ind>\n" + 
					"                        </slot>\n" + 
					"                    </Atom>\n" + 
					"                    <Atom>\n" + 
					"                        <op>\n" + 
					"                            <Rel>Person</Rel>\n" + 
					"                        </op>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>name</Ind>\n" + 
					"                            <Var>$buyer</Var>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>age</Ind>\n" + 
					"                            <Var></Var>\n" + 
					"                        </slot>\n" + 
					"                    </Atom>\n" + 
					"                </And>\n" + 
					"            </if>\n" + 
					"            <do>\n" + 
					"                <Retract>\n" + 
					"                    <Atom>\n" + 
					"                        <op>\n" + 
					"                            <Rel>Buy</Rel>\n" + 
					"                        </op>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>buyer</Ind>\n" + 
					"                            <Ind>Ti6o</Ind>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>seller</Ind>\n" + 
					"                            <Ind>Dealer</Ind>\n" + 
					"                        </slot>\n" + 
					"                        <slot>\n" + 
					"                            <Ind>item</Ind>\n" + 
					"                            <Ind>Objective</Ind>\n" + 
					"                        </slot>\n" + 
					"                    </Atom>\n" + 
					"                </Retract>\n" + 
					"            </do>\n" + 
					"        </Rule>\n" + 
					"    </Assert>\n" + 
					"</RuleML>\n";
			
			System.out.println(result);
			assertEquals(expected, result);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		} finally {
			logger.close();
		}
	}
}

<?xml version="1.0" encoding="UTF-8"?> 
<!--	

	Translation based on RuleML 0.91, Reaction RuleML 0.1 and RBSLA 0.3:

			RuleML 0.91 : 	http://www.ruleml.org/0.91/xsd

			RBSLA 0.2 :	    http://ibis.in.tum.de/projects/rbsla

			RBSLA 0.2 Schema

			Layers -	http://ibis.in.tum.de/staff/paschke/docs/rbsla/0.2/:
						hornlog2rbsla.xsd
						eca.xsd
						event_calculus.xsd
						deontic.xsd
						defeasible.xsd
		
			Modules - http://ibis.in.tum.de/staff/paschke/docs/rbsla/0.2/modules/:
						root_module.xsd
						attachment_module.xsd
						attribute_module.xsd
						testcases_module.xsd
						update_module.xsd
						eca_module.xsd
						events_module.xsd
						event-algebra_module.xsd
						deontic_module.xsd
						defeasible_module.xsd

-->
<xsl:stylesheet  version="1.0"	
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2004/07/xpath-functions" 		
	xmlns:ruleml="http://www.ruleml.org/0.91/xsd">
<xsl:output method="text"	encoding="UTF-8"/>
<xsl:strip-space elements="*"/>
<xsl:variable name="alphabet" select=" 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ' "/>

<!-- RULEML | ECA-RuleML | RBSLA -->
<xsl:template match="ruleml:RuleML | ruleml:ECA-RuleML | ruleml:RBSLA">
	<xsl:if test="comment()">
		<xsl:apply-templates select="comment()"/>
	</xsl:if>
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<!-- add a dot and a new line after processing an element-->
	<xsl:for-each select="./ruleml:Message | ./ruleml:Assert | ./ruleml:Query | ./ruleml:Protect | ./ruleml:Retract">
		<xsl:apply-templates select="."/>
	</xsl:for-each>
</xsl:template>

<!-- OID -->
<xsl:template match="ruleml:oid">						
		<xsl:if test="name(..)= 'Message'">	
			<xsl:value-of select="."/>
		</xsl:if>
		<xsl:if test="name(..)!= 'Message' and name(..)!= 'Overrides'">	
			<!-- new line -->		
			<xsl:text>&#10;</xsl:text>	
			<xsl:for-each select=".">
				<!-- ch <xsl:value-of select="descendant::*[name()]"/>]<xsl:text>&#10;</xsl:text> -->
				<xsl:call-template name="comments">
						<xsl:with-param name="string" select="descendant::*[name()]"/>
						<!-- &#xA;  - escape character for new line (\n) -->
						<xsl:with-param name="break" select=" '&#xA;' "/>
						<xsl:with-param name="prefix" select=" '%' "/>
				</xsl:call-template>
			</xsl:for-each>	
		</xsl:if>				
</xsl:template>

<!-- ECA -->
<!--
<xsl:template match="ruleml:ECA">
	<xsl:if test="./ruleml:time and ./ruleml:event and ./ruleml:condition and ./ruleml:action">
				<xsl:call-template name="handle_rbsla_eca">
					<xsl:with-param name="time" select="./ruleml:time"/>
					<xsl:with-param name="event" select="./ruleml:event"/>
					<xsl:with-param name="condition" select="./ruleml:condition"/>
					<xsl:with-param name="action" select="./ruleml:action"/>
				</xsl:call-template>
	</xsl:if>
	<xsl:if test="not(./ruleml:time) or not(./ruleml:action) or not(./ruleml:event) or not(./ruleml:condition)">
				<xsl:call-template name="handle_rbsla_eca">
					<xsl:with-param name="time" select="descendant::*[1]"/>
					<xsl:with-param name="event" select="descendant::*[2]"/>
					<xsl:with-param name="condition" select="descendant::*[3]"/>
					<xsl:with-param name="action" select="descendant::*[4]"/>
				</xsl:call-template>
	</xsl:if>
</xsl:template>
-->


<!-- ASSERT 
		See http://www.ruleml.org/0.91/glossary/#gloss-Assert 
		content model: 	
			( 	
				oid?, 
				(formula | Rulebase | Atom | Implies | Reaction | Equivalent | Entails | Forall)* 
			)

		Note: oid -> translate as module
-->
<xsl:template match="ruleml:Assert">

	<!-- oid after Assert is usually ignored or treated as a comment -->
	<xsl:if test="./ruleml:oid">
			<xsl:if test="name(..) ='RBSLA'"> 
				<xsl:if test="not(./ruleml:Initiates) and not(./ruleml:Terminates)">
						<xsl:text>:-eval(add(</xsl:text>
						<xsl:apply-templates select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var"/>
						<xsl:if test="./ruleml:Atom">
							<xsl:text>,"</xsl:text>
							<xsl:for-each select="./ruleml:Atom">
								<xsl:apply-templates select="."/><xsl:text>.</xsl:text><xsl:if test="count(following-sibling::*) > 0"><xsl:text> </xsl:text></xsl:if>
							</xsl:for-each>
							<xsl:text>"</xsl:text>
						</xsl:if>
						<xsl:text>)).</xsl:text><xsl:text>&#10;</xsl:text>
				</xsl:if>
				<xsl:if test="./ruleml:Initiates or ./ruleml:Terminates">
					<xsl:apply-templates select="./ruleml:Initiates or ./ruleml:Terminates"/>
				</xsl:if>
			</xsl:if>
			
			<xsl:if test="name(..) ='RuleML'"> 
				<xsl:if test="not(./ruleml:Initiates) and not(./ruleml:Terminates)">
						<xsl:text>:-eval(add(</xsl:text>
						<xsl:apply-templates select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var"/>						
							<xsl:for-each select="./ruleml:Atom">
								<xsl:if test="count(following-sibling::*) > 0">
									<xsl:text>,"</xsl:text>
								</xsl:if>
								<xsl:apply-templates select="."/>
									<xsl:text>.</xsl:text>
								<xsl:if test="count(following-sibling::*) > 0">
									<xsl:text> </xsl:text>
								</xsl:if>
								<xsl:if test="count(following-sibling::*) = 0">
									<xsl:text>"</xsl:text>
								</xsl:if>
							</xsl:for-each>					
						<xsl:text>)).</xsl:text><xsl:text>&#10;</xsl:text>
				</xsl:if>
				<xsl:if test="./ruleml:Initiates or ./ruleml:Terminates">
					<xsl:apply-templates select="./ruleml:Initiates or ./ruleml:Terminates"/>
				</xsl:if>
			</xsl:if>
				
			<xsl:if test="name(..) ='body'">
					<xsl:text>add(</xsl:text>
					<xsl:apply-templates select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var"/>
					<xsl:if test="./ruleml:Atom">
						<xsl:text>,"</xsl:text>
						<xsl:for-each select="./ruleml:Atom | ./ruleml:Implies">
							<xsl:apply-templates select="."/><xsl:text>.</xsl:text><xsl:if test="count(following-sibling::*) > 0"><xsl:text> </xsl:text></xsl:if>
						</xsl:for-each>
						<xsl:text>"</xsl:text>
					</xsl:if>
					<xsl:text>)</xsl:text>
			</xsl:if>
				
			<xsl:if test="name(..) ='action' ">		
					<xsl:text>add(</xsl:text>
					<xsl:for-each select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var | ./ruleml:Atom | ./ruleml:Implies | ./ruleml:formula  | ./ruleml:Expr | ./ruleml:oid/ruleml:Expr">
						<xsl:apply-templates select="."/>
						<xsl:if test="count(following::*) > 0"><xsl:text>,</xsl:text></xsl:if>
					</xsl:for-each>												
					<xsl:text>)</xsl:text>
			</xsl:if>	
			
			
			<xsl:if test="name(..) ='And'">
					<xsl:text>add(</xsl:text>
					<xsl:apply-templates select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var"/>
					<xsl:if test="./ruleml:Atom | ./ruleml:Implies">
						<xsl:text>,"</xsl:text>
						<xsl:for-each select="./ruleml:Atom | ./ruleml:Implies">
							<xsl:apply-templates select="."/><xsl:text>.</xsl:text><xsl:if test="count(following-sibling::*) > 0"><xsl:text> </xsl:text></xsl:if>
						</xsl:for-each>
						<xsl:text>"</xsl:text>
					</xsl:if>
					<xsl:text>)</xsl:text>
			</xsl:if>
			<xsl:if test="name(..) ='Reaction' ">
					<xsl:text>add(</xsl:text>
					<xsl:for-each select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var | ./ruleml:Atom | ./ruleml:Implies | ./ruleml:formula ">
								<xsl:apply-templates select="."/><xsl:if test="count(following::*) > 0"><xsl:text>,</xsl:text></xsl:if>
					</xsl:for-each>						
					<xsl:text>)</xsl:text>
			</xsl:if>		
				
			<xsl:if test="not(name(..) ='RBSLA') and not(name(..) ='RuleML') and not(name(..) = 'body') and not(name(..) = 'action') and not(name(..) = 'Reaction')">
					<xsl:apply-templates select="./ruleml:oid"/>
			</xsl:if>

	</xsl:if>

	<xsl:if test="not(./ruleml:oid)">
	<xsl:if test="./ruleml:formula | ./ruleml:Rulebase | ./ruleml:Atom | ./ruleml:Implies | ./ruleml:Initiates | 								                  ./ruleml:Terminates |./ruleml:Reaction | ./ruleml:Equivalent | ./ruleml:Entails | ./ruleml:Forall |                  ./ruleml:ECA | ./ruleml:Overrides">
				<xsl:for-each select="	./ruleml:formula | ./ruleml:Rulebase | ./ruleml:Atom | ./ruleml:Implies |                                        ./ruleml:Initiates | ./ruleml:Terminates | ./ruleml:Reaction | ./ruleml:Equivalent |						                                        ./ruleml:Entails | ./ruleml:Forall | ./ruleml:ECA | ./ruleml:Overrides">
					<!-- new line 
					<xsl:text>&#10;</xsl:text> and not(name(descendant::*) = 'Reaction') -->
		<!--			<xsl:if test="not(name(../.) = 'action') "> <xsl:text>.</xsl:text><xsl:text>&#10;</xsl:text> -->
						<xsl:apply-templates select="."/>
						
	<xsl:if test="(name(..) = 'RuleML' or name(..) = 'RBSLA' or name(../..) = 'RBSLA' or name(../..) = 'RuleML') ">
		<xsl:text>.</xsl:text><xsl:text>&#10;</xsl:text>
	</xsl:if>
		<!--			</xsl:if> -->
				</xsl:for-each>		
			</xsl:if>
	</xsl:if>
</xsl:template>


<!--

		*** Rulebase ***
		A collection of rules that can be ordered or unordered, without or with duplicates.

		See http://www.ruleml.org/0.91/glossary/#gloss-Rulebase
		
		content model:
		( oid?, (formula | Atom | Implies | Equivalent | Forall | Equal)* )


-->
<xsl:template match="ruleml:Rulebase">

	<!--<xsl:if test="not(./ruleml:oid)">-->
	<xsl:if test="./ruleml:formula | ./ruleml:Atom | ./ruleml:Implies | ./ruleml:Equivalent | ./ruleml:Equal | ./ruleml:Forall">
		<xsl:for-each select="	./ruleml:formula | ./ruleml:Atom | ./ruleml:Implies | ./ruleml:Equal | ./ruleml:Equivalent |	 ./ruleml:Forall">			
				<xsl:apply-templates select="."/>					
				<xsl:text>.</xsl:text><xsl:text>&#10;</xsl:text>
		</xsl:for-each>		
	</xsl:if>
	<!--</xsl:if>-->
</xsl:template>


<!-- FORMULA -->
<xsl:template match="ruleml:formula">
	<xsl:text>"</xsl:text><xsl:apply-templates select="./ruleml:Happens"/>
	<xsl:if test="./ruleml:Atom">
		<xsl:apply-templates select="./ruleml:Atom"/>
	</xsl:if>
	<xsl:text>."</xsl:text>
</xsl:template>

<!-- INITIATES -->
<xsl:template match="ruleml:Initiates">	

		<xsl:for-each select="./ruleml:event | ./ruleml:fluent | ./ruleml:time | ./ruleml:Expr | ./ruleml:Var | ./ruleml:Ind | ./ruleml:Expr">
		<xsl:text>initially(</xsl:text>
			<xsl:apply-templates select="."/>
			<xsl:if test="count(following-sibling::*) > 0">
				<xsl:text>,</xsl:text>
			</xsl:if>						
		</xsl:for-each>			
		<xsl:for-each select="./ruleml:state/ruleml:Expr">
			<xsl:apply-templates select="."/>
			<xsl:if test="count(following-sibling::*) > 0">
				<xsl:text>,</xsl:text>
			</xsl:if>
			<xsl:if test="count(following-sibling::*) = 0">
				<xsl:text>,T</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="./ruleml:norm">
			<xsl:apply-templates select="."/>
			<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
		</xsl:for-each>
</xsl:template>

<!-- TERMINATES -->
<xsl:template match="ruleml:Terminates">	
		<!--<xsl:text>terminates(</xsl:text>-->
		<xsl:for-each select="./ruleml:event | ./ruleml:fluent | ./ruleml:time | ./ruleml:Expr | ./ruleml:Var | ./ruleml:Ind">
			<xsl:apply-templates select="."/>
			<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
	<xsl:text>)</xsl:text>
		</xsl:for-each>
	<xsl:for-each select="./ruleml:state/ruleml:Expr">
			<xsl:apply-templates select="."/>
			<xsl:if test="count(following-sibling::*) > 0">
				<xsl:text>,</xsl:text>
			</xsl:if>
			<xsl:if test="count(following-sibling::*) = 0">
				<xsl:text>,T</xsl:text>
			</xsl:if>
		</xsl:for-each>
</xsl:template>

<!-- HAPPENS -->
<xsl:template match="ruleml:Happens">
	<xsl:text>happens(</xsl:text>
	<xsl:for-each select="./ruleml:event | ./ruleml:time">
		<xsl:apply-templates select="."/><xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
	</xsl:for-each>
	<xsl:text>)</xsl:text>
</xsl:template>

<!-- RETRACT -->
<xsl:template match="ruleml:Retract">
		<xsl:if test="./ruleml:oid">
	
<!--				<xsl:if test="name(..) ='RBSLA' "> -->
					<!--<xsl:text>:-eval(remove("</xsl:text>-->
					<xsl:text>:-eval(remove(</xsl:text>
					<xsl:apply-templates select="./ruleml:oid/ruleml:Ind | ./ruleml:oid/ruleml:Var"/>										
						<xsl:for-each select="./ruleml:Atom">
						<xsl:text>,"</xsl:text>
						<xsl:apply-templates select="."/>
							<xsl:text>.</xsl:text>
						</xsl:for-each>
					
					<!--<xsl:text>")).</xsl:text><xsl:text>&#10;</xsl:text>-->
					<xsl:text>).</xsl:text><xsl:text>&#10;</xsl:text>
<!--				</xsl:if> -->
				
<!--				<xsl:if test="not(name(..) ='RBSLA') ">
					<xsl:apply-templates select="./ruleml:oid"/>
				</xsl:if> -->

		</xsl:if>
		
		<xsl:if test="not(./ruleml:oid)">
			<xsl:if test="	./ruleml:formula | ./ruleml:Rulebase  | ./ruleml:Implies | ./ruleml:Atom | 
									./ruleml:Reaction | ./ruleml:Equivalent | ./ruleml:Entails | ./ruleml:Forall | ./ruleml:ECA">
				<xsl:for-each select="	./ruleml:formula | ./ruleml:Rulebase | ./ruleml:Atom | ./ruleml:Implies | 
														./ruleml:Reaction | ./ruleml:Equivalent | ./ruleml:Entails | ./ruleml:Forall | ./ruleml:ECA">
						<xsl:apply-templates select="."/>	
				</xsl:for-each>					
				<!-- ENDPOINT -->
				<!--<xsl:text>.</xsl:text>-->		
			</xsl:if>

	</xsl:if>
</xsl:template>

<!-- REACTION *** Reaction ***
      An Reaction element that provides the basic syntax for reaction rules.

      content model: 
		  (
				oid?,  
				(event | Naf | Neg | Atom | Reaction | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic)?, 
				(body | Naf | Neg | Atom | And | Or)?,  
				(action | Atom | Assert | Retract | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic)?, 
				(postcond  | Naf | Neg | Atom | And | Or)?,  
				(alternative | Atom | Assert | Retract | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic)?
		)
		
	 However this is non-deterministic. Hence the redefined content model is:
	 
		( (event, ( (	body, 	(action)?, 	(postcond)?, 	(alternative)? ) |( action, (postcond)?, (alternative)? ) )? )| ( body, action, (postcond)?, (alternative)? ) ( action, | (postcond)?, (alternative)? ) ) |
	 (Naf | Neg | Atom | Assert | Retract | And | Or | Reaction | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic)+

					event,
					   (
					     ( body, (action)?, (postcond)?, (alternative)? )
					     |
					     ( action, (postcond)?, (alternative)? )
					   )?
						(  body, action, (postcond)?, (alternative)? )

						( action, (postcond)?, (alternative)? ) 
	 
	 Note: The validation of the role-skipped content model (second choice) interprets the required @kind attribute and uses the patterns defined in the attribute for the validation
	
      
      Reaction has the following attributes:
      @kind, @eval, @exec
-->
<xsl:template match="ruleml:Reaction">

	<xsl:if test="not(@exec)">
	</xsl:if>
	
	<xsl:if test="@exec">
			<!-- @kind IS defined -->
			<xsl:if test="@kind">
	<!-- TEST 	
			<xsl:text>[</xsl:text><xsl:value-of select="@kind"/><xsl:text>]&#10;</xsl:text> -->
<!--		-->
				
					<xsl:if test="@kind = 'e' ">
							<xsl:if test="./ruleml:event">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="not(./ruleml:event)">
								<xsl:call-template name="handle_eca">
<!--										<xsl:with-param name="event" select="descendant::node()[1]"/> -->
										<xsl:with-param name="event" select="descendant::*[1]"/>
								</xsl:call-template>
							</xsl:if>
					</xsl:if>
					
					
					<xsl:if test="@kind = 'a' ">
						<xsl:if test="./ruleml:action">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="./ruleml:action"/>
								</xsl:call-template>
						</xsl:if>
						<xsl:if test="not(./ruleml:action)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="descendant::*[1]"/>
								</xsl:call-template>
						</xsl:if>
					</xsl:if>

					<xsl:if test="@kind = 'ec' ">
							<xsl:if test="./ruleml:event and ./ruleml:body">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="body" select="./ruleml:body"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:body)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="body" select="descendant::*[2]"/>
								</xsl:call-template>
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'eca' ">
						<xsl:if test="./ruleml:event and ./ruleml:body and ./ruleml:action">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
								</xsl:call-template>
						</xsl:if>
						<xsl:if test="not(./ruleml:event) or not(./ruleml:body) or not(./ruleml:action)">											
								<xsl:call-template name="handle_eca">
										<!--<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="body" select="descendant::*[2]"/>
										<xsl:with-param name="action" select="descendant::*[3]"/>-->	
										<xsl:with-param name="event" select="child::*[1]"/>
										<xsl:with-param name="body" select="child::*[2]"/>
										<xsl:with-param name="action" select="child::*[3]"/>									
								</xsl:call-template>
						</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'ecap' ">
							<xsl:if test="./ruleml:event and ./ruleml:body and ./ruleml:action and ./ruleml:postcond">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:body) or not(./ruleml:action) or not(./ruleml:postcond)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="body" select="descendant::*[2]"/>
										<xsl:with-param name="action" select="descendant::*[3]"/>
										<xsl:with-param name="postcond" select="descendant::*[4]"/>
								</xsl:call-template>
							</xsl:if>
					</xsl:if>
					
					
					<xsl:if test="@kind = 'ecapa' ">
							<xsl:if test="./ruleml:event and ./ruleml:body and ./ruleml:action and ./ruleml:postcond and ./ruleml:alternative">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>					
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:body) or not(./ruleml:action) or not(./ruleml:postcond) or not(./ruleml:alternative)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="body" select="descendant::*[2]"/>
										<xsl:with-param name="action" select="descendant::*[3]"/>
										<xsl:with-param name="postcond" select="descendant::*[4]"/>
										<xsl:with-param name="alternative" select="descendant::*[5]"/>
								</xsl:call-template>					
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'ecaa' ">
							<xsl:if test="./ruleml:event and ./ruleml:body and ./ruleml:action and ./ruleml:alternative">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>					
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:body) or not(./ruleml:action) or not(./ruleml:alternative)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="body" select="descendant::*[2]"/>
										<xsl:with-param name="action" select="descendant::*[3]"/>
										<xsl:with-param name="alternative" select="descendant::*[4]"/>
								</xsl:call-template>					
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'ea' ">
							<xsl:if test="./ruleml:event and ./ruleml:action">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
								</xsl:call-template>					
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:action)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="child::*[1]"/>
										<xsl:with-param name="action" select="child::*[2]"/>
								</xsl:call-template>	
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'eap' ">
							<xsl:if test="./ruleml:event and ./ruleml:action and ./ruleml:postcond">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:action) or not(./ruleml:postcond)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
										<xsl:with-param name="postcond" select="descendant::*[3]"/>
								</xsl:call-template>	
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'eapa' ">
							<xsl:if test="./ruleml:event and ./ruleml:action and ./ruleml:postcond and ./ruleml:alternative">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:action) or not(./ruleml:postcond) or not(./ruleml:alternative)">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
										<xsl:with-param name="postcond" select="descendant::*[3]"/>
										<xsl:with-param name="alternative" select="descendant::*[4]"/>
								</xsl:call-template>		
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'eaa' ">
							<xsl:if test="./ruleml:event and ./ruleml:action and ./ruleml:alternative">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="./ruleml:event"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:event) or not(./ruleml:action) or not(./ruleml:alternative)">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
										<xsl:with-param name="alternative" select="descendant::*[3]"/>
								</xsl:call-template>		
							</xsl:if>
					</xsl:if>

					<xsl:if test="@kind = 'ca' ">
							<xsl:if test="./ruleml:body and ./ruleml:action">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:body) or not(./ruleml:action)">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
								</xsl:call-template>		
							</xsl:if>
					</xsl:if>
					
					
					<xsl:if test="@kind = 'cap' ">
							<xsl:if test="./ruleml:body and ./ruleml:action and ./ruleml:postcond">
								<xsl:call-template name="handle_eca">

										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:body) or not(./ruleml:action) or not(./ruleml:postcond)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
										<xsl:with-param name="postcond" select="descendant::*[3]"/>
								</xsl:call-template>		
							</xsl:if>
					</xsl:if>
					
					
					<xsl:if test="@kind = 'capa' ">
							<xsl:if test="./ruleml:body and ./ruleml:action and ./ruleml:postcond and ./ruleml:alternative">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:body) or not(./ruleml:action) or not(./ruleml:postcond) or not(./ruleml:alternative)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
										<xsl:with-param name="postcond" select="descendant::*[3]"/>
										<xsl:with-param name="alternative" select="descendant::*[4]"/>
								</xsl:call-template>	
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'caa' ">
							<xsl:if test="./ruleml:body and ./ruleml:action and ./ruleml:alternative">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="./ruleml:body"/>
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:body) or not(./ruleml:action) or not(./ruleml:alternative)">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="body" select="descendant::*[1]"/>
										<xsl:with-param name="action" select="descendant::*[2]"/>
										<xsl:with-param name="alternative" select="descendant::*[3]"/>
								</xsl:call-template>	
							</xsl:if>
					</xsl:if>
					
					
					<xsl:if test="@kind = 'ap' ">
							<xsl:if test="./ruleml:action and ./ruleml:postcond">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
								</xsl:call-template>	
							</xsl:if>
							<xsl:if test="not(./ruleml:action) or not(./ruleml:postcond)">
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="descendant::*[1]"/>
										<xsl:with-param name="postcond" select="descendant::*[2]"/>
								</xsl:call-template>	
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'apa' ">
							<xsl:if test="./ruleml:action and ./ruleml:postcond and ./ruleml:alternative">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="postcond" select="./ruleml:postcond"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="not(./ruleml:action) or not(./ruleml:postcond) or not(./ruleml:alternative)">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="descendant::*[1]"/>
										<xsl:with-param name="postcond" select="descendant::*[2]"/>
										<xsl:with-param name="alternative" select="descendant::*[3]"/>
								</xsl:call-template>
							</xsl:if>
					</xsl:if>
					
					<xsl:if test="@kind = 'aa' ">
							<xsl:if test="./ruleml:action and ./ruleml:alternative">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="./ruleml:action"/>
										<xsl:with-param name="alternative" select="./ruleml:alternative"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="not(./ruleml:action) or not(./ruleml:alternative)">
									<xsl:call-template name="handle_eca">
										<xsl:with-param name="action" select="descendant::*[1]"/>
										<xsl:with-param name="alternative" select="descendant::*[2]"/>
								</xsl:call-template>
							</xsl:if>
					</xsl:if>
					


			</xsl:if>
			<!-- @kind IS NOT defined -->
			<xsl:if test="not(@kind)">			
								<xsl:call-template name="handle_eca">
										<xsl:with-param name="event" select="descendant::*[1]"/>
										<xsl:with-param name="body" select="descendant::*[2]"/>
										<xsl:with-param name="action" select="descendant::*[3]"/>
										<xsl:with-param name="postcond" select="descendant::*[4]"/>
										<xsl:with-param name="alternative" select="descendant::*[5]"/>
								</xsl:call-template>
			</xsl:if>
			
	</xsl:if>
	
</xsl:template>

<!-- REL - optional attribute - uri
		See http://www.ruleml.org/0.91/glossary/#gloss-Rel 					<xsl:value-of select="name(../..)"/>
		content model: (#PCDATA)
-->
<xsl:template match="ruleml:Rel">
	
  	<xsl:text>[</xsl:text>
  	<xsl:variable name="relation" select="."/>
  	<!-- if relation name contains a white space replace with '_' -->
	<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
	<xsl:text>,</xsl:text>
		<xsl:if test=" (../ruleml:Ind[1] | ../ruleml:Var[1] | ../../ruleml:Ind[1] | ../../ruleml:Var[1] | ../ruleml:Expr[1] | ../../ruleml:Expr[1])">
				<xsl:apply-templates select="../ruleml:Ind[1] | ../ruleml:Var[1] | ../../ruleml:Ind[1] | ../../ruleml:Var[1] | ../ruleml:Expr[1] | ../../ruleml:Expr[1]"/>
		</xsl:if>
		<xsl:for-each select="	../ruleml:Ind[position() > 1] | ../../ruleml:Ind[position() > 1] | 
												../ruleml:Var[position() >1] | ../../ruleml:Var[position() >1] | 
												../ruleml:Expr[position() >1] | ../../ruleml:Expr[position() >1]">
				<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
				<xsl:apply-templates select="."/>
		</xsl:for-each>
		
	<xsl:text>]</xsl:text>

</xsl:template>

<!-- BODY 
		content model: (Assert | Retract | Naf | Neg | Atom | And | Or | Equal | HoldsState)
-->
<xsl:template match="ruleml:body">
	<xsl:apply-templates select="./ruleml:Assert | ./ruleml:Retract | ./ruleml:And | ./ruleml:Atom | ./ruleml:Or | ./ruleml:Naf | ./ruleml:Neg | ./ruleml:Equal | ./ruleml:HoldsState | comment()"/>	
</xsl:template>
<!-- ACTION 
		content model: (Assert | Retract | Naf | Neg | Atom | And | Or | Equal | HoldsState )
-->
<xsl:template match="ruleml:action">
	<xsl:apply-templates select="./ruleml:Assert | ./ruleml:Retract | ./ruleml:And | ./ruleml:Atom | ./ruleml:Or | ./ruleml:Naf | ./ruleml:Neg | ./ruleml:Equal | ./ruleml:HoldsState | ./ruleml:Terminates | comment()"/>	
</xsl:template>

<!-- ATOM 
		See http://www.ruleml.org/0.9/glossary/#gloss-Atom
		content model (normally):
			( oid?, (op | Rel), (slot)* , (arg | Ind | Data | Var | Expr | Plex)+, (slot)*)
		
		however, the above content model is non-deterministic, so it is (equivalently) restructured as follows:
		(	(oid)?, (op | Rel), ( (arg | Ind | Data | Var | Expr | Plex)+,)?	)

		(another version of Atom is in the holog module)
		( oid?, (op | Rel), (slot)* , resl?, ( (arg | Ind | Data | Var | Expr | Plex)+, repo?) | (repo) ), (slot)*, resl? )
-->

<xsl:template match="ruleml:Atom">
	
		<!-- /oid -->
 
		<xsl:if test="./ruleml:oid ">
			<xsl:apply-templates select="./ruleml:oid"/>
		</xsl:if>		
		
		<!-- /op/Rel -->		
		<xsl:if test="./ruleml:op/ruleml:Rel">
				<xsl:text>[</xsl:text>
				<xsl:variable name="relation" select="./ruleml:op/ruleml:Rel"/> 
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="translate($relation,' ','_')"/>
				<xsl:text>,</xsl:text>
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun ">								
											<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] | ./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] | ./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)"><xsl:text>,</xsl:text></xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">					
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">								
								<xsl:apply-templates select="."/>											
								<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>

				<!-- end Atom -->
				<xsl:text>]</xsl:text>
		</xsl:if>
		
		<!-- /Rel -->
		<xsl:if test="./ruleml:Rel">
				<xsl:text>[</xsl:text>
				<xsl:variable name="relation" select="./ruleml:Rel"/> 
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>,</xsl:text>		
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">
									<xsl:text>[</xsl:text>								
									<xsl:value-of select="normalize-space(./ruleml:Fun) | normalize-space(./ruleml:op/ruleml:Fun)"/>
											<xsl:text>,</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>]</xsl:text>
					</xsl:if>
					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">											
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">
								<xsl:apply-templates select="."/>											
								<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>
					
				<!-- end Atom -->
				<xsl:text>]</xsl:text>
		</xsl:if>
		
		
		<!-- ./op/Fun | ./Fun -->
		<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
		</xsl:if>
		
		<xsl:if test="./ruleml:op/ruleml:Fun | ./ruleml:Fun">
			<xsl:apply-templates select="./ruleml:op/ruleml:Fun | ./ruleml:Fun"/>
		</xsl:if>
				
</xsl:template>


<!-- PLEX
		See http://www.ruleml.org/0.9/glossary/#gloss-Plex
		content model (within Atom, Plex):
			( (arg | Ind | Var | Expr | Plex)*, (repo)?, (resl)? )

		however, this is non-deterministic, so it is (equivalently) restructured as follows:
			(   oid?,    (  ( (arg|Ind|Data|Var|Expr|Plex)+, (repo)?, (resl)? )?   |  ( (repo), (resl)? )  |  (resl)   )	)

		content model (within repo): ( (arg | Ind | Var | Expr | Plex | repo)* )
		content model (within resl): ( (resl)* )

		Note that the above two do not apply to Datalog sublanguages, since rest variables
		are only introduced in Hornlog and above.
		
		This element's content model is context-sensitive.
-->
<xsl:template match="ruleml:Plex">
	<xsl:text>[</xsl:text>
		<xsl:for-each select="./ruleml:Ind | ./ruleml:Var | ./ruleml:arg | ./ruleml:slot | ./ruleml:Expr | ./ruleml:resl | ./ruleml:repo">
		<xsl:if test="count(preceding-sibling::*) > 0">
			<xsl:if test="(name(.) = 'repo') or (name(.) = 'resl')"><xsl:text>|</xsl:text></xsl:if>
			<xsl:if test="not(name(.) = 'repo') and not(name(.) = 'resl')"><xsl:text>,</xsl:text></xsl:if>
		</xsl:if>
		<xsl:apply-templates select="."/>
		</xsl:for-each>
	<xsl:text>]</xsl:text>
</xsl:template>


<!-- ATOM 
		content model (normally):
			( oid?, (op | Rel), (slot)* , (arg | Ind | Data | Var | Expr | Plex)+, (slot)*)
		
		however, the above content model is non-deterministic, so it is (equivalently) restructured as follows:
		(	(oid)?, (op | Rel), ( (arg | Ind | Data | Var | Expr | Plex)+,)?	)

		(another version of Atom is in the holog module)
		( oid?, (op | Rel), (slot)* , resl?, ( (arg | Ind | Data | Var | Expr | Plex)+, repo?) | (repo) ), (slot)*, resl? )
-->
<xsl:template match="ruleml:Atom">
				
		<!-- /oid Ind-->
		<xsl:if test="./ruleml:oid/ruleml:Ind and name(..) ='body' and not(@uri)">
			<xsl:text>partial(</xsl:text>	
			

		
		<!-- /op/Rel -->
		<xsl:if test="./ruleml:op/ruleml:Rel">
				<xsl:text>[</xsl:text>
				<xsl:variable name="relation" select="./ruleml:op/ruleml:Rel"/>
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>,</xsl:text>
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun ">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] | ./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] | ./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)"><xsl:text>,</xsl:text></xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">					
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">								
								<xsl:apply-templates select="."/>											
								<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>

				<!-- end Atom -->
				<xsl:text>]</xsl:text>
		</xsl:if>
		
		<!-- /Rel -->
		<xsl:if test="./ruleml:Rel">
		
				<xsl:text>"</xsl:text>
				<xsl:variable name="relation" select="./ruleml:Rel"/>
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>(</xsl:text>		
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="normalize-space(./ruleml:Fun) | normalize-space(./ruleml:op/ruleml:Fun)"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>
					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">											
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">
								<xsl:apply-templates select="."/>											
								<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>
					
				<!-- end Atom -->
				<xsl:text>)</xsl:text>
		</xsl:if>
		
		
		<!-- ./op/Fun | ./Fun -->
		<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
		</xsl:if>
		
		<xsl:if test="./ruleml:op/ruleml:Fun | ./ruleml:Fun">
			<xsl:apply-templates select="./ruleml:op/ruleml:Fun | ./ruleml:Fun"/>
		</xsl:if>
		
		
		<xsl:text>,"</xsl:text>
			<xsl:value-of select="./ruleml:oid/ruleml:Ind"/>
			<xsl:text>")</xsl:text>
	</xsl:if>
	
	<!-- /oid/Var -->
	<xsl:if test="./ruleml:oid/ruleml:Var and name(..) ='body'">
			<xsl:text>metadata(</xsl:text>	
			

		
		<!-- /op/Rel -->
		<xsl:if test="./ruleml:op/ruleml:Rel">
		
				<xsl:variable name="relation" select="./ruleml:op/ruleml:Rel"/>
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>(</xsl:text>
				
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun ">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] | ./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] | ./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)"><xsl:text>,</xsl:text></xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">					
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">								
								<xsl:apply-templates select="."/>											
								<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>

				<!-- end Atom -->
				<xsl:text>)</xsl:text>
		</xsl:if>
		
		<!-- /Rel -->
		<xsl:if test="./ruleml:Rel">
		
				<xsl:text>"</xsl:text>
				<xsl:variable name="relation" select="./ruleml:Rel"/>
				
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>(</xsl:text>		
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="normalize-space(./ruleml:Fun) | normalize-space(./ruleml:op/ruleml:Fun)"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>
					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">											
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">
								<xsl:apply-templates select="."/>															
																			
								<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>
					
				<!-- end Atom -->
				<xsl:text>)</xsl:text>
		</xsl:if>
		
		
		<!-- ./op/Fun | ./Fun -->
		<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
		</xsl:if>
		
		<xsl:if test="./ruleml:op/ruleml:Fun | ./ruleml:Fun">
			<xsl:apply-templates select="./ruleml:op/ruleml:Fun | ./ruleml:Fun"/>
		</xsl:if>
		
		
		<xsl:text>,</xsl:text>
			<xsl:value-of select="./ruleml:oid/ruleml:Var"/>
			<xsl:text>,"label")</xsl:text>
	</xsl:if>
	
	
	
			<!-- /not oid -->
		<xsl:if test="not(./ruleml:oid and name(..) ='body')">	
			<xsl:if test="./ruleml:oid and not(./ruleml:oid/ruleml:Ind[@uri])">
				<xsl:text>metadata(label(</xsl:text>
					<xsl:value-of select="./ruleml:oid/ruleml:Ind"/>
				<xsl:text>))::</xsl:text>
			</xsl:if>
			<xsl:if test="./ruleml:oid/ruleml:Ind[@uri]">
					<xsl:apply-templates select="./ruleml:oid/ruleml:Ind"/>
					<xsl:text>.</xsl:text>
			</xsl:if>	
		
		<!-- /op/Rel -->
		<xsl:if test="./ruleml:op/ruleml:Rel">
		
				<xsl:variable name="relation" select="./ruleml:op/ruleml:Rel"/>
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>(</xsl:text>
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun ">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] | ./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] | ./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)"><xsl:text>,</xsl:text></xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">					
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex | ./ruleml:arg">								
								<xsl:apply-templates select="."/>											
								<xsl:if test="(count(following-sibling::*)>0)"><xsl:text>,</xsl:text></xsl:if>								
							</xsl:for-each>	
																
					</xsl:if>
				<!-- end Atom -->
				<xsl:text>)</xsl:text>
		</xsl:if>
		
		<!-- /Rel -->
		
				
		<xsl:if test="./ruleml:Rel">
				<xsl:if test="name(..) ='Assert'">
					<!--<xsl:text>"</xsl:text>-->
				</xsl:if>
				<xsl:text>[</xsl:text>
				<xsl:variable name="relation" select="./ruleml:Rel"/>
				<!-- if relation name contains a white space replace with '_' -->
				<xsl:value-of select="normalize-space(translate($relation,' ','_'))"/>
				<xsl:text>,</xsl:text>		
					<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="normalize-space(./ruleml:Fun) | normalize-space(./ruleml:op/ruleml:Fun)"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
					</xsl:if>
					
					<xsl:if test="not(./ruleml:Fun) and not(./ruleml:op/ruleml:Fun)">											
							<xsl:for-each select="./ruleml:slot | ./ruleml:Var| ./ruleml:Ind | ./ruleml:Expr | ./ruleml:Data | ./ruleml:Reify | ./ruleml:Plex">
								<xsl:apply-templates select="."/>	
								<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
							</xsl:for-each>										
					</xsl:if>
					
				<!-- end Atom -->
				<xsl:if test="name(..) ='Assert'">
					<!--<xsl:text>)."</xsl:text>-->
				</xsl:if>
				<xsl:if test="name(..) !='Assert'">
					<xsl:text>]</xsl:text>
				</xsl:if>
		</xsl:if>
		
		
		<!-- ./op/Fun | ./Fun -->
		<xsl:if test="./ruleml:Fun | ./ruleml:op/ruleml:Fun">								
									<xsl:value-of select="./ruleml:Fun | ./ruleml:op/ruleml:Fun"/>
											<xsl:text>(</xsl:text>
													<xsl:if test="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																			./ruleml:Var[1] | ./../ruleml:Var[1] |
																			./ruleml:Expr[1] |./../ruleml:Expr[1]">
															<xsl:apply-templates select="	./ruleml:Ind[1] | ./../ruleml:Ind[1] |
																													./ruleml:Var[1] | ./../ruleml:Var[1] |
																													./ruleml:Expr[1] |./../ruleml:Expr[1]"/>
													</xsl:if>
													<xsl:for-each select="	./ruleml:Ind[position() > 1] | ./../ruleml:Ind[position() > 1] |
																							./ruleml:Var[position() > 1] | ./../ruleml:Var[position() > 1] |
																							./ruleml:Expr[position() > 1] | ./../ruleml:Expr[position() > 1]">
															<xsl:if test="(preceding::node()[position() > 1]) or (count(preceding::*)>0)">,</xsl:if>
																	<xsl:apply-templates select="."/>					
													</xsl:for-each>
											<xsl:text>)</xsl:text>
		</xsl:if>
		
		<xsl:if test="./ruleml:op/ruleml:Fun | ./ruleml:Fun">
			<xsl:apply-templates select="./ruleml:op/ruleml:Fun | ./ruleml:Fun"/>
		</xsl:if>
	</xsl:if>
				
</xsl:template>

<!-- NAF
		content model: ( oid?, (weak | Atom) )
-->
<xsl:template match="ruleml:Naf">
	<xsl:text>not(</xsl:text>
		<xsl:apply-templates select="./ruleml:weak | ./ruleml:Atom | ./ruleml:And"/>
	<xsl:text>)</xsl:text>
</xsl:template>

<!-- NEG
		content model: ( oid?, (strong | Atom) )
-->
<xsl:template match="ruleml:Neg">
	<xsl:text>neg(</xsl:text>
		<xsl:apply-templates select="./ruleml:strong | ./ruleml:Atom | ./ruleml:HoldsState"/>
	<xsl:text>)</xsl:text>
</xsl:template>

<!-- WEAK
		content model: (Atom)
-->
<xsl:template match="ruleml:weak">
	<xsl:apply-templates select="./ruleml:Atom"/>
</xsl:template>

<!-- STRONG
		content model: (Atom)
-->
<xsl:template match="ruleml:strong">
	<xsl:apply-templates select="./ruleml:Atom"/>
</xsl:template>

<!-- IMPLIES 
		content model:	
		( 
			oid?, 
			(head, body)   |	 (body, head)   |   (  (Atom | Naf | Neg | Reaction | And | Or | Equal | Assert | Retract)* , Atom  ) 
		)	
-->
<xsl:template match="ruleml:Implies">
	<!--<xsl:value-of select="name(.)"/>
		
	<xsl:if test="@variety='defeasible'">
		<xsl:text>defeasible(</xsl:text>	
		<xsl:text>),not(defeated("</xsl:text>						
		<xsl:value-of select="./ruleml:oid/ruleml:Ind"/>						
		<xsl:text>",</xsl:text>											
		<xsl:apply-templates select="./ruleml:head"/>
		<xsl:text>))</xsl:text>
		<xsl:text>.&#10;neg(blocked(defeasible(</xsl:text>
		<xsl:apply-templates select="./ruleml:head"/>												
		<xsl:text>)))) :- </xsl:text>			
		<xsl:text>testIntegrity(</xsl:text>			
		<xsl:apply-templates select="./ruleml:head"/>				
		<xsl:text>),defeasible(</xsl:text>	
		<xsl:apply-templates select="./ruleml:body"/>			
		<xsl:text>.&#10;body(defeasible(</xsl:text>			
		<xsl:apply-templates select="./ruleml:head"/>				
		<xsl:text>)) :- defeasible(</xsl:text>	
		<xsl:apply-templates select="./ruleml:body"/>		
		<xsl:text>)</xsl:text>	
	</xsl:if>		-->	


	
	
		<xsl:if test="./ruleml:oid/ruleml:Ind">
		<xsl:text>metadata(label(</xsl:text>
				<xsl:value-of select="./ruleml:oid/ruleml:Ind"/>
		<xsl:text>))::</xsl:text>							
		</xsl:if>
		
		<xsl:if test="@variety='defeasible'">
			<xsl:text>defeasible(</xsl:text><xsl:apply-templates select="./ruleml:head"/>
			<xsl:text>)) :- testIntegrity(</xsl:text>
			<xsl:apply-templates select="./ruleml:head"/>	
			<xsl:text>),defeasible(</xsl:text>
			<xsl:apply-templates select="./ruleml:body"/>
			<xsl:text>),not(defeated("</xsl:text>						
			<xsl:value-of select="./ruleml:oid/ruleml:Ind"/>						
			<xsl:text>",</xsl:text>											
			<xsl:apply-templates select="./ruleml:head"/>
			<xsl:text>))</xsl:text>
			<xsl:text>.&#10;neg(blocked(defeasible(</xsl:text>
			<xsl:apply-templates select="./ruleml:head"/>												
			<xsl:text>))) :- </xsl:text>			
			<xsl:text>testIntegrity(</xsl:text>			
			<xsl:apply-templates select="./ruleml:head"/>				
			<xsl:text>),defeasible(</xsl:text>	
			<xsl:apply-templates select="./ruleml:body"/>			
			<xsl:text>.&#10;body(defeasible(</xsl:text>			
			<xsl:apply-templates select="./ruleml:head"/>				
			<xsl:text>)) :- defeasible(</xsl:text>	
			<xsl:apply-templates select="./ruleml:body"/>		
			<xsl:text>)</xsl:text>	
		</xsl:if>
			
	<xsl:if test="not(@variety)">	
		<xsl:if test="not(./ruleml:And) and not(./ruleml:Or) and ./ruleml:Atom[1]">
						<xsl:call-template name="handle_head_body">
							<xsl:with-param name="headpart" select="./ruleml:Atom[2]"/>
							<xsl:with-param name="bodypart" select="./ruleml:Atom[1]"/>
							<xsl:with-param name="custom" select="'1'"/>
						</xsl:call-template>
					</xsl:if>
		<xsl:if test="not(./ruleml:And) and not(./ruleml:Or) and ./ruleml:HoldsState[1]">
						<xsl:call-template name="handle_head_body">
							<xsl:with-param name="headpart" select="./ruleml:Atom[2]"/>
							<xsl:with-param name="bodypart" select="./ruleml:HoldsState"/>
							<xsl:with-param name="custom" select="'1'"/>
						</xsl:call-template>
					</xsl:if>

					<xsl:if test="./ruleml:And">
						<xsl:call-template name="handle_head_body">
							<xsl:with-param name="headpart" select="./ruleml:Atom"/>
							<xsl:with-param name="bodypart" select="./ruleml:And"/>
							<xsl:with-param name="custom" select="'1'"/>
						</xsl:call-template>
					</xsl:if>

					<xsl:if test="./ruleml:Or">			
						<xsl:variable name="headtoken" select="./ruleml:Atom"/>
						<xsl:for-each select="./ruleml:Or/ruleml:Atom | ./ruleml:Or/ruleml:And |
								      ./ruleml:Or/ruleml:Or | ./ruleml:Or/ruleml:formula">
							<xsl:variable name="bodytoken" select="."/>
							<xsl:call-template name="handle_head_body">						
								<xsl:with-param name="headpart" select="$headtoken"/>
								<xsl:with-param name="bodypart" select="$bodytoken"/>	
								<xsl:with-param name="nocommas" select="'1'"/>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:if>	
					<xsl:if test="./ruleml:head">						 																					
						<xsl:if test="./ruleml:body/ruleml:Or">
							<xsl:variable name="headtoken" select="./ruleml:head"/>
							<xsl:for-each select="./ruleml:body/ruleml:Or/ruleml:Atom | ./ruleml:body/ruleml:Or/ruleml:And |
									      ./ruleml:body/ruleml:Or/ruleml:Or | ./ruleml:body/ruleml:Or/ruleml:formula">
								<xsl:variable name="bodytoken" select="."/>
								<xsl:call-template name="handle_head_body">						
									<xsl:with-param name="headpart" select="$headtoken"/>
									<xsl:with-param name="bodypart" select="$bodytoken"/>	
									<xsl:with-param name="nocommas" select="'1'"/>
								</xsl:call-template>
							</xsl:for-each>							
						</xsl:if>
						
						<xsl:if test="not(./ruleml:body/ruleml:Or)">
							<xsl:call-template name="handle_head_body">
								<xsl:with-param name="headpart" select="./ruleml:head"/>
								<xsl:with-param name="bodypart" select=" ./ruleml:body"/>
							</xsl:call-template>
						</xsl:if>		
					</xsl:if>
	</xsl:if>
</xsl:template>


<!-- TIME 
content model: 
		(
			Ind | Var | Expr
		)
-->
<xsl:template match="ruleml:time">
	<xsl:apply-templates select="./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | ./ruleml:Atom"/>
</xsl:template>

<!-- EVENT
content model: 
		(
			 Naf | Neg | Atom | Reaction | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic | Ind | Var | Expr
		)
-->
<xsl:template match="ruleml:event">
	<xsl:apply-templates select="	./ruleml:Naf | ./ruleml:Neg | ./ruleml:Atom | ./ruleml:Reaction | ./ruleml:Message |
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | comment()"/>
</xsl:template>

<!-- ACTION
content model: 
		(
			  Atom | Assert | Retract | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic | Ind | Var | Expr
		)
-->
<xsl:template match="ruleml:action">
	<xsl:apply-templates select="  ./ruleml:Atom | ./ruleml:Assert | ./ruleml:Retract | ./ruleml:Message | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | ./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | ./ruleml:Initiates  | ./ruleml:Terminates | comment()"/>
<!-- <xsl:apply-templates select="  ./ruleml:Naf | ./ruleml:Neg | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or"/>	-->
</xsl:template>


<!-- POSTCOND
content model: 
		(
			  Naf | Neg | Atom | And | Or
		)
-->
<xsl:template match="ruleml:postcond">
	<xsl:apply-templates select="  ./ruleml:Naf | ./ruleml:Neg | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or | comment()"/>
</xsl:template>

<!-- ALTERNATIVE
content model: 
		( 
				Atom | Assert | Retract | Message | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
		)
-->
<xsl:template match="ruleml:alternative">
	<xsl:apply-templates select="  ./ruleml:Atom | ./ruleml:Assert | ./ruleml:Retract | ./ruleml:Message | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic | comment()"/>
</xsl:template>

<!-- MESSAGE
		A Message element that provides the syntax for inbound and outbound messages / notifications 

      content model: 
       ( 
			(oid | Ind | Var | Expr), 
			(protocol | Ind | Var | Expr), 
			(sender | Ind | Var | Expr), 
			(content | Atom | Naf | Neg | Rulebase | And | Or | Entails | Exists | Equal | Ind | Var) 
		)
      
      Message has the following attributes:
      @mode, @directive
-->
<xsl:template match="ruleml:Message">
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:text>[[id,</xsl:text>
			<xsl:apply-templates select="./ruleml:oid | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr"/>
			<xsl:text>],</xsl:text>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="./ruleml:protocol | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr"/>
			<xsl:text>,</xsl:text>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">			
			<xsl:apply-templates select="./ruleml:sender | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr"/>
			<xsl:text>,</xsl:text>
			<xsl:value-of select="@directive"/>
			<xsl:text>,</xsl:text>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="./ruleml:content | ./ruleml:Atom | ./ruleml:Naf | ./ruleml:Neg | ./ruleml:Rulebase | ./ruleml:And | ./ruleml:Or | ./ruleml:Entails | ./ruleml:Exists | ./ruleml:Equal  | ./ruleml:Ind | ./ruleml:Var"/>
			<xsl:text>]</xsl:text>
	</xsl:if>
</xsl:template>

<!-- PROTOCOL
	 content model: 
		(
			Ind | Var | Expr 
		)
-->
<xsl:template match="ruleml:protocol">
	<xsl:apply-templates select=" ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr"/>
</xsl:template>

<!-- SENDER
	 content model: 
		(
			Ind | Var | Expr 
		)
-->
<xsl:template match="ruleml:sender">
	<xsl:apply-templates select=" ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr"/>
</xsl:template>

<!-- CONTENT
	 content model: 
		(
			Atom | Naf | Neg | Rulebase | And | Or | Entails | Exists | Equal | Ind | Var
		)
-->
<xsl:template match="ruleml:content">
	<xsl:apply-templates select=" ./ruleml:Atom | ./ruleml:Naf | ./ruleml:Neg | ./ruleml:Rulebase | ./ruleml:And | ./ruleml:Or | ./ruleml:Entails | ./ruleml:Exists | ./ruleml:Equal | ./ruleml:Ind | ./ruleml:Var"/>
</xsl:template>

<!-- @EXEC
		restriction: active | passive | reasoning
		default value: passive
		required attribute
-->
<xsl:template match="@exec">
</xsl:template>

<!-- @MODE
		restriction: inbound or outbound ; 
		default = inbound
		optional attribute
		A attribute for optionally specifing the intended input-output constellations of the predicate terms with the
           following semantics:
        
           "+" The term is intended to be input
           "-" The term is intended to be output
           "?" The term is undefined (input or output)
		   
		   default="?"
		   optional attribute
-->
<xsl:template match="@mode">
</xsl:template>

<!-- @SAFETY
		restriction: normal or transaction
		 default: normal
		 optional attribute
-->
<xsl:template match="@safety">
</xsl:template>


<!-- @ARG
		restriction: arg values
	 optional attribute
-->
<xsl:template match="@arg">
</xsl:template>

<!-- @DIRECTIVE
		restriction: different performatives / directives, e.g. ACL:inform 
	 required attribute
-->
<xsl:template match="@directive">
</xsl:template>

<!-- @EVAL
		 restriction: strong or weak ; 
		 default: strong
		 optional attribute
-->
<xsl:template match="@eval">
</xsl:template>

<!-- @KIND
		 restriction: different patterns
		 default: eca (ECA rule pattern)
		 required attribute
-->
<xsl:template match="@kind">
</xsl:template>


<!-- SEQUENCE 
content model: 
		(
			oid?, 
			event | action | Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
		)
-->
<xsl:template match="ruleml:Sequence">
	<xsl:if test="not(name(./child::*[1])='Ind')">
		<xsl:value-of select="name(./child::*[1])"/>
		<xsl:text>(sequence(</xsl:text>
	</xsl:if>
	<xsl:if test="name(./child::*[1])='Ind'">
		<xsl:text>(</xsl:text>
	</xsl:if>
		
	
	<xsl:if test="	 ./ruleml:action | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic">
															
		<xsl:apply-templates select="	 ./ruleml:action | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
		<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
	</xsl:if>
			
		<xsl:for-each select="./ruleml:Ind">
			<xsl:apply-templates select="."/>
			<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
		</xsl:for-each>
			
		<xsl:for-each select="./ruleml:event">
			<xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
			<xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
			<xsl:value-of select="translate(name(./child::*[1]),$ucletters,$lcletters)"/>
			<xsl:apply-templates select="./child::*[1]"/>
			<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
		</xsl:for-each>
	
	<xsl:text>)</xsl:text>
														
</xsl:template>

<!-- DISJUNCTION 
content model: 
		(
			oid?, 
			event | action |  Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
		)
-->
<xsl:template match="ruleml:Disjunction">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
</xsl:template>

<!-- CONJUNCTION 
content model: 
		(
			oid?, 
			event | action |  Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
		)
-->
<xsl:template match="ruleml:Conjunction">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
</xsl:template>



<!-- XOR
content model: 
		(
			oid?, 
			event | action |  Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
		)
-->
<xsl:template match="ruleml:Xor">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
</xsl:template>

<!-- CONCURRENT
content model: 
		(	
			oid?, 
			event | action |  Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
		)
-->
<xsl:template match="ruleml:Concurrent">
	<!--<xsl:text>concurrent(</xsl:text>-->
	<xsl:text>(</xsl:text>
	<xsl:for-each select="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
		<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
	</xsl:for-each>
	<xsl:for-each select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic">

		<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
															./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
															./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
	<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
	</xsl:for-each>
	<xsl:text>)</xsl:text>
</xsl:template>

<!-- NOT
content model: 
		(	
			oid?, 
			event | action |  Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic, 
			interval | Interval | Plex | Var
		)
-->
<xsl:template match="ruleml:Not">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 1">
			<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
																	./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
																	./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="	./ruleml:interval | ./ruleml:Interval |./ruleml:Plex | ./ruleml:Var"/>
	</xsl:if>
</xsl:template>

<!-- ANY
content model: 
		(	
			oid?, 
			Ind | Data | Var, 
			event | action |  Ind | Var | Expr |Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic, 
			interval | Interval | Plex | Var
		)
-->
<xsl:template match="ruleml:Any">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 2">
			<xsl:apply-templates select="./ruleml:Ind | ./ruleml:Data | ./ruleml:Var"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 1">
			<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
																	./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
																	./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="	./ruleml:interval | ./ruleml:Interval |./ruleml:Plex | ./ruleml:Var"/>
	</xsl:if>
</xsl:template>

<!-- APERIODIC
content model: 
		(	
			oid?, 
			event | action |  Ind | Var | Expr |Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic, 
			interval | Interval | Plex | Var
		)
-->
<xsl:template match="ruleml:Aperiodic">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 1">
			<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
																	./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
																	./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="	./ruleml:interval | ./ruleml:Interval |./ruleml:Plex | ./ruleml:Var"/>
	</xsl:if>
</xsl:template>

<!-- PERIODIC
content model: 
		(
			oid?, 
			time | Ind | Var | Expr , 
			interval | Interval | Plex | Var
		)
-->
<xsl:template match="ruleml:Periodic">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 1">
			<xsl:apply-templates select="	./ruleml:time | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="	./ruleml:interval | ./ruleml:Interval |./ruleml:Plex | ./ruleml:Var"/>
	</xsl:if>
</xsl:template>

<!-- INTERVAL *** interval ***
	  content model: 
		( 
			Interval | Plex | Var 
		)
-->
<xsl:template match="ruleml:interval">
	<xsl:apply-templates select="./ruleml:Interval | ./ruleml:Plex | ./ruleml:Var"/>
</xsl:template>

<!-- INTERVAL *** Interval ***
	  content model: 
			( 
				oid?, 
				event | action | time | Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic , 
				event | action | time | Ind | Var | Expr | Sequence | Disjunction | Xor | Conjunction | Concurrent | Not | Any | Aperiodic | Periodic
			)
-->
<xsl:template match="ruleml:Interval">
	<xsl:if test="./ruleml:oid">
		<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 1">
			<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:time | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
																	./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
																	./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
	</xsl:if>
	<xsl:if test="count(following-sibling::*) = 0">
			<xsl:apply-templates select="	./ruleml:event | ./ruleml:action | ./ruleml:time | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr | 
																	./ruleml:Sequence | ./ruleml:Disjunction | ./ruleml:Xor | ./ruleml:Conjunction | 
																	./ruleml:Concurrent | ./ruleml:Not | ./ruleml:Any | ./ruleml:Aperiodic | ./ruleml:Periodic"/>
	</xsl:if>
</xsl:template>

<!-- ARG -->
<xsl:template match="ruleml:arg">
	<xsl:if test="./ruleml:Var">
		<xsl:apply-templates select="./ruleml:Var"/>
	</xsl:if>
</xsl:template>

<!-- HOLDSSTATE -->
<xsl:template match="ruleml:HoldsState">

	<xsl:text>holdsAt(</xsl:text>
	<!--<xsl:if test="./ruleml:Expr">
		<xsl:apply-templates select="./ruleml:Expr"/>			
	</xsl:if>
	
	<xsl:if test="./ruleml:state">
		<xsl:if test="./ruleml:state/ruleml:Expr">
			<xsl:apply-templates select="./ruleml:state/ruleml:Expr"/>
		</xsl:if>
	</xsl:if>
	
	<xsl:if test="./ruleml:time">
	<xsl:apply-templates select="./ruleml:time/."/>
		<xsl:if test="./ruleml:time/ruleml:Var">	
			<xsl:apply-templates select="./ruleml:time/ruleml:Var"/>
		</xsl:if>
		<xsl:if test="./ruleml:time/ruleml:Expr">
			<xsl:apply-templates select="./ruleml:time/ruleml:Expr"/>			
		</xsl:if>
	</xsl:if>-->
	<xsl:for-each select="./ruleml:Expr | ./ruleml:state | ./ruleml:time | ./ruleml:Var">
		<xsl:apply-templates select="."/> 
		<xsl:if test="count(following-sibling::*) > 0">,</xsl:if>	
	</xsl:for-each>
	
	<xsl:text>)</xsl:text>
</xsl:template>

<!-- FUN - optional attribute - uri
		See http://www.ruleml.org/0.9/glossary/#gloss-Fun
		content model: (#PCDATA)
-->
<xsl:template match="ruleml:Fun">
	<xsl:value-of select="translate(., ' ', '_')"/>		
</xsl:template>

<!-- IND -->
<xsl:template match="ruleml:Ind">

	<!-- <xsl:choose> -->
    	<xsl:variable name="empty_string"/>
    	
		<xsl:if test="not(current()/@uri)">
    	
				<xsl:if test="current()/@type">
					<xsl:value-of select="translate(current()/@type,':','_')"/>
					<xsl:text>:</xsl:text>
					<xsl:if test="contains(current()/@type,'.')">
						<xsl:value-of select="normalize-space(.)"/>
					</xsl:if>
					<xsl:if test="not(contains(current()/@type,'.'))">
						<xsl:if test="current()[@type='int']"><xsl:value-of select="normalize-space(.)"/></xsl:if>
						<xsl:if test="current()[@type!='int']"><xsl:value-of select="normalize-space(.)"/></xsl:if>
					</xsl:if>
				</xsl:if>
								
				<xsl:if test="name(..)= 'Expr' and not(current()/@type) and string(.+1)!='NaN' and not(contains(current()/@type,'.'))">					
						<xsl:value-of select="normalize-space(string(.))"/>										
				</xsl:if>
				<xsl:if test="name(..)= 'Expr' and not(name(../..)= 'Atom') and not(current()/@type) and string(.+1)='NaN' and not(contains(current()/@type,'.'))">				
						<xsl:text>"</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>"</xsl:text>								
				</xsl:if>	
				<xsl:if test="name(..)= 'Expr' and name(../..)= 'Atom' and not(current()/@type) and string(.+1)='NaN' and not(contains(current()/@type,'.'))">				
						<xsl:text>"</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>"</xsl:text>								
				</xsl:if>
				<xsl:if test="name(..)= 'Expr'  and not(current()/@type) and not(contains(substring(.,1,1),'0123456789' )) and contains(current()/@type,'.')">					
						<xsl:value-of select="normalize-space(string(.))"/>										
				</xsl:if>
											
				<xsl:if test="name(..)= 'Atom' and name(../..)= 'Retract' and not(current()/@type) and not(contains(substring(.,1,1),'0123456789' )) and not(contains(current()/@type,'.'))">					
						<xsl:text>"</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>"</xsl:text>										
				</xsl:if>
				<xsl:if test="name(..)= 'Atom' and not(name(../..)= 'Retract') and not(current()/@type) and string(.+1)!='NaN' and not(contains(current()/@type,'.'))">				
						<xsl:value-of select="normalize-space(string(.))"/>								
				</xsl:if>													
				<xsl:if test="name(..)= 'Atom' and not(name(../..)= 'Retract') and not(current()/@type) and string(.+1)='NaN' and not(contains(current()/@type,'.'))">				
						<xsl:text>"</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>"</xsl:text>								
				</xsl:if>	
				<xsl:if test="name(..)= 'oid'  and not(current()/@type) and string(.+1)!='NaN' and not(contains(current()/@type,'.'))">
					<xsl:value-of select="normalize-space(string(.))"/>
				</xsl:if>
				<xsl:if test="name(..)= 'oid'  and not(current()/@type) and string(.+1)='NaN' and not(contains(current()/@type,'.'))">
					<xsl:text>"</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>"</xsl:text>
				</xsl:if>
				<xsl:if test="name(..)= 'event'  and not(current()/@type) and not(contains(substring(.,1,1),'0123456789')) and not(contains(current()/@type,'.'))">
					<xsl:value-of select="normalize-space(string(.))"/>
				</xsl:if>
				<xsl:if test="name(..)= 'Sequence'  and not(current()/@type) and not(contains(substring(.,1,1),'0123456789')) and not(contains(current()/@type,'.'))">
					<xsl:value-of select="normalize-space(string(.))"/>
				</xsl:if>
				
				
				
				
				
				<xsl:if test="not(name(..)= 'Expr') and not(name(..)= 'Atom') and not(name(..)= 'oid') and not(name(..)= 'event') and not(name(..)= 'Sequence') and not(current()/@type) and not(contains(substring(.,1,1),'0123456789')) and not(contains(current()/@type,'.'))">
					<xsl:text>"</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>"</xsl:text>					
				</xsl:if>
				<xsl:if test="not(name(..)= 'Expr') and not(current()/@type) and contains(substring(.,1,1),'0123456789') and not(contains(current()/@type,'.'))">
					<xsl:text>(</xsl:text><xsl:value-of select="normalize-space(string(.))"/><xsl:text>)</xsl:text>
				</xsl:if>
				
				<xsl:if test="not(current()/@type) and contains(substring(.,1,1),'0123456789') and not(contains(current()/@type,'.'))">
						<xsl:if test="not(contains(.,'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'))"><xsl:value-of select="normalize-space(.)"/></xsl:if>
						<xsl:if test="contains(.,'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')">
							<xsl:value-of select="normalize-space(string(.))"/></xsl:if>
							<xsl:text>"</xsl:text>
						</xsl:if>
				
				<xsl:if test="current()/@mode">
							<xsl:if test="current()[@mode='+']">
								<xsl:value-of select="normalize-space(.)"/>
							</xsl:if>
				</xsl:if>
				
		</xsl:if>

		<xsl:if test="not(current()/@mode) and not(current()/@type) and current()/@uri">
				<xsl:if test="normalize-space(.) = $empty_string">
					<xsl:if test="name(../..)= 'Assert'">
						<xsl:text>"</xsl:text><xsl:value-of select="@uri"/><xsl:text>"</xsl:text>
					</xsl:if>
					<xsl:if test="not(name(../..)= 'Assert')">
						<xsl:value-of select="@uri"/>
					</xsl:if>
				</xsl:if>
				<xsl:if test="normalize-space(.) != $empty_string">
						<xsl:if test="string-length(.) > 0">
								<xsl:value-of select="."/>
						</xsl:if>
						<xsl:if test="string-length() = 0">
								<xsl:value-of select="@uri"/>
						</xsl:if>
				</xsl:if>
		</xsl:if>		

</xsl:template>


<!-- EXPR - optional attribute - type
		See http://www.ruleml.org/0.91/glossary/#gloss-Expr

		content model:
			( (op | Fun), (slot)*,(resl)?, (arg|Ind|Data|Var|Expr|Plex)*, (repo)?,(slot)* ,(resl)? )

		however, this is non-deterministic, so it is (equivalently) restructured as follows:
			( oid?, (op | Fun), (slot)*, (resl)?,	( 	( 	((arg|Ind|Data|Var|Expr|Plex)+, (repo)?)	|	(repo)	),	(slot)* ,(resl)?	)? )
		
		(Note the 'positionalized' normal form where the op role is at the beginning of the Expr.)
-->
<xsl:template match="ruleml:Expr">

				<xsl:if test="./ruleml:oid/ruleml:Ind">
						<xsl:apply-templates select="./ruleml:oid/ruleml:Ind"/>
						<xsl:text>.</xsl:text>
				</xsl:if>
				<xsl:if test="./ruleml:oid/ruleml:Var">
						<xsl:apply-templates select="./ruleml:oid/ruleml:Var"/>
						<xsl:text>.</xsl:text>
				</xsl:if>				
<xsl:choose>

				
				<xsl:when test="./ruleml:Fun">
					<xsl:text>[</xsl:text>
					<xsl:value-of select="./ruleml:Fun"/>
					<xsl:text>,</xsl:text>
				<xsl:if test="./ruleml:arg">
						<xsl:apply-templates select="./ruleml:arg"/>
				</xsl:if>
				
				<xsl:if test="./ruleml:Ind	and not(./../ruleml:Ind)">
					<xsl:for-each select="./ruleml:Ind	|  ./../ruleml:Ind | ./ruleml:Expr | ./ruleml:Var | ./../../ruleml:Var">
					<xsl:apply-templates select="."/>
					<xsl:if test="count(following-sibling::*) > 0">
						<xsl:text>,</xsl:text>
					</xsl:if>
					</xsl:for-each>		
				</xsl:if>
				
				<xsl:if test="not(./ruleml:Ind)	">
					<xsl:for-each select="./ruleml:Expr | ./ruleml:Var ">
						<xsl:apply-templates select="."/>
						<xsl:if test="count(following-sibling::*) > 0">
							<xsl:text>,</xsl:text>
						</xsl:if>
					</xsl:for-each>		
				</xsl:if>
				
				<xsl:if test="./ruleml:Ind	and ./../ruleml:Ind">
					<xsl:for-each select="./ruleml:Ind | ./ruleml:Expr | ./ruleml:Var | ./../../ruleml:Var">
						<xsl:apply-templates select="."/>
						<xsl:if test="count(following-sibling::*) > 0">
							<xsl:text>,</xsl:text>
						</xsl:if>
					</xsl:for-each>		
				</xsl:if>
							
					<xsl:text>]</xsl:text>				
				</xsl:when>
				
				<xsl:when test="./ruleml:op/ruleml:Fun">
					<xsl:text>[</xsl:text>
					<xsl:value-of select="./ruleml:op/ruleml:Fun"/>
					<xsl:text>,</xsl:text>
							<xsl:apply-templates select="./ruleml:Ind[1] | ./../ruleml:Ind[1] | ./../../ruleml:Ind[1] | ./ruleml:Expr[1] | ./ruleml:Var[1] | ./../ruleml:Var[1] | ./../../ruleml:Var[1]"/>	
							<xsl:for-each select="./ruleml:Ind[position() >1] | ./../ruleml:Ind[position() > 1] | ./../../ruleml:Ind[position() > 1] | ./ruleml:Expr[position() > 1] |./ruleml:Var[position() >1] | ./../ruleml:Var[position() >1] | ./../../ruleml:Var[position() >1]">,<xsl:apply-templates select="."/></xsl:for-each>
					
				</xsl:when>
				
				<xsl:when test="(./ruleml:Var | ./ruleml:Ind) and not(./ruleml:Fun) and not(./ruleml:Attachment)">
						<xsl:apply-templates select="./ruleml:Ind[1] | ./ruleml:Var[1]"/>	
						<xsl:for-each select="./ruleml:Ind[position() > 1] | ./ruleml:Var[position() > 1]">,<xsl:apply-templates select="."/></xsl:for-each>
				<xsl:text>]</xsl:text>
				</xsl:when>
				
				<!-- chech attachment -->
				<xsl:when test="./ruleml:Attachment">
					<xsl:for-each select="./ruleml:Attachment/ruleml:Ind">
							<xsl:if test="count(following-sibling::*) > 0"><xsl:value-of select="."/><xsl:text>.</xsl:text></xsl:if>
							<xsl:if test="count(following-sibling::*) = 0"><xsl:value-of select="."/></xsl:if>
					</xsl:for-each>
						<xsl:text>(</xsl:text>
						<xsl:if test="./ruleml:Ind">
								<xsl:apply-templates select="./ruleml:Ind"/>
						</xsl:if>
						<xsl:text>)</xsl:text>
				</xsl:when>					
</xsl:choose>			

</xsl:template>


<!-- VAR -->
<xsl:template match="ruleml:Var">
    	<xsl:variable name="empty_string"/>
	<xsl:if test="current()[@mode='-']">	
				<xsl:value-of select="."/>				
	</xsl:if>
	
	<xsl:if test="normalize-space(.) = $empty_string">
		<xsl:text>_</xsl:text>
	</xsl:if>
			
	<xsl:if test="not(contains(@type,':'))">
		<xsl:if test="current()[@type] and not(name(..)='arg')">		
			<xsl:value-of select="@type"/>			
			<xsl:text>:</xsl:text>
		</xsl:if>
		<xsl:if test="not(current()[@mode='-'])">		
			<xsl:value-of select="translate(substring(.,1,1), 'abcdefghijklmnopqrstuvwxyz0123456789' , 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789' )"/> 
			<xsl:value-of select="substring(translate(.,' ','_'),2)"/>
		</xsl:if>
	</xsl:if>
	
	<xsl:if test="contains(@type,':')">
		<xsl:if test="not(current()[@mode='-'])">
			<xsl:value-of select="translate(substring(.,1,1), 'abcdefghijklmnopqrstuvwxyz0123456789' , 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789' )"/> 
			<xsl:value-of select="substring(translate(.,' ','_'),2)"/>
		</xsl:if>
		<xsl:if test="current()[@type] and not(name(..)='arg')">
			<xsl:text>:</xsl:text>
			<xsl:value-of select="translate(@type,':','_')"/>
		</xsl:if>
	</xsl:if>
</xsl:template>

<!-- match XML comment and preserve them in Prova-Syntax -->
<!--
<xsl:template match="comment()">
	<xsl:text>&#10;</xsl:text>
	<xsl:call-template name="comments"/>
</xsl:template>
-->
<!-- Named templates -->
<!-- handle XML-comments -->
<xsl:template match="comment()" name="comments" mode="tokenize">
	<xsl:param name="string" select="string()" />
	<xsl:param name="break" select=" '&#xA;' " />
	<xsl:param name="prefix" select=" '%' " />
	<xsl:variable name="prefixString" select="$prefix" />
	<xsl:variable name="multitoken" select="contains($string, $break)" />
	<xsl:variable name="token">
		<xsl:choose>
			<xsl:when test="$multitoken">
				<xsl:value-of select="normalize-space(substring-before($string, $break))" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="normalize-space($string)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<!-- creates a single line of comment -->
	<xsl:if test="string($token)">
		<!-- start with comment-char '%', add new line at the end -->
		<xsl:value-of select="$prefixString"/><xsl:text> </xsl:text><token><xsl:value-of select="$token"/></token><xsl:text>&#10;</xsl:text>
	</xsl:if>
	<!-- rest of the text -->
	<xsl:if test="$multitoken">
		<xsl:call-template name="comments">
			<xsl:with-param name="string" select="substring-after($string, $break)"/>
			<xsl:with-param name="break" select="$break"/>
			<xsl:with-param name="prefix" select="$prefix"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>

<!-- AND 
		content model: ( oid?, (formula | Atom | And | Or)* )
-->
<xsl:template match="ruleml:And">
	
		<xsl:if test="./ruleml:oid">
			<xsl:apply-templates select="./ruleml:oid"/>
		</xsl:if>
		
		<xsl:if test="./ruleml:formula | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or  | ./ruleml:Reaction |./ruleml:Equal ">
			<xsl:for-each select="./ruleml:formula | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or | ./ruleml:Assert | ./ruleml:Reaction |./ruleml:Equal ">
			
				<xsl:for-each select="(descendant::*[name() = 'Var' and @mode])">
					<xsl:if test="@mode = '-' "><xsl:text>free(</xsl:text>
						<xsl:value-of select="."/><xsl:text>),&#10;&#x9;</xsl:text>
					</xsl:if>
					<xsl:if test="@mode = '+' ">
						<xsl:text>bound(</xsl:text>
						<xsl:value-of select="."/><xsl:text>),&#10;&#x9;</xsl:text>
					</xsl:if>
				</xsl:for-each>
			
				<xsl:apply-templates select="."/>
				<xsl:if test="(count(following-sibling::*)>0)">
				<xsl:text>,&#10;&#x9;</xsl:text></xsl:if>
			</xsl:for-each>
		</xsl:if>
		
		
</xsl:template>

<!-- OR 
		content model: ( oid?, (formula | Atom | And | Or)* )
-->
<xsl:template match="ruleml:Or">

		<xsl:if test="./ruleml:oid">
			<xsl:apply-templates select="./ruleml:oid"/>
		</xsl:if>
		
		<xsl:if test="./ruleml:formula | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or">
			<xsl:for-each select="./ruleml:formula | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or">
				<xsl:apply-templates select="."/>
				<xsl:if test="(following-sibling::node()[position() > 1])"><xsl:text>,</xsl:text></xsl:if>
			</xsl:for-each>
		</xsl:if>

</xsl:template>

<!-- EQUAL 
		content model:
		(   (oid)?, (degree)?  (side | Ind | Data | Var | Expr | Plex ),  (side | Ind | Data | Var | Expr | Plex )	)
-->
<xsl:template match="ruleml:Equal">		
	<xsl:if test="./ruleml:oid">
			<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>			

	<xsl:for-each select="./ruleml:formula | ./ruleml:Atom | ./ruleml:And | ./ruleml:Or | ./ruleml:Data | ./ruleml:Plex | ./ruleml:Ind | ./ruleml:Var | ./ruleml:Expr" >
		<xsl:if test="(count(preceding-sibling::*) > 0)"><xsl:text>=</xsl:text></xsl:if>
		<xsl:apply-templates select="."/>											
	</xsl:for-each>									
</xsl:template>





<!-- FORALL - Explicit universal quantifier.
		!! Translating only Impies and Atom !!
		content model: ( oid?, (declare | Var)+, (formula | Atom | Implies | Equivalent | Forall) )
-->
<xsl:template match="ruleml:Forall">
	<xsl:if test="./ruleml:oid">
			<xsl:apply-templates select="./ruleml:oid"/>
	</xsl:if>
	<xsl:for-each select="./ruleml:Atom | ./ruleml:Implies | ./ruleml:Equivalent">
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="."/>											
	</xsl:for-each>										
</xsl:template>

<!-- NORM - Explicit universal quantifier.
		content model: ( Expr) )
-->
<xsl:template match="ruleml:norm">
	<xsl:apply-templates select="./ruleml:Expr"/>						
</xsl:template>

<!-- QUERY - Explicit universal quantifier.
		content model: ( Expr) )
-->
<xsl:template match="ruleml:Query">
	<xsl:text>:- solve(</xsl:text>
<!--	<xsl:apply-templates select="./ruleml:HoldsState"/>	-->
		<xsl:for-each select="./ruleml:HoldsState | ./ruleml:Atom">
			<xsl:apply-templates select="."/> 
			<xsl:if test="count(following-sibling::*) > 0">,</xsl:if>	
		</xsl:for-each>
	<xsl:text>)</xsl:text>					
</xsl:template>

<!-- OVERRIDES - Explicit universal quantifier.
		content model: ( oid) )
-->
<xsl:template match="ruleml:Overrides">
	<xsl:text>overrides(</xsl:text>
	<xsl:for-each  select="./ruleml:oid">	
		<xsl:text></xsl:text>			
		<xsl:apply-templates select="./ruleml:Ind"/>	
		<xsl:text></xsl:text>			
		<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>			
	</xsl:for-each>
	<xsl:for-each  select="./ruleml:Atom">		
		<xsl:apply-templates select="."/>			
		<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>			
	</xsl:for-each>
	<xsl:text>)</xsl:text>					
</xsl:template>


<!-- format Reaction elements and their parts -->
<xsl:template name="handle_eca">
	<xsl:param name="time" select=" '_' "/>
	<xsl:param name="event" select=" '_' "/>
	<xsl:param name="body" select=" '_' "/>
	<xsl:param name="action" select=" '_' "/>
	<xsl:param name="postcond" select=" '_' "/>
	<xsl:param name="alternative" select=" '_' "/>
	<xsl:param name="context" select=" descendant::*[name() = 'Reaction'] "/>

	<!-- initiate global variable to false
	<xsl:variable name="nested_reaction">0</xsl:variable>
     check if ancestor was a Reaction element 
	<xsl:for-each select="ancestor::*">
		<xsl:if test="name(.) = 'Reaction' ">
			<xsl:variable name="nested_reaction">1</xsl:variable>
		</xsl:if>
	</xsl:for-each>
	<xsl:text>#</xsl:text><xsl:value-of select="$nested_reaction"/><xsl:text>#</xsl:text> -->

<!-- print ancestor reaction -->	
	<!--ancestors=<xsl:value-of select="count(ancestor::*[name() = 'Reaction'])"/>,descendants=<xsl:value-of select="count(descendant::*[name() = 'Reaction'])"/> -->
	
<!--	<xsl:if test="count(ancestor::*[name() = 'Reaction']) = 0"> 
	counter=<xsl:value-of select="count(descendant::*[name() = 'Reaction'])"/> -->
	<xsl:if test="count($context) = 0">
<!--		called_with <xsl:value-of select="$event"/>#<xsl:value-of select="$body"/>#<xsl:value-of select="$action"/>#<xsl:value-of select="$postcond"/>#<xsl:value-of select="$alternative"/># -->
	
	<xsl:if test="./ruleml:event/ruleml:Message[@mode='inbound']">
		<xsl:text>rcvMsg(</xsl:text>
	</xsl:if>
	<xsl:if test="./ruleml:action/ruleml:Message[@mode='outbound']">
		<xsl:text>sendMsg(</xsl:text>
	</xsl:if>
	<xsl:if test="./ruleml:action/ruleml:Initiates">
		<xsl:text>initiates(</xsl:text>
	</xsl:if>
	<xsl:if test="./ruleml:action/ruleml:Terminates | ./ruleml:Terminates">
		<xsl:text>terminates(</xsl:text>
	</xsl:if>
	<xsl:if test="not(./ruleml:event/ruleml:Message) and not(./ruleml:action/ruleml:Message)  and not(./ruleml:action/ruleml:Initiates) and not(./ruleml:action/ruleml:Terminates) and  not(./ruleml:Terminates)  and not(./ruleml:Initiates)">
		<xsl:text>eca(</xsl:text>
	</xsl:if>
	<xsl:if test="current()[@exec='active']">
        	<xsl:if test="$time = '_' "><xsl:text>_</xsl:text></xsl:if>
			<xsl:if test="$time != '_' "><xsl:apply-templates select="$time"/></xsl:if>
			
			<xsl:text>,</xsl:text>
			
			<xsl:if test="$event = '_' "><xsl:text>_</xsl:text></xsl:if>
			<xsl:if test="$event != '_' "><xsl:apply-templates select="$event"/></xsl:if>
			
			<xsl:text>,</xsl:text>
				
			<xsl:if test="$body = '_' "><xsl:text>_</xsl:text></xsl:if>						
			<xsl:if test="$body != '_' ">
				<xsl:apply-templates select="$body"/>
				<!--<xsl:apply-templates select="./ruleml:HoldsState"/>-->
			</xsl:if>
			
			<xsl:text>,</xsl:text>
				
			<xsl:if test="$action = '_' "><xsl:text>_</xsl:text></xsl:if>
			<xsl:if test="$action != '_' ">
				<xsl:apply-templates select="$action"/>		
				<!--<xsl:if test="./ruleml:Assert">
					<xsl:apply-templates select="./ruleml:Assert"/>
				</xsl:if>	
				<xsl:if test="./ruleml:Initiates">
					<xsl:apply-templates select="./ruleml:Initiates"/>
				</xsl:if>-->
			</xsl:if>
			
			<xsl:text>,</xsl:text>
						
			<xsl:if test="$postcond = '_' "><xsl:text>_</xsl:text></xsl:if>
			<xsl:if test="$postcond != '_' "><xsl:apply-templates select="$postcond"/></xsl:if>
			
			<xsl:text>,</xsl:text>
						
			<xsl:if test="$alternative = '_' "><xsl:text>_</xsl:text></xsl:if>
			<xsl:if test="$alternative != '_' "><xsl:apply-templates select="$alternative"/></xsl:if>
	<xsl:text>)</xsl:text>
	</xsl:if>
	
	<xsl:if test="current()[@exec='passive']">
	
			<xsl:if test="$time != '_' ">				
				<xsl:apply-templates select="$time"/>
				<xsl:text>,</xsl:text>
			</xsl:if>
			
			<xsl:if test="$event != '_' ">
				<xsl:apply-templates select="$event"/>
				<xsl:if test="not(./ruleml:event/ruleml:Message) and ./ruleml:action/ruleml:Message">
					<xsl:text>,</xsl:text>
				</xsl:if>				
			</xsl:if>
						

			<xsl:if test="$body != '_' ">
				<xsl:apply-templates select="$body"/>
				<xsl:text>,</xsl:text>
			</xsl:if>
			
			<xsl:if test="$action != '_' ">
				<xsl:if test="not(./ruleml:event/ruleml:Message[@mode='outbound']) and not(./ruleml:action/ruleml:Message[@mode='outbound'])">
					<xsl:text>):-&#10;&#x9;</xsl:text>
				</xsl:if>
<!--				<xsl:if test="./ruleml:event/ruleml:Message and ./ruleml:action/ruleml:Message">
					<xsl:text>):-</xsl:text>
				</xsl:if>	-->			
				<xsl:apply-templates select="$action"/>
				<xsl:if test="not(./ruleml:event/ruleml:Message) and not(./ruleml:action/ruleml:Message)">
					<xsl:text>,</xsl:text>
				</xsl:if>				
			</xsl:if>
			
			<xsl:if test="$postcond != '_' ">
				<xsl:apply-templates select="$postcond"/>
				<xsl:text>,</xsl:text>
			</xsl:if>
			
			<xsl:if test="$alternative != '_' ">
				<xsl:apply-templates select="$alternative"/>
			</xsl:if>
			<xsl:if test="not(./ruleml:event/ruleml:Message) or not(./ruleml:action) and not(./ruleml:action/ruleml:Message)">
					<xsl:text>)</xsl:text>
			</xsl:if>
	
	</xsl:if>
	
	<xsl:if test="current()[@exec='reasoning']">
	
			<xsl:if test="$time != '_' ">t			
				<xsl:apply-templates select="$time"/>
				<xsl:text>,</xsl:text>
			</xsl:if>
			
			<xsl:if test="$event != '_' ">
				<xsl:apply-templates select="$event"/>				
					<xsl:text>,</xsl:text>								
			</xsl:if>
						

			<xsl:if test="$body != '_' ">
				<xsl:apply-templates select="$body"/>
				<xsl:text>,</xsl:text>
			</xsl:if>
			
			<xsl:if test="$action != '_' ">
				<xsl:apply-templates select="$action"/>
				</xsl:if>
			
			<xsl:if test="$postcond != '_' ">
				<xsl:apply-templates select="$postcond"/>
				<xsl:text>,</xsl:text>
			</xsl:if>
			
			<xsl:if test="$alternative != '_'">
				<xsl:apply-templates select="$alternative"/>
			</xsl:if>
			<xsl:if test="not(./ruleml:event/ruleml:Message) or not(./ruleml:action) and not(./ruleml:action/ruleml:Message)">
					<xsl:text>)</xsl:text>
			</xsl:if>
	
	</xsl:if>
	
	
	</xsl:if>
	
<!--	<xsl:if test="count(ancestor::*[name() = 'Reaction']) > 0"> -->

		<xsl:if test="count($context) > 0">
					<xsl:if test="./ruleml:event">
						

					
<!--					1:<xsl:value-of select="name(./ruleml:event/ruleml:Reaction/ruleml:action)"/>  -->
						<xsl:call-template name="handle_eca">
								<xsl:with-param name="time" select="./ruleml:event/ruleml:Reaction/ruleml:event"/>
								<xsl:with-param name="event" select="./ruleml:event/ruleml:Reaction/ruleml:action"/> 
								<xsl:with-param name="body" select="$body"/>
								<xsl:with-param name="action" select="$action"/>
								<xsl:with-param name="postcond" select="$postcond"/>
								<xsl:with-param name="alternative" select="$alternative"/>				
								<xsl:with-param name="context" select="./ruleml:Ind"/>				
						 </xsl:call-template>
					</xsl:if>
					<xsl:if test="not(./ruleml:event)">
<!--					1:<xsl:value-of select="name(./ruleml:Reaction/child::*[1])"/>  
					2:<xsl:value-of select="name(./ruleml:Reaction/child::*[2])"/>   -->
						<xsl:call-template name="handle_eca">
								<xsl:with-param name="time" select="./ruleml:Reaction/child::*[1]"/>
								<xsl:with-param name="event" select="./ruleml:Reaction/child::*[2]"/> 
								<xsl:with-param name="body" select="$body"/>
								<xsl:with-param name="action" select="$action"/>
								<xsl:with-param name="postcond" select="$postcond"/>
								<xsl:with-param name="alternative" select="$alternative"/>				
								<xsl:with-param name="context" select="./ruleml:Ind"/>				
						 </xsl:call-template>
					</xsl:if>
	</xsl:if>
	
</xsl:template>
<!-- handle head and body parts -->
<xsl:template name="handle_head_body">
	<xsl:param name="headpart" select="."/>
	<xsl:param name="bodypart" select="."/>
	<xsl:param name="custom" select="'0'"/>
	<xsl:param name="customOR" select="'0'"/>
	<xsl:param name="nocommas" select="'0'"/>
	
	<xsl:variable name="modes"/>
	
	<xsl:if test="$nocommas = '0'">
	
		<xsl:if test="$customOR = '0'">

			<xsl:for-each select="$headpart">
			<!--	<xsl:if test="(name(..) = 'Implies')">						
					<xsl:text>defeasible(</xsl:text>							
				</xsl:if>-->
				<xsl:apply-templates select="."/>
				<!--<xsl:if test="(name(..) = 'Implies')">						
					<xsl:text>)</xsl:text>							
					<xsl:if test="./ruleml:oid/ruleml:Ind and name(..) ='body' and not(@uri)">
				</xsl:if>-->
								
				<xsl:text>:- </xsl:text>
				<xsl:if test="name(../../..)!='And' and name(../../..)!='body' and name(../../..)!='action' and name(../../..)!='Reaction' and not(./following-sibling::*/child::*/ruleml:oid/ruleml:Ind) and not(./following-sibling::*/child::*/ruleml:oid/ruleml:Var)">
					<xsl:text>&#10;</xsl:text>
					<xsl:text>&#x9;</xsl:text>
				</xsl:if>
				
				<!--<xsl:if test="(name(..) = 'Implies')">						
					<xsl:text>testIntegrity(</xsl:text>			
					<xsl:apply-templates select="."/>					
					<xsl:text>),</xsl:text>							
				</xsl:if>-->
				
				<xsl:for-each select="(descendant::*[name() = 'Var' and @mode])">									
					<xsl:if test="@mode = '-' ">
						<xsl:text>free(</xsl:text>
						<xsl:value-of select="."/>
						<xsl:text>),&#10;</xsl:text>
						<xsl:text>&#x9;</xsl:text>
					</xsl:if>
					<xsl:if test="@mode = '+' ">
						<xsl:text>bound(</xsl:text>
						<xsl:value-of select="."/>
						<xsl:text>),&#10;</xsl:text>
						<xsl:text>&#x9;</xsl:text>
					</xsl:if>
			</xsl:for-each>
			</xsl:for-each>
			


			
			<xsl:for-each select="$bodypart">
			<xsl:if test="($custom = '1') and (preceding-sibling::node()[position() > 1])">,</xsl:if>
			
				
				<!--<xsl:if test="(name(..) = 'Implies')">
						<xsl:text>defeasible(</xsl:text>	
						<xsl:apply-templates select="."/>
						<xsl:text>),not(defeated("</xsl:text>						
						<xsl:value-of select="./../ruleml:oid/ruleml:Ind"/>						
						<xsl:text>",</xsl:text>
						<xsl:if test="./../ruleml:head/ruleml:Atom/ruleml:Ind=5">									
								<xsl:apply-templates select="./../../ruleml:Atom/ruleml:Expr/ruleml:Expr[1]"/>
								<xsl:text>.&#10;neg(blocked(defeasible(</xsl:text>
								<xsl:apply-templates select="./../../ruleml:Atom/ruleml:Expr/ruleml:Expr[1]"/>							
						</xsl:if>
						<xsl:if test="./../ruleml:head/ruleml:Atom/ruleml:Ind=10">												
								<xsl:apply-templates select="./../../ruleml:Atom/ruleml:Expr/ruleml:Expr[2]"/>
								<xsl:text>.&#10;neg(blocked(defeasible(</xsl:text>
								<xsl:apply-templates select="./../../ruleml:Atom/ruleml:Expr/ruleml:Expr[2]"/>							
						</xsl:if>		
						<xsl:text>))</xsl:text>		
						
						
						<xsl:text> :- defeasible(</xsl:text>	
						<xsl:apply-templates select="."/>
						<xsl:text>))</xsl:text>								
				</xsl:if>-->
				
					
				
				<xsl:if test="name(.) = 'HoldsState'">
					<xsl:text>:-</xsl:text>
					<xsl:text>bound(</xsl:text>
					<xsl:apply-templates select="./ruleml:state/ruleml:Expr/ruleml:Var[@mode='+']"/>
					<xsl:text>)</xsl:text>
					<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
					<xsl:text>bound(</xsl:text>
					<xsl:apply-templates select="./ruleml:time/ruleml:Var[@mode='+']"/>
					<xsl:text>)</xsl:text>
					<xsl:if test="count(following-sibling::*) > 0"><xsl:text>,</xsl:text></xsl:if>
				</xsl:if>
			<!--	
				<xsl:if test="(name(..) != 'Implies')">-->
				<xsl:apply-templates select="."/>
			<!--	</xsl:if>-->
					<xsl:if test="($custom = '0') and (following-sibling::node()[position() > 1]) and name(../..)!='Assert'">,&#10;</xsl:if>
					<!-- <xsl:text>.</xsl:text> -->
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="$customOR = '1'">
			<xsl:for-each select="$bodypart">
				<xsl:call-template name="handle_head_body">
					<xsl:with-param name="headpart" select="$headpart"/>
					<xsl:with-param name="bodypart" select="."/>
					<xsl:with-param name="custom" select="'0'"/>
					<xsl:with-param name="customOR" select="'0'"/>
				</xsl:call-template>

			</xsl:for-each>
		</xsl:if>
	</xsl:if>
	<xsl:if test="$nocommas = '1'">
			<xsl:for-each select="$headpart">			
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:text>:-</xsl:text>
			<xsl:text>&#10;</xsl:text>
			<xsl:for-each select="$bodypart">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
	</xsl:if>
</xsl:template>
<!-- handle ordered previously bound variables -->
	<xsl:template name="handle_ordered_bound">
		<xsl:param name="p"/>
		<xsl:if test="name(..) ='And'">	
	<xsl:text>,[</xsl:text>
		<xsl:for-each select="descendant::*[name() = 'Atom']/ruleml:Var">
			<xsl:value-of select="."/>,
		</xsl:for-each>	
	<xsl:text>]</xsl:text>
</xsl:if>							
	</xsl:template>
	
<xsl:template name="incrementValue">
  <xsl:param name="value"/>
    <xsl:if test="$value &lt;= 10">
      <xsl:value-of select="$value"/>
      <xsl:call-template name="incrementValue">
        <xsl:with-param name="value" select="$value + 1"/>
      </xsl:call-template>
    </xsl:if>
</xsl:template>
	
	
</xsl:stylesheet>




package ruleml.translator.drl2ruleml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import reactionruleml.AndInnerType;
import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.EqualType;
import reactionruleml.ExistsType;
import reactionruleml.IfType;
import reactionruleml.ImpliesType;
import reactionruleml.IndType;
import reactionruleml.NegType;
import reactionruleml.ObjectFactory;
import reactionruleml.OpAtomType;
import reactionruleml.OrInnerType;
import reactionruleml.RelType;
import reactionruleml.RuleMLType;
import reactionruleml.SlotType;
import reactionruleml.VarType;

public class RuleMLBuilder {
	private ObjectFactory factory = new ObjectFactory();

	public JAXBElement<SlotType> createSlot(JAXBElement<?> relationName,
			JAXBElement<?> content) {
		SlotType slotType = factory.createSlotType();
		slotType.getContent().add(relationName);
		slotType.getContent().add(content);
		return factory.createSlot(slotType);
	}

	public JAXBElement<VarType> createVar(String content) {
		VarType varType = factory.createVarType();
		varType.getContent().add(content);
		return factory.createVar(varType);
	}

	public JAXBElement<IndType> createInd(String content) {
		IndType indType = factory.createIndType();
		indType.getContent().add(content);
		return factory.createInd(indType);
	}

	public JAXBElement<AtomType> createAtom(JAXBElement<?>[] content) {
		AtomType atomType = factory.createAtomType();
		atomType.getContent().addAll(Arrays.asList(content));
		return factory.createAtom(atomType);
	}

	public JAXBElement<AndInnerType> createAnd(JAXBElement<?>[] content) {
		AndInnerType andType = factory.createAndInnerType();
		andType.getFormulaOrAtomOrAnd().addAll(convertJAXBArray(content));
		return factory.createAnd(andType);
	}

	public JAXBElement<OrInnerType> createOr(JAXBElement<?>[] content) {
		OrInnerType orType = factory.createOrInnerType();
		orType.getFormulaOrAtomOrAnd().addAll(convertJAXBArray(content));
		return factory.createOr(orType);
	}

	public JAXBElement<NegType> createNeg(JAXBElement<?>[] content) {
		NegType negType = factory.createNegType();

		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement.getValue() instanceof AtomType) {
				negType.setAtom((AtomType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof EqualType) {
				negType.setEqual((EqualType) jaxbElement.getValue());
			}
		}
		return factory.createNeg(negType);
	}

	public JAXBElement<?> createExists(JAXBElement<?>[] content) {
		ExistsType existsType = factory.createExistsType();
		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement.getValue() instanceof AndInnerType) {
				existsType.setAnd((AndInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof OrInnerType) {
				existsType.setOr((OrInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof AtomType) {
				existsType.setAtom((AtomType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof NegType) {
				existsType.setNeg((NegType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof EqualType) {
				existsType.setEqual((EqualType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof ExistsType) {
				existsType.setExists((ExistsType) jaxbElement.getValue());
			}
		}
		return factory.createExists(existsType);
	}

	public RelType createRel(String content) {
		RelType relType = factory.createRelType();
		relType.getContent().add(content);
		return relType;
	}

	public JAXBElement<OpAtomType> createOp(RelType relType) {
		OpAtomType opAtomType = factory.createOpAtomType();
		opAtomType.setRel(relType);
		return factory.createAtomTypeOp(opAtomType);
	}

	public JAXBElement<IfType> createIf(JAXBElement<?>[] content) {
		IfType ifType = factory.createIfType();

		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement.getValue() instanceof AndInnerType) {
				ifType.setAnd((AndInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof OrInnerType) {
				ifType.setOr((OrInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof AtomType) {
				ifType.setAtom((AtomType) jaxbElement.getValue());
			}
		}

		return factory.createIf(ifType);
	}

	public JAXBElement<AssertType> createAssert(JAXBElement<?>[] content) {
		AssertType assertType = factory.createAssertType();
		assertType.getFormulaOrRulebaseOrAtom().addAll(
				convertJAXBArray(content));
		return factory.createAssert(assertType);
	}

	public JAXBElement<RuleMLType> createRuleML(JAXBElement<?>[] content) {
		RuleMLType ruleMLType = factory.createRuleMLType();
		ruleMLType.getAssertOrRetractOrQuery()
				.addAll(convertJAXBArray(content));
		return factory.createRuleML(ruleMLType);
	}

	public JAXBElement<ImpliesType> createImplies(JAXBElement<?>[] content) {
		ImpliesType impliesType = factory.createImpliesType();
		impliesType.getContent().addAll(Arrays.asList(content));
		return factory.createImplies(impliesType);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<?> convertJAXBArray(JAXBElement<?>[] content) {
		List result = new ArrayList();

		for (JAXBElement<?> jaxbElement : content) {
			result.add(jaxbElement.getValue());
		}

		return result;
	}

	public ObjectFactory getFactory() {
		return factory;
	}

	public String marshal(JAXBElement<RuleMLType> ruleML) {
		try {
			JAXBContext jContext = JAXBContext.newInstance("reactionruleml");

			Marshaller marshaller = jContext.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			StringWriter writer = new StringWriter();
			marshaller.marshal(ruleML.getValue(), writer);
			return writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}

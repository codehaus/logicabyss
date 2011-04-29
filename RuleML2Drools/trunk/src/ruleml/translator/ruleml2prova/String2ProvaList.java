package ruleml.translator.ruleml2prova;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;
import ws.prova.reference2.ProvaVariableImpl;

public class String2ProvaList {

	private String regex = "\\.?@\\d+=";
	private LinkedList ll = new LinkedList();

	public ProvaList createProvaList(String output) {
		String[] items = output.split(",");
		int i = 0;
		if (items[0].indexOf("id") != -1)
			i = 1;
		String id = items[i].substring(0, items[i].length() - i);

		ProvaList list = ProvaListImpl.create(new ProvaObject[] {
				ProvaConstantImpl.create(id),
				ProvaConstantImpl.create(removeQutationMark(items[i + 1])),
				ProvaConstantImpl.create(removeQutationMark(items[i + 2])),
				ProvaConstantImpl.create(removeQutationMark(items[i + 3])),
				parseContent(output.substring(output.indexOf(items[i + 3])
						+ items[i + 3].length() + 1, output.length() - 1)) });
		return list;

	}

	private String removeQutationMark(String string) {
		String result = "";
		// TODO Auto-generated method stub
		if (string.startsWith("\""))
			result = string.substring(1, string.length() - 1);
		else
			result = string;
		return result;
	}

	private String removeComma(String content) {
		// TODO Auto-generated method stub
		if (content.startsWith(","))
			content = content.substring(1);
		if (content.endsWith(","))
			content = content.substring(0, content.length() - 1);
		return content;
	}

	private ProvaList parseContent(String content) {

		List list = new ArrayList();
		// TODO Auto-generated method stub
		if (content.startsWith("["))
			content = content.substring(1, content.length() - 1);
		if (content.indexOf("=") != -1) {
			if (content.contains("\n"))
				content = content.replaceAll("\r\n", "");
			content = content.substring(0, content.length() - 1);
			Pattern pattern = Pattern.compile(regex);
			Matcher m = pattern.matcher(content);
			while (m.find()) {
				content = m.replaceAll(",");
				content = "substitutions" + content;
			}
		}
		int tag = 0;
		if (content.indexOf("[") == -1 || content.indexOf("]") == -1) {
			parseSimpleContent(list, content);
		} else
			for (int i = 0; i < content.length(); i++) {
				if (content.charAt(i) == '['
						|| (i == content.length() - 1 && content.charAt(i) != ']')) {
					if ((i - tag) != 0 && ll.isEmpty()) {
						String temp = "";
						if (i == content.length() - 1)
							temp = content.substring(tag, i + 1);
						else
							temp = content.substring(tag, i);
						if (!temp.equalsIgnoreCase(",")) {
							parseSimpleContent(list, temp);
						}
					}
					ll.addFirst(i);
					tag = i;
				} else if (!ll.isEmpty() && content.charAt(i) == ']') {
					int pos = (Integer) ll.removeFirst();
					if (ll.isEmpty())
						list.add(parseContent(content.substring(pos, i + 1)));
					tag = i + 1;
				}

			}
		return ProvaListImpl.create(list);
	}

	private void parseSimpleContent(List list, String content) {
		// TODO Auto-generated method stub
		content = removeComma(content);
		String[] objs = content.split(",");
		for (int j = 0; j < objs.length; j++) {
			char firstChar = objs[j].charAt(0);
			if (Character.isLetter(firstChar)
					&& Character.isUpperCase(firstChar))
				list.add(ProvaVariableImpl.create(objs[j]));
			else if (Character.isLetter(firstChar)
					&& Character.isLowerCase(firstChar))
				list.add(ProvaConstantImpl.create(objs[j]));
			else if (firstChar == ('\"') || firstChar == ('\''))
				list.add(ProvaConstantImpl.create(removeQutationMark(objs[j])));
			else if (isNumeric(objs[j]))
				list.add(ProvaConstantImpl.create(objs[j]));
			else if (firstChar == '@')
				list.add(ProvaVariableImpl.create(removeQutationMark(objs[j])));
		}
	}

	private boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr == 46)
				continue;
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}
}

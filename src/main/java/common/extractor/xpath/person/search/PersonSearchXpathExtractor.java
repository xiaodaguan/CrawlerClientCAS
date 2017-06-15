package common.extractor.xpath.person.search;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.PersonData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class PersonSearchXpathExtractor extends XpathExtractor<PersonData> implements PersonSearchExtractorAttribute {

	@Override public void processPage(PersonData data, Node domtree, Map<String, Component> comp, String... args) {

		this.parseCompany(data, domtree, comp.get("company"));
		this.parsePosition(data, domtree, comp.get("position"));
		this.parseImgUrl(data, domtree, comp.get("image"));
		this.parseSex(data, domtree, comp.get("sex"));
		this.parseBrief(data, domtree, comp.get("brief"));
		this.parseNation(data, domtree, comp.get("nation"));
		this.parseNativePlace(data, domtree, comp.get("nativePlace"));
		this.parseBirthday(data, domtree, comp.get("birthday"));
		this.parsePoliticalStatus(data, domtree, comp.get("politicalStatus"));
		this.parseSchool(data, domtree, comp.get("school"));
		this.parseMajor(data, domtree, comp.get("major"));
		this.parseEducationBackground(data, domtree, comp.get("educationBackground"));
		this.parseProfession(data, domtree, comp.get("profession"));
		this.parseInterest(data, domtree, comp.get("interest"));
		this.parseEducationHistory(data, domtree, comp.get("educationHistory"));
		this.parseWorkExperience(data, domtree, comp.get("workExperience"));

	}

	public void parseWorkExperience(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		if (nl.getLength() != 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				str += nl.item(i).getTextContent() + "\n";
			}
			data.setWorkExperience(str);
		}
	}

	public void parseEducationHistory(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		if (nl.getLength() != 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				str += nl.item(i).getTextContent() + "\n";
			}
			data.setEducationHistory(str);
		}
	}

	public void parseInterest(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setInterest(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseProfession(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setProfession(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseEducationBackground(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setEducationBackground(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseMajor(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setMajor(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseSchool(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setSchool(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parsePoliticalStatus(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setPoliticalStatus(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseBirthday(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null) {
			String date = StringUtil.format(nl.item(0).getTextContent()).replace("年", "-").replace("月", "");
			date = date.substring(date.indexOf("：") + 1);
			data.setBirthday(timeProcess(date));
		}
	}

	public void parseNativePlace(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setNativePlace(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseNation(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setNation(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseBrief(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setBrief(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parseSex(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setSex(StringUtil.format(nl.item(0).getTextContent()));
	}

	public void parsePosition(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setPosition(StringUtil.format(nl.item(0).getTextContent()));
	}

	@Override public void parseUrl(List<PersonData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}

	@Override public void parseTitle(List<PersonData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		for (int i = 0; i < nl.getLength(); i++) {
			PersonData vd = new PersonData();
			vd.setName(StringUtil.format(nl.item(i).getTextContent()));
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}

	@Override public String parseNext(Node dom, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return null;
		if (nl.item(0) != null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	/**
	 * 摘要
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override public void parseBrief(List<PersonData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setBrief(nl.item(i).getTextContent());
		}
	}

	public void parseAuthor(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setName(StringUtil.format(nl.item(0).getTextContent()));
	}

	private void parseCompany(PersonData data, Node domtree, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return;
		String str = "";// 正文文本
		for (int i = 0; i < nl.getLength(); i++) {
			str += nl.item(i).getTextContent() + "\r\n";
		}
		data.setCompany(str);
	}

	public void parseImgUrl(PersonData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String imgs = "";
		for (int i = 0; i < nl.getLength(); i++) {
			imgs += StringUtil.format(nl.item(i).getTextContent()) + ";";
		}
		data.setImgUrl(imgs);
	}

	@Override public void processList(List<PersonData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"));

	}

	@Override public void parseSource(List<PersonData> list, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parseAuthor(List<PersonData> list, Node domtree, Component component, String... content) {
		// TODO Auto-generated method stub

	}

	@Override public void parsePubtime(List<PersonData> list, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

}

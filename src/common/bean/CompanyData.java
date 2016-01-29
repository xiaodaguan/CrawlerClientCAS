package common.bean;

@SuppressWarnings("serial")
public class CompanyData extends CommonData {


	private String name;//公司名称
	private String ico;//公司logo
	private String address;//所在地
	private String field;//涉足领域
	private String products;//相关产品
	private String foundDate;//成立时间
	private String website;//官方网站
	private String contact;//联系方式
	private String brief;//简介
	private String briefProducts;//相关产品简介
	private String fundingExperience;//融资经历

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		this.ico = ico;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public String getFoundDate() {
		return foundDate;
	}

	public void setFoundDate(String foundDate) {
		this.foundDate = foundDate;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getBriefProducts() {
		return briefProducts;
	}

	public void setBriefProducts(String briefProducts) {
		this.briefProducts = briefProducts;
	}

	public String getFundingExperience() {
		return fundingExperience;
	}

	public void setFundingExperience(String fundingExperience) {
		this.fundingExperience = fundingExperience;
	}
}

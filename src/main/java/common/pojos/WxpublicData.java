package common.pojos;

@SuppressWarnings("serial")
public class WxpublicData extends CommonData {

	private String name;
	private String url;
	private String brief;
	private String weixinName;
	private String userIcon;
	private String posIcon;
	private String openId;
	private String verify;
	private int paperCount;
	private int customizeId;// bang_customized_vip表中id
	
	private int fromAriticle;//是否为，采集文章的同时采集到的公众号
	private int cityId;
	

	public int getCityId()
	{
		return cityId;
	}

	public void setCityId(int cityId)
	{
		this.cityId = cityId;
	}

	public int getFromAriticle() {
		return fromAriticle;
	}

	public void setFromAriticle(int fromAriticle) {
		this.fromAriticle = fromAriticle;
	}

	public int getCustomizeId() {
		return customizeId;
	}

	public void setCustomizeId(int customizeId) {
		this.customizeId = customizeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getWeixinName() {
		return weixinName;
	}

	public void setWeixinName(String weixinName) {
		this.weixinName = weixinName;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public String getPosIcon() {
		return posIcon;
	}

	public void setPosIcon(String posIcon) {
		this.posIcon = posIcon;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public int getPaperCount() {
		return paperCount;
	}

	public void setPaperCount(int paperCount) {
		this.paperCount = paperCount;
	}

}

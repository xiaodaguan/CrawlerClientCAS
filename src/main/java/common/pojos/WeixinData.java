package common.pojos;

@SuppressWarnings("serial")
public class WeixinData extends CommonData
{

	private String brief;
	private String pubtime;
	// private String cacheurl;
	// private String relnum;
	private String source;
	private String imgUrl;
	private String sameUrl;
	private String author;
	private int readNum;
	private int praiseNum;
	private int samenum;
	private int onlyUpdate;
	private String weixinName;
	private int category1;
	private int category2;
	private int category3;
	private String contentHtml;// 正文部分的html源码

	private String headImg;// 列表页简介图片

	private int customizeId;// customized id
	private int cityId;//
	private int gongzhongId;
	
	@Override
	public String toString() {

		return "*****\n" + title + "\n" + brief + "\n" + pubtime + "\n" + source + "\n" + imgUrl + "\n" + sameUrl + "\n"
				+ author + "\n" + readNum + "\n" + praiseNum + "\n" + samenum + "\n";

	};

	public int getGongzhongId()
	{
		return gongzhongId;
	}

	public void setGongzhongId(int gongzhongId)
	{
		this.gongzhongId = gongzhongId;
	}

	private String crawler_cookie;// 列表页自动获取的cookie传到详情页

	public String getCrawler_cookie()
	{
		return crawler_cookie;
	}

	public void setCrawler_cookie(String crawler_cookie)
	{
		this.crawler_cookie = crawler_cookie;
	}

	public int getCityId()
	{
		return cityId;
	}

	public void setCityId(int cityId)
	{
		this.cityId = cityId;
	}

	public int getCustomizeId()
	{
		return customizeId;
	}

	public void setCustomizeId(int customizeId)
	{
		this.customizeId = customizeId;
	}

	public String getContentHtml()
	{
		return contentHtml;
	}

	public void setContentHtml(String contentHtml)
	{
		this.contentHtml = contentHtml;
	}

	public String getHeadImg()
	{
		return headImg;
	}

	public void setHeadImg(String headImg)
	{
		this.headImg = headImg;
	}

	public int getCategory1()
	{
		return category1;
	}

	public void setCategory1(int category1)
	{
		this.category1 = category1;
	}

	public int getCategory2()
	{
		return category2;
	}

	public void setCategory2(int category2)
	{
		this.category2 = category2;
	}

	public int getCategory3()
	{
		return category3;
	}

	public void setCategory3(int category3)
	{
		this.category3 = category3;
	}

	public String getWeixinName()
	{
		return weixinName;
	}

	public void setWeixinName(String weixinName)
	{
		this.weixinName = weixinName;
	}

	public int getOnlyUpdate()
	{
		return onlyUpdate;
	}

	public void setOnlyUpdate(int onlyUpdate)
	{
		this.onlyUpdate = onlyUpdate;
	}

	public int getReadNum()
	{
		return readNum;
	}

	public void setReadNum(int readNum)
	{
		this.readNum = readNum;
	}

	public int getPraiseNum()
	{
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum)
	{
		this.praiseNum = praiseNum;
	}

	public String getSameUrl()
	{
		return sameUrl;
	}

	public void setSameUrl(String sameUrl)
	{
		this.sameUrl = sameUrl;
	}

	public String getBrief()
	{
		return brief;
	}

	public void setBrief(String brief)
	{
		this.brief = brief;
	}

	public String getPubtime()
	{
		return pubtime;
	}

	public void setPubtime(String pubtime)
	{
		this.pubtime = pubtime;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public int getSamenum()
	{
		return samenum;
	}

	public void setSamenum(int samenum)
	{
		this.samenum = samenum;
	}

}

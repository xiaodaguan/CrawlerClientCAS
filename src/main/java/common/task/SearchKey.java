package common.task;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class SearchKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8592400718234618947L;

	private long ID;
	private int CATEGORY_CODE;
	private String KEYWORD;
	private String SITE_ID;
	private String SITE_NAME;
	private int USER_ID;
	private Time PROPOSE_TIME;
	private int STATUS;
	private String TYPE;
	private int CATEGORY1;
	private String EB_STYLE;
	private int IS_USABLE;
	private int HIGHWAY;
	private int DEBUG;
	private int PRIORITY;
	private int KEYWORD_BELONG;

	@Override
	public String toString(){
		return "CategoryCode["+CATEGORY_CODE+"], Keyword["+KEYWORD+"], SiteId[{"+SITE_ID+"}], SiteName["+SITE_NAME+"]";
	}

	public List<Integer> getMediaTypeList(){
		if(TYPE==null||TYPE.length()==0){
			return null;
		}

		String[] strArray = TYPE.split(";");
		ArrayList<Integer> integerArrayList = new ArrayList<>();
		for(String str: strArray){
			if(str.length()>0){
				int type = Integer.parseInt(str);
				integerArrayList.add(type);
			}
		}
		return integerArrayList;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public long getID() {
		return ID;
	}

	public void setID(long ID) {
		this.ID = ID;
	}

	public int getCATEGORY_CODE() {
		return CATEGORY_CODE;
	}

	public void setCATEGORY_CODE(int CATEGORY_CODE) {
		this.CATEGORY_CODE = CATEGORY_CODE;
	}

	public String getKEYWORD() {
		return KEYWORD;
	}

	public void setKEYWORD(String KEYWORD) {
		this.KEYWORD = KEYWORD;
	}

	public String getSITE_ID() {
		return SITE_ID;
	}

	public void setSITE_ID(String SITE_ID) {
		this.SITE_ID = SITE_ID;
	}

	public String getSITE_NAME() {
		return SITE_NAME;
	}

	public void setSITE_NAME(String SITE_NAME) {
		this.SITE_NAME = SITE_NAME;
	}

	public int getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(int USER_ID) {
		this.USER_ID = USER_ID;
	}

	public Time getPROPOSE_TIME() {
		return PROPOSE_TIME;
	}

	public void setPROPOSE_TIME(Time PROPOSE_TIME) {
		this.PROPOSE_TIME = PROPOSE_TIME;
	}

	public int getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(int STATUS) {
		this.STATUS = STATUS;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String TYPE) {
		this.TYPE = TYPE;
	}

	public int getCATEGORY1() {
		return CATEGORY1;
	}

	public void setCATEGORY1(int CATEGORY1) {
		this.CATEGORY1 = CATEGORY1;
	}

	public String getEB_STYLE() {
		return EB_STYLE;
	}

	public void setEB_STYLE(String EB_STYLE) {
		this.EB_STYLE = EB_STYLE;
	}

	public int getIS_USABLE() {
		return IS_USABLE;
	}

	public void setIS_USABLE(int IS_USABLE) {
		this.IS_USABLE = IS_USABLE;
	}

	public int getHIGHWAY() {
		return HIGHWAY;
	}

	public void setHIGHWAY(int HIGHWAY) {
		this.HIGHWAY = HIGHWAY;
	}

	public int getDEBUG() {
		return DEBUG;
	}

	public void setDEBUG(int DEBUG) {
		this.DEBUG = DEBUG;
	}

	public int getPRIORITY() {
		return PRIORITY;
	}

	public void setPRIORITY(int PRIORITY) {
		this.PRIORITY = PRIORITY;
	}

	public int getKEYWORD_BELONG() {
		return KEYWORD_BELONG;
	}

	public void setKEYWORD_BELONG(int KEYWORD_BELONG) {
		this.KEYWORD_BELONG = KEYWORD_BELONG;
	}
}

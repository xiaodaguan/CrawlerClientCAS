package common.system;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class UserAttribute implements Serializable {

    private int id;
    private String ip;
    private String siteFlag;//用户负责站点
    private String name;//用户登录名
    private String pass;//密码
    private String cookie;//cookie
    private String userAgent;//模拟不同浏览器
    private int agentIndex;//不同agent的索引
    private int stat;//有效,被使用
    private String tip;
    private String referer;
    private boolean hadRun;//是否曾经运行过
    private Date lastLoginTime;//上次登陆时间
    private Date lastUsedTime;//上次登陆时间
    private boolean valid;// true 有效，false失效
    private boolean runStatus;// true 正在运行，false 没有运行
    private int tryCount;


    public void setLastUsedTime(Date lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public Date getLastUsedTime() {
        return lastUsedTime;
    }

    public boolean isRunStatus() {
        return runStatus;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public void setRunStatus(boolean runStatus) {
        this.runStatus = runStatus;
    }

    public boolean isHadRun() {
        return hadRun;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public int getTryCount() {
        return tryCount;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "[name:" + name + " last login:" + lastLoginTime + "]";
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public UserAttribute() {
    }

    public UserAttribute(String user, String pass) {
        this.name = user;
        this.pass = pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSiteFlag() {
        return siteFlag;
    }

    public void setSiteFlag(String string) {
        this.siteFlag = string;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getUsed() {
        return stat;
    }

    public void setUsed(int used) {
        this.stat = used;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public boolean getHadRun() {
        return hadRun;
    }

    public void setHadRun(boolean hadRun) {
        this.hadRun = hadRun;
    }

    public int getAgentIndex() {
        return agentIndex;
    }

    public void setAgentIndex(int agentIndex) {
        this.agentIndex = agentIndex;
    }

}

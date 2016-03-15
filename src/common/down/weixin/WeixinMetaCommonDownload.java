package common.down.weixin;

import common.bean.Header;
import common.bean.HtmlInfo;
import common.bean.WeixinData;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.StringUtil;
import common.util.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载元数据
 *
 * @author grs
 */
public class WeixinMetaCommonDownload extends GenericMetaCommonDownload<WeixinData> {
    private static String cookies = "";

    public WeixinMetaCommonDownload(SearchKey key) {
        super(key);
    }

    @Override
    public void process() {

        List<WeixinData> alllist = new ArrayList<WeixinData>();
        List<WeixinData> list = new ArrayList<WeixinData>();
        String url = getRealUrl(siteinfo, key.getId() > 0 ? key.getKey() : gloaburl);//
        int page = getRealPage(siteinfo);
        String keyword = key.getKey();
        map.put(keyword, 1);
        String nexturl = url;
        DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);

        // getKVandCookie(nexturl);
        HtmlInfo html = htmlInfo("META");

        String last = null;
        int totalCount = 0;
        while (nexturl != null && !nexturl.equals("")) {

            list.clear();

            html.setOrignUrl(nexturl);
            try {

                Header h = Systemconfig.dbService.randomHeaderFromDB();
                html.setCookie(h.getCookie());
                html.setUa(h.getUserAgent());
                Systemconfig.sysLog.log(keyword + ": " + url + "downloading...");
                Systemconfig.sysLog.log("downloading : " + nexturl);
                http.getContent(html);// 下载同时将response cookie保存

                if (checkBlock(html)) {// 验证是否被屏蔽
                    Systemconfig.sysLog.log("ip被屏蔽，请手动验证@列表页");
                    System.in.read();
                    continue;
                }

                // 如果有response cookie 则更新cookie，后续列表页下载可以继续使用
                if (html.getResponseCookie() != null && !html.getResponseCookie().equals("")) {
                    html.setCookie(html.getCookie() + "; " + html.getResponseCookie());
                }

                if (ifStop(html.getContent(), last)) break;
                nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "", html.getCookie());

                if (list.size() == 0) {
                    Systemconfig.sysLog.log(keyword + ": " + url + "元数据页面解析为空！！");
                    TimeUtil.rest(siteinfo.getDownInterval());
                    break;
                }
                Systemconfig.sysLog.log(keyword + ": " + url + "元数据页面解析完成。第[" + map.get(keyword) + "/" + page + "]页");
                last = html.getContent();
                totalCount += list.size();
                Systemconfig.dbService.getNorepeatData(list, "");
                if (list.size() == 0) {
                    Systemconfig.sysLog.log("无新数据。");
                    if (alllist.size() == 0) TimeUtil.rest(siteinfo.getDownInterval());
                    break;
                }
                alllist.addAll(list);

                map.put(keyword, map.get(keyword) + 1);
                if (map.get(keyword) > page) {
                    Systemconfig.sysLog.log("达到最大页数");

                    break;
                }
                url = nexturl;
                if (nexturl != null) TimeUtil.rest(siteinfo.getDownInterval());

            } catch (Exception e) {
                Systemconfig.sysLog.log("列表页异常{" + keyword + "}   [" + url + "]");
                e.printStackTrace();
                break;
            }
        }

        try {
            Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
//		dtc.process(alllist, siteinfo.getDownInterval(), null, key);
        for (WeixinData wd : alllist) {
            try {
                Systemconfig.dbService.saveData(wd);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        TimeUtil.rest(3 * siteinfo.getDownInterval());
    }

    /**
     * 判断是否被屏蔽
     *
     * @param html
     * @return true: 被屏蔽
     */
    protected boolean checkBlock(HtmlInfo html) {
        if (html.getContent().contains("您的访问过于频繁") && html.getContent().contains("验证")) return true;
        else return false;
    }

    /**
     * @param curr
     * @param last
     * @return false: 不是最后一页; true: 是最后一页
     */
    protected boolean ifStop(String curr, String last) {
        if (last != null && curr != null) {
            String currPage = StringUtil.regMatcher(curr, "<DOCUMENT>", "/DOCUMENT>");
            String lastPage = StringUtil.regMatcher(last, "<DOCUMENT>", "/DOCUMENT>");
            if (currPage != null && lastPage != null) {
                if (currPage.equals(lastPage)) {
                    System.out.println("当前页和上一页内容相同，已是最后一页，退出.");
                    return true;
                }
            }
        }

        return false;
    }

    private void loadCookie(HtmlInfo html) {

        cookies = StringUtil.getContent("config/weixin_search_meta.txt").replace("\r\n", "").replace("\n", "").trim();
        html.setCookie(cookies);
    }

//	@Override
//	protected void specialHtmlInfo(HtmlInfo html) {
//
//		cookies = StringUtil.getContent("config/weixin_search_meta.txt").replace("\r\n", "").replace("\n", "").trim();
//		html.setUa("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
//		// html.setHost("weixin.sogou.com");
//		// html.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		// html.setAcceptEncoding("gzip, deflate");
//		// html.setAcceptLanguage("zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//		// html.setConnection("keep-alive");
//		// html.setCacheControl("max-age=0");
//
//		// if (cookies.equals(""))
//		// testCookieAndGet(html);
//		// else
//		// cookies = Systemconfig.dbService.randomHeaderFromDB().getCookie();
//		html.setReferUrl("http://weixin.sogou.com/");
//		html.setCookie(cookies);
//
//	}
}

package common.extractor.xpath.weibo.monitor.sub;

import common.pojos.HtmlInfo;
import common.pojos.UserData;
import common.pojos.WeiboData;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.JsonUtil;
import common.utils.MD5Util;
import common.utils.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author grs
 */
public class SinaExtractor extends WeiboMonitorXpathExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SinaExtractor.class);

    // @Override
    // protected Node getRealDOM(HtmlInfo html) throws SAXException, IOException
    // {
    //
    // String content = html.getContent().replace("\r\n", "\n");
    // String[] lines = content.split("\n");
    // StringBuilder newContent = new StringBuilder();
    // for (String line : lines) {
    // if (line.contains("<script>")) {
    // if (line.contains("<div") && line.contains("/div>"))
    // newContent.append(line.substring(line.indexOf("div>"),
    // line.lastIndexOf("/div>")+5)).append("\n");
    // else {
    // newContent.append(line).append("\n");
    // }
    // } else {
    // newContent.append(line).append("\n");
    // }
    // }
    // html.setContent(newContent.toString());
    // StringUtil.writeFile("tempDom.htm", newContent.toString());
    // return super.getRealDOM(html);
    //
    // }

    /**
     * 覆盖微博评论解析方法,解析移动版json文件
     *
     * @param list
     * @param html
     * @param page
     * @param keyword
     * @return
     * @throws SAXException
     * @throws IOException
     */
    @Override
    public String templateComment(List<WeiboData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
        Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
//        Node domtree = getRealDOM(html);
//        if (domtree == null) {
//            LOGGER.info("DOM解析为NULL！！");
//            return null;
//
        CommonComponent comp = getRealComp(siteinfo, html.getCrawlerType().substring(0, html.getCrawlerType().indexOf(File.separator)));//得到数据的配置组件
        String cont = html.getContent();
        if(cont.contains("\"msg\": null")){
            return null;
        }
        /**
         * 解析json
         */
//        LOGGER.info(cont);
        JSONArray root = null;
        try {
            root = JSONArray.fromObject(cont);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("json content: \r\n" + cont);
        }

//        JSONArray cardGroup = null;
//        if (root.size() > 1) cardGroup = (JSONArray) root.getJSONObject(1).get("card_group");
//        else cardGroup = (JSONArray) root.getJSONObject(0).get("card_group");
//        if (cardGroup == null) {
//            LOGGER.info("debug");
//        }
        for (int j = 0; j < root.size(); j++) {
            JSONObject child = root.getJSONObject(j);
            JSONArray cardGroup = null;
            if (child.get("card_group") == null) continue;
            else {
                cardGroup = (JSONArray) child.get("card_group");
            }
            for (int i = 0; i < cardGroup.size(); i++) {
                WeiboData wd = new WeiboData();

                JSONObject comment = cardGroup.getJSONObject(i);
                String commentId = comment.getLong("id") + "";
//            wd.setMid(commentId);
                String commentPubtime = comment.getString("created_at").trim();
                
                //"03-01 17:32"
                if(commentPubtime.contains("-")){
	                if(commentPubtime.split("-")[0].length()!=4){
	                	commentPubtime = Calendar.getInstance().get(Calendar.YEAR)+"-"+commentPubtime;
	                }
                }
                
                
                wd.setPubtime(commentPubtime);
                wd.setPubdate(timeprocess(commentPubtime));
                String commentSource = comment.getString("source");
                wd.setSource(commentSource);
                JSONObject commentUser = comment.getJSONObject("user");
                {
                    String commentUserId = commentUser.getLong("id") + "";
                    wd.setUid(commentUserId);
                    String commentUserName = commentUser.getString("screen_name");
                    wd.setAuthor(commentUserName);
                    String commentUserImg = commentUser.getString("profile_image_url");
                    wd.setAuthorImg(commentUserImg);
                    String commentUserUrl = "http://weibo.com" + commentUser.getString("profile_url");
                    wd.setAuthorurl(commentUserUrl);
                }
                String commentContent = comment.getString("text");
                wd.setContent(commentContent);
                String commentLikeCount = comment.getInt("like_counts") + "";
                //todo
                String commentUrl = comment.getString("url");
                wd.setCommentUrl(commentUrl);
                wd.setMid(commentUrl.substring(commentUrl.indexOf("/comment?id=")+12,commentUrl.indexOf("&reply=")));
                wd.setUrl(html.getOrignUrl());
                list.add(wd);


//                LOGGER.info(commentUrl);

            }
        }

//        parseCommentAuthorUrl(list, domtree, comp.getComponents().get("comment_author_url"));
//        parseCommentAuthorImg(list, domtree, comp.getComponents().get("comment_author_img"));
//        parseCommentTime(list, domtree, comp.getComponents().get("comment_time"));
//        parseCommentContent(list, domtree, comp.getComponents().get("comment_content"));
//        parseCommentUid(list, domtree, comp.getComponents().get("comment_uid"));
        for (WeiboData wd : list) {
            wd.setMd5(MD5Util.MD5(wd.getUid() + wd.getPubtime()));
            wd.setId(Integer.parseInt(keyword[0]));
//            wd.setInserttime(new Date());
        }

        Node domtree = null;
        String nextPage = parseCommentNext(domtree, comp.getComponents().get("comment_next"), keyword[1]);
        return nextPage;
    }

    @Override
    public String parseCommentNext(Node domtree, Component component, String... args) {
        String currUrl = args[0];
        String currPage = currUrl.substring(currUrl.lastIndexOf("&page=") + 6);
        int currPageNum = Integer.parseInt(currPage);
        String nextUrl = currUrl.replace("&page=" + currPage, "&page=" + (currPageNum + 1));
        return nextUrl;
    }

    @Override
    protected Node getRealDOM(HtmlInfo html) throws SAXException, IOException {
        String content = html.getContent();
        String con = "";
        String flag = html.getCrawlerType().substring(0, html.getCrawlerType().indexOf(File.separator));
        if (html.getOrignUrl().contains("http://weibo.com/p/aj/mblog/mbloglist")) {
            try {
                con = JsonUtil.getStringByKey(content, "data");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (html.getOrignUrl().startsWith("http://weibo.com/aj")) {
            try {
                con = JsonUtil.getJsonObjectByKey(content, "data").getString("html");
            } catch (Exception e) {
                // e.printStackTrace();
            }
        } else {
            String id = StringUtil.regMatcher(content, "\\$CONFIG\\['page_id'\\]\\s*=\\s*'", "'");
            if (flag.equals("USER")) {
                String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.header.head.index\",", "\\)</script>");
                if (temp != null) {
                    con = JsonUtil.getStringByKey("{" + temp, "html");
                }
                temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.nav.index\",", "\\)</script>");
                if (temp != null) {
                    con += JsonUtil.getStringByKey("{" + temp, "html");
                }
                temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\",", "\\)</script>");// 粉丝\微博|关注...
                if (temp != null) {
                    con += JsonUtil.getStringByKey("{" + temp, "html");
                }
            } else if (flag.equals("USERINFO")) {
                String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.textMulti.index\",", "\\)</script>");
                if (temp != null) {
                    con = JsonUtil.getStringByKey("{" + temp, "html");
                }
                temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"\",\"domid\":\"Pl_Official_LeftInfo__25\",", "\\)</script>");
                if (temp != null) {
                    con += JsonUtil.getStringByKey("{" + temp, "html");
                }
            } else if (flag.equals("FANS")) {
                String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.followTab.index\",", "\\)</script>");
                if (temp != null) {
                    con = JsonUtil.getStringByKey("{" + temp, "html");
                }
            } else if (flag.equals("FOLLOW")) {
                String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.followTab.index\",", "\\)</script>");
                if (temp != null) {
                    con = JsonUtil.getStringByKey("{" + temp, "html");
                }
            } else if (flag.equals("DATA")) {
                // String temp = StringUtil.regMatcher(content,
                // "<script>FM.view\\(\\{\"ns\":\"pl.content.homeFeed.index\",",
                // "\\)</script>");
                // if (temp != null) {
                // con = JsonUtil.getStringByKey("{" + temp, "html");
                // }
                // temp = StringUtil.regMatcher(content,
                // "<script>FM.view\\(\\{\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Official_MyProfileFeed__22\",",
                // "\\)</script>");// 微博列表
                // if (temp != null) {
                // con += JsonUtil.getStringByKey("{" + temp, "html");
                // }
                // if (con.equals(""))
                // con = content;

                content = content.replace("\r\n", "\n");
                String[] lines = content.split("\n");
                StringBuilder newContent = new StringBuilder();
                for (String line : lines) {
                    if (line.contains("<script>")) {
                        if (line.contains("<div") && line.contains("/div>"))
                            newContent.append(line.substring(line.indexOf("div>"), line.lastIndexOf("/div>") + 5)).append("\n");
                        else {
                            newContent.append(line).append("\n");
                        }
                    } else {
                        newContent.append(line).append("\n");
                    }
                }
                con = newContent.toString();
                con = con.replace("\\\"", "\"").replace("\\/", "/");

            }
            if (id != null && !id.equals("")) con += "<AUTHORID>" + id + "</AUTHORID>";
        }
        html.setContent(con);
        StringUtil.writeFile("tmpDom.htm", con);
        return super.getRealDOM(html);
    }

    @Override
    public void parseFansUrl(UserData data, Node dom, Component component, String... args) {
        super.parseFansUrl(data, dom, component);
        String url = data.getFansUrl();
        if (url != null && url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?")) + "?relate=fans";
        }
        data.setFansUrl(url);
    }

    @Override
    public void parseFollowUrl(UserData data, Node dom, Component component, String... args) {
        super.parseFollowUrl(data, dom, component);
        String url = data.getFollowUrl();
        if (url != null && url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        data.setFollowUrl(url);
    }

    @Override
    public void parseInfoUrl(UserData data, Node dom, Component component, String... args) {
        super.parseInfoUrl(data, dom, component);
        String url = data.getInfoUrl();
        if (url != null && url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        data.setInfoUrl(url);
    }

    @Override
    public void parseWeiboUrl(UserData data, Node dom, Component component, String... args) {
        super.parseWeiboUrl(data, dom, component);
        String url = data.getWeiboUrl();
        if (url != null && url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        data.setWeiboUrl(url);
    }

    @Override
    public void parseRelationSex(List<UserData> list, Node domtree, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getTextContent().endsWith("female")) list.get(i).setSex("女");
            else list.get(i).setSex("男");
        }
    }

    @Override
    public void parseRelationCertify(List<UserData> list, Node domtree, Component component, String... args) {
        for (int i = 0; i < list.size(); i++) {
            NodeList nl = commonList("//LI[" + (i + 1) + component.getXpath(), domtree);
            if (nl.item(0) != null) list.get(i).setCertify(nl.item(0).getTextContent());
        }
    }

    @Override
    public void parseWeiboImgUrl(List<WeiboData> list, Node domtree, Component component, String... args) {
        for (int i = 0; i < list.size(); i++) {
            NodeList nl = commonList("//DIV[@action-type][not(@feedType)][" + (i + 1) + component.getXpath(), domtree);
            String url = "";
            for (int k = 0; k < nl.getLength(); k++) {
                url += nl.item(k).getTextContent() + ";";
            }
            list.get(i).setImgUrl(url);
        }
    }

    @Override
    public void parseWeiboRttContent(List<WeiboData> list, Node domtree, Component component, String... args) {
        for (int i = 0; i < list.size(); i++) {
            String xpath = component.getXpath().replace("index", (i + 1) + "");
            NodeList nl = commonList(xpath, domtree);
            String url = "";
            for (int k = 0; k < nl.getLength(); k++) {
                url += nl.item(k).getTextContent();
            }
            if (!url.equals("")) list.get(i).setContent(list.get(i).getContent() + (url.equals("") ? "" : "转发\r\n" + url));
        }
    }

    private String nextPrefix;

    @Override
    public String parseNext(Node dom, Component component, String... args) {
        String currUrl = args[5];

        int curr = currUrl.contains("page=") ? Integer.parseInt(currUrl.substring(currUrl.indexOf("page=") + 5)) : 1;

        String nextUrl = currUrl.contains("page=") ? currUrl.replace("page=" + curr, "page=" + (curr + 1)) : currUrl + "?page=2";

        return nextUrl;
    }

    @Override
    public void parseWeiboCommentUrl(List<WeiboData> list, Node dom, Component component, String... args) {
        for (int i = 0; i < list.size(); i++) {
//            list.get(i).setCommentUrl("http://weibo.com/aj/comment/big?id=" + list.get(i).getMid());
            if (list.get(i).getMid() != null) {
                list.get(i).setCommentUrl("http://m.weibo.cn/single/rcList?format=cards&id=" + list.get(i).getMid() + "&type=comment&hot=0&page=1");
            }
        }
    }

    @Override
    public void parseWeiboRttUrl(List<WeiboData> list, Node dom, Component component, String... args) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRttUrl("http://weibo.com/aj/mblog/info/big?id=" + list.get(i).getMid());
        }
    }

    @Override
    public void parseCommentUid(List<WeiboData> list, Node domtree, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setUid(nl.item(i).getTextContent().replace("/", ""));
        }
    }

    @Override
    public void parseRttUid(List<WeiboData> list, Node domtree, Component component, String... args) {
        parseCommentUid(list, domtree, component);
    }

    @Override
    public void parseWeiboPubtime(List<WeiboData> list, Node domtree, Component component, String... args) {
        timeCommon(list, domtree, component);
    }

    @Override
    public void parseCommentTime(List<WeiboData> list, Node domtree, Component component, String... args) {
        timeCommon(list, domtree, component);
    }

    @Override
    public void parseRttTime(List<WeiboData> list, Node domtree, Component component, String... args) {
        timeCommon(list, domtree, component);
    }

    private void timeCommon(List<WeiboData> list, Node domtree, Component component) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setPubtime(nl.item(i).getTextContent());
            list.get(i).setPubdate(timeprocess(nl.item(i).getTextContent()));
        }
    }

    private Date timeprocess(String s) {
        Date d = super.timeProcess(s);
        if (d == null) {
            Calendar c = Calendar.getInstance();
            if (s.indexOf("月") > -1 || s.indexOf("日") > -1) {
                if (s.indexOf("年") == -1) {
                    s = c.get(Calendar.YEAR) + "-" + s;
                }
                s = s.replace("年", "-").replace("月", "-").replace("日", "");
                d = super.timeProcess(s);
            }
            if (d == null) {

                int num = Integer.parseInt(StringUtil.extractMulti(s, "\\d"));
                if (s.contains("minute") || s.contains("分钟前")) {
                    c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - num);
                } else if (s.contains("hour") || s.contains("小时前")) {
                    c.set(Calendar.HOUR, c.get(Calendar.HOUR) - num);
                } else if (s.contains("今天")) {
                    s.replace("今天", c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE) + "");
                } else if (s.contains("day") || s.contains("天前")) {
                    c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - num);
                } else if (s.contains("month") || s.contains("月前")) {
                    c.set(Calendar.MONTH, c.get(Calendar.MONTH) - num);
                } else if (s.contains("year") || s.contains("年前")) {
                    c.set(Calendar.YEAR, c.get(Calendar.YEAR) - num);
                } else if (s.contains("second") || s.contains("秒前")) {
                    c.set(Calendar.SECOND, c.get(Calendar.SECOND) - num);
                }
                return c.getTime();
            }
        }
        return d;
    }

}

package test;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Iterator;

public class testJson {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SimpleHttpProcess shp = new SimpleHttpProcess();

        HtmlInfo html = new HtmlInfo();
        html.setType("DATA");
        html.setEncode("gb2312");
        html.setCookie("t=1ca3b1e4428f58d23c1664711aff679e; cna=v0jXDKH/KBUCAZ/isbxlMFll; isg=E17C14E67ED31A91EFFFFCB86F090105; mt=ci=-1_0; uc3=nk2=B0Pu7nPGs6l4QKNM&id2=UUBaDgGeSbBc&vt3=F8dATSQJYaxf5F9cue8%3D&lg2=V32FPkk%2Fw0dUvg%3D%3D; lgc=darkslayer27; tracknick=darkslayer27; _cc_=WqG3DMC9EA%3D%3D; tg=0; x=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0%26__ll%3D-1%26_ato%3D0; unt=darkslayer27%26center; v=0; cookie2=1c7e23f502b36bbbda6c09eb8a9ac885; uc1=cookie14=UoW29wRnm91IBA%3D%3D; alitrackid=www.taobao.com; lastalitrackid=www.taobao.com; swfstore=222717");
        html.setOrignUrl("http://m.weibo.cn/single/rcList?format=cards&id=3945741208568191&type=comment&hot=0&page=1");
        String content = "";

        // content = StringUtil.getContent("filedown/a.json");
        shp.getContent(html);
        content = html.getContent();
//        content = content.substring(content.indexOf("[") + 1, content.lastIndexOf("]"));

        System.out.println(content);
//        content = StringUtil.regMatcher(content, "g_page_config = ", "};") + "}";
        JSONArray root = JSONArray.fromObject(content);
        JSONArray cardGroup = (JSONArray) root.getJSONObject(1).get("card_group");
        for (int i = 0; i < cardGroup.size(); i++) {
            JSONObject comment = cardGroup.getJSONObject(i);
            System.out.println(cardGroup.getJSONObject(i));
            String commentId = comment.getLong("id") + "";
            String commentPubtime = comment.getString("created_at");
            String commentSource = comment.getString("source");
            JSONObject commentUser = comment.getJSONObject("user");
            {
                String commentUserId = commentUser.getLong("id") + "";
                String commentUserName = commentUser.getString("screen_name");
                String commentUserImg = commentUser.getString("profile_image_url");
                String commentUserUrl = "http://weibo.com" + commentUser.getString("profile_url");
            }
            String commentContent = comment.getString("text");
            String commentLikeCount = comment.getInt("like_counts") + "";
            String commentUrl = comment.getString("url");

            System.out.println(commentUrl);
        }


    }

    public static void decodeJSONObject(JSONObject json) {
        Iterator<String> keys = json.keys();
        JSONObject jo = null;
        Object o;
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            o = json.get(key);
            if (o instanceof JSONObject) {
                jo = (JSONObject) o;
                if (jo.keySet().size() > 0) {
                    decodeJSONObject(jo);
                } else {
                    System.out.println(key);
                }
            } else {
                System.out.println(o);
            }
        }
    }
}

package common.extractor.xpath.news.search.sub;

import common.extractor.xpath.news.search.NewsSearchXpathExtractor;
import common.siteinfo.Component;
import org.w3c.dom.Node;

/**
 * 百度新闻搜索特殊属性抽取
 *
 * @author grs
 */
public class QingdaonizaoExtractor extends NewsSearchXpathExtractor {

    @Override
    public String parseNext(Node dom, Component component, String... args) {
        String currUrl = args[0];
        String currPage = currUrl.substring(currUrl.lastIndexOf("&page=") + 6);
        int curr = Integer.parseInt(currPage);

        String nextUrl = currUrl.replace("&page=" + currPage, "&page=" + (curr + 1));

        return nextUrl;
    }


}

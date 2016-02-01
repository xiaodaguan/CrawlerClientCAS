package common.extractor.xpath.company.monitor.sub;

import common.extractor.xpath.company.monitor.CompanyMonitorXpathExtractor;
import common.siteinfo.Component;
import org.w3c.dom.Node;

/**
 * Created by guanxiaoda on 2/1/16.
 */
public class PedailyExtractor extends CompanyMonitorXpathExtractor {


    @Override
    public String parseNext(Node domtree, Component component, String... args) {

        String currUrl=args[0];
        String currPage=currUrl.substring(currUrl.lastIndexOf("action-post/")+12);
        int currPageNum=Integer.parseInt(currPage);
        String nextUrl=currUrl.replace("action-post/"+currPageNum,"action-post/"+(currPageNum+1));

        return nextUrl;

    }

}

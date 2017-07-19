package common.extractor;

import common.extractor.xpath.XpathExtractor;
import common.pojos.HtmlInfo;
import common.system.Systemconfig;
import common.utils.StringUtil;

/**
 * Created by guanxiaoda on 2017/7/18.
 */
public class ExtractorHelper {
    public static XpathExtractor createExtractor(HtmlInfo task, String mediaTypeFull, String mediaTypePrefix){
        XpathExtractor extractor =null;

                String siteFlag = task.getSite();//e.g., "baidu"
        String extractorFlag = siteFlag+"_"+mediaTypeFull;
        String extractorPath = Systemconfig.siteExtractClass.get(extractorFlag);
        if(extractorPath == null)
            extractorPath = "common.extractor.xpath."+mediaTypeFull.replace("_",".")+"."+ StringUtil.upperFirstLetter(mediaTypePrefix)+ (Systemconfig.crawlerType%2==1?"Search":"Monitor")+"XpathExtractor";
        else
            extractorPath = extractorPath.replace("/",".");
        try {
            extractor = (XpathExtractor) Class.forName(extractorPath).newInstance();

            return extractor;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}

package common.extractor.xpath.company.monitor;


import common.pojos.CompanyData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;
import org.w3c.dom.Node;

import java.util.List;

public interface CompanyMonitorExtractorAttribute extends ExtractorAttribute<CompanyData> {

    /**
     * 内容解析
     *
     * @param list
     * @param dom
     * @param component
     * @param args
     */
    public void parseContent(CompanyData cd, Node dom, Component component, String... args);

    /**
     * 发布时间列表解析
     *
     * @param list
     * @param dom
     * @param component
     * @param args
     */
    public void parsePubtime(List<CompanyData> list, Node dom, Component component, String... args);

    /**
     * 内容页发布时间解析
     *
     * @param list
     * @param dom
     * @param component
     * @param args
     */
    public void parsePubtime(CompanyData cd, Node dom, Component component, String... args);
}

package common.service.oracle;

import common.service.DBFactory;
import common.service.DBService;
import common.system.Systemconfig;

public class UnitOracleService implements DBFactory {

    private NewsOracleService newsOracleService;
    private BbsOracleService bbsOracleService;
    private BlogOracleService blogOracleService;
    private WeiboOracleService weiboOracleService;
    private EbusinessOracleService ebusinessOracleService;
    private WeixinOracleService weixinOracleService;
    private ReportOracleService reportOracleService;
    private AgricaltureOracleService agricaltureOracleService;
    private ConferenceOracleService conferenceOracleService;
    private PersonOracleService personOracleService;
    private CompanyOracleService companyOracleService;

    public DBService dbService() {
        switch (Systemconfig.crawlerType) {
            case 1:
            case 2:
                return newsOracleService;
            case 3:
            case 4:
                return bbsOracleService;
            case 5:
            case 6:
                return blogOracleService;
            case 7:
            case 8:
                return weiboOracleService;
            case 9:
            case 10:
                return null;
            case 11:
            case 12:
                return null;
            case 13:
            case 14:
                return ebusinessOracleService;
            case 15:
            case 16:
                return weixinOracleService;
            case 21:
            case 22:
                return reportOracleService;

            case 27:
            case 28:
                return agricaltureOracleService;
            case 29:
            case 30:
                return conferenceOracleService;
            case 31:
            case 32:
                return personOracleService;
            case 33:
            case 34:
                return companyOracleService;
        }
        return null;
    }

    public void setPersonOracleService(PersonOracleService personOracleService) {

        this.personOracleService = personOracleService;
    }

    public void setAgricaltureOracleService(AgricaltureOracleService agricaltureOracleService) {
        this.agricaltureOracleService = agricaltureOracleService;
    }

    public void setNewsOracleService(NewsOracleService newsOracleService) {
        this.newsOracleService = newsOracleService;
    }

    public void setWeixinOracleService(WeixinOracleService weixinOracleService) {
        this.weixinOracleService = weixinOracleService;
    }

    public void setBbsOracleService(BbsOracleService bbsOracleService) {
        this.bbsOracleService = bbsOracleService;
    }

    public void setWeiboOracleService(WeiboOracleService weiboOracleService) {
        this.weiboOracleService = weiboOracleService;
    }

    public void setBlogOracleService(BlogOracleService blogOracleService) {
        this.blogOracleService = blogOracleService;
    }

    public void setEbusinessOracleService(EbusinessOracleService ebusinessOracleService) {
        this.ebusinessOracleService = ebusinessOracleService;
    }

    public void setReportOracleService(ReportOracleService reportOracleService) {
        this.reportOracleService = reportOracleService;
    }

    public void setConferenceOracleService(ConferenceOracleService conferenceOracleService) {
        this.conferenceOracleService = conferenceOracleService;
    }

    public void setCompanyOracleService(CompanyOracleService companyOracleService) {
        this.companyOracleService = companyOracleService;
    }
}

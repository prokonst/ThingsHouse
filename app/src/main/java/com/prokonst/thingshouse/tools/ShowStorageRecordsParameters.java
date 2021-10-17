package com.prokonst.thingshouse.tools;

import com.prokonst.thingshouse.model.tables.Thing;

import java.io.Serializable;

public class ShowStorageRecordsParameters  implements Serializable {
    private Thing sourceThing;
    private ReportType reportType;

    public ShowStorageRecordsParameters(Thing sourceThing, ReportType reportType) {
        this.sourceThing = sourceThing;
        this.reportType = reportType;
    }

    public Thing getSourceThing() {
        return sourceThing;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public enum ReportType implements Serializable {
        WhereUsed,
        SelfItems
    }

}

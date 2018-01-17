package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 26/07/17.
 */
@Root(name = "events")
public class CaseEvents {

    @Attribute(name = "ixBugEvent", required = false)
    String count;

    @Attribute(name = "ixBug", required = false)
    String totalHits;

    @ElementList(inline = true , required = false)
    List<CaseEvent> caseEvents;

    public List<CaseEvent> getCaseEvents() {
        return caseEvents;
    }

    public void setCaseEvents(List<CaseEvent> caseEvents) {
        this.caseEvents = caseEvents;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(String totalHits) {
        this.totalHits = totalHits;
    }
}

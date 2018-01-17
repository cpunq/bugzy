package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 19/06/17.
 */
@Root(name = "cases")
public class Cases{


    @ElementList(inline = true,required = false)
    private List<Case> cases;

    @Attribute(name = "count", required = false)
    String count;

    @Attribute(name = "totalHits", required = false)
    String totalHits;



    public List<Case> getCases() {
        return cases;
    }

    public String getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(String totalHits) {
        this.totalHits = totalHits;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }



    public void setCases(List<Case> cases) {
        this.cases = cases;
    }
}

package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 06/06/17.
 */
@Root(name = "response")
public class ListCasesResponse{


    @Element(name= "cases",required = false)
    private Cases cases;

    @Element(name = "description", required = false)
    private String description;

    public List<Case> getCases() {
        return cases.getCases();
    }

    public String getTotalHits() {
        return cases.getCount();
    }

    public void setTotalHits(String totalHits) {
        this.cases.totalHits = totalHits;
    }

    public String getCount() {
        return this.cases.count;
    }

    public void setCount(String count) {
        this.cases.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCases(Cases cases) {
        this.cases = cases;
    }
}


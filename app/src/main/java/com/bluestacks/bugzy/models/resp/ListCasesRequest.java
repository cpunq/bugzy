package com.bluestacks.bugzy.models.resp;

import com.bluestacks.bugzy.models.Request;

public class ListCasesRequest extends Request {
    String[] cols;

    public ListCasesRequest(String[] cols) {
        super("listCases");
        this.cols = cols;
    }

    public String[] getCols() {
        return cols;
    }

    public void setCols(String[] cols) {
        this.cols = cols;
    }
}

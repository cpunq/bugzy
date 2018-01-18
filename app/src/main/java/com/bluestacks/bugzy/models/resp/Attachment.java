package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by msharma on 27/07/17.
 */
@Root(name = "attachment")
public class Attachment {
    @Element(name = "sFileName",required = false)
    String filename;

    @Element(name = "sURL", required = false)
    String url;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

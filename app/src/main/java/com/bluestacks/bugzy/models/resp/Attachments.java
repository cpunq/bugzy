package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 27/07/17.
 */
@Root(name = "rgAttachments")
public class Attachments {

    @ElementList(inline = true , required = false)
    List<Attachment> attachments;

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachment) {
        this.attachments = attachment;
    }
}

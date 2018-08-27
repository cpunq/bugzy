package in.bugzy.utils;



import android.support.annotation.Nullable;

import in.bugzy.data.model.Attachment;

public class BugzyUrlGenerator {
    private String mOrganisationName;
    private int mPersonId;
    private String mToken;

    public BugzyUrlGenerator(String organisationName, int personId, String token) {
        mOrganisationName = organisationName;
        mPersonId = personId;
        mToken = token;
    }

    public void setOrganisationName(String organisationName) {
        mOrganisationName = organisationName;
    }

    public void setPersonId(@Nullable int personId) {
        mPersonId = personId;
    }

    public String getAttachmentUrl(Attachment attachment) {
        return String.format("https://%s.manuscript.com/%s&token=%s",
                mOrganisationName,
                attachment.getUrl(),
                mToken)
                .replaceAll("&amp;","&");
    }

    public String getPersonImageUrl(int personId) {
        return String.format("https://%s.manuscript.com/default.asp?ixPerson=%d&pg=pgAvatar&pxSize=60",
                mOrganisationName,
                personId);
    }

    public void setToken(String token) {
        mToken = token;
    }
}

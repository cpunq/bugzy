package in.bugzy.data.model;

import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(primaryKeys = "id")
public class SearchSuggestion {
    // Id is used to uniquely identify this suggestion
    @NonNull
    private String id;
    // Text is actual text which goes into the search query
    private String text;
    private String type;

    public SearchSuggestion(String id, String text, String type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
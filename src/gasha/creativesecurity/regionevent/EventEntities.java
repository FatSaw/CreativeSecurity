package gasha.creativesecurity.regionevent;

import java.util.ArrayList;
import java.util.List;

public class EventEntities {
    private List<String> enter = new ArrayList<String>();
    private List<String> leave = new ArrayList<String>();
    private String type;
    private String name;
    private String replacement;
    private boolean contains = false;
    private boolean cased = false;
    private boolean cancel = false;
    private boolean caseInsensitive = true;

    public EventEntities(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public List<String> getEnter() {
        return this.enter;
    }

    public List<String> getLeave() {
        return this.leave;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setReplacement(String value) {
        this.replacement = value;
    }

    public String getReplacement() {
        return this.replacement;
    }

    public void setEnter(List<String> enter) {
        this.enter = enter;
    }

    public void setLeave(List<String> leave) {
        this.leave = leave;
    }

    public void setContains(boolean contains) {
        this.contains = contains;
    }

    public boolean isContains() {
        return this.contains;
    }

    public boolean isCased() {
        return this.cased;
    }

    public void setCased(boolean cased) {
        this.cased = cased;
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
}


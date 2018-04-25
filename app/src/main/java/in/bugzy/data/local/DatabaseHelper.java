package in.bugzy.data.local;


import in.bugzy.data.model.Case;
import in.bugzy.data.model.Person;


import java.util.List;

public interface DatabaseHelper {
    void setCases(List<Case> filterList, String sFilter);
    List<Case> getCases(String sFilter);

    void setPeople(List<Person> people);
    List<Person> getPeople();

}

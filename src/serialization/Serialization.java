package serialization;

import javafx.collections.ObservableList;
import tsubulko.entity.Person;

import java.io.IOException;

public interface Serialization {
    public void serialise(ObservableList<Person> data,String name) throws IOException;
    public ObservableList<Person> deserialise(String name,ObservableList<Person> now) throws IOException, ClassNotFoundException, Exception;

}

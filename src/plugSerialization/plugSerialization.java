package plugSerialization;

import javafx.collections.ObservableList;
import tsubulko.entity.Person;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface plugSerialization {
    public void serialise(ObservableList<Person> data, String name,Class cryptoClass, Method encryptionMethod) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException;
    public ObservableList<Person> deserialise (String name,ObservableList<Person> now) throws IOException, ClassNotFoundException, Exception;
}

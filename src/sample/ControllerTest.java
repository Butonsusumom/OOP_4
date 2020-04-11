package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import junit.framework.TestCase;

import org.junit.jupiter.api.Test;
import plugSerialization.plagJsonConverter;
import plugSerialization.plagTextConverter;
import serialization.BinarConverter;
import serialization.JsonConverter;
import serialization.TextConverter;
import tsubulko.entity.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ControllerTest extends TestCase {

    Controller controller= new Controller();

    @Test
    public void testGetPerson()  {
        assertNull("Shouldn't be found",controller.getPerson(-1));

    }

    @Test
    public void testDeletePerson()  {
        controller.personData.add(new Student(0, new Birth(14, Birth.Month.OCT,2000), "Ksenia", "Tsubulko", Person.Sex.Female, "tsubulko.ksenia@gmail.com", new Adress("Minsk", "Perehyadnaya", 12), Person.Position.Student, "BSUIR", "KSIS", "SOIT", 2, "851002"));
        assertTrue( controller.deletePerson(0));
    }

    @Test
    public void testFillPersonData()  {
        controller.fillPersonData();
        int count=0;
        for (Person person :
                controller.personData) {
          count++;
        }
        assertEquals("Not equals",5,count);
    }

//------------------------------------------------- SERIALIZATION--------------------------------
    @Test
    public void testJsonSerialization() throws IOException {
        ObservableList<Person> result = FXCollections.observableArrayList();
        Manager man=new Manager(1, new Birth(18, Birth.Month.DEC, 1973), "Dmitry", "Tsubulko", Person.Sex.Male, "TiMiQ@gmail.com", new Adress("Minsk", "Soltisa", 14), Person.Position.Manager, 6000, 20, Employee.Education.Hight, 1000);
        controller.personData.add(man);
        JsonConverter conv = new JsonConverter();
        conv.serialise(controller.personData,"persondata.json");
        result = conv.deserialise("persondata.json",controller.personData);
        assertEquals("Not equals",result,controller.personData);
    }

    @Test
    public void testBinarSerialization() throws Exception {
        ObservableList<Person> result = FXCollections.observableArrayList();
        Manager man = new Manager(1, new Birth(18, Birth.Month.DEC, 1973), "Dmitry", "Tsubulko", Person.Sex.Male, "TiMiQ@gmail.com", new Adress("Minsk", "Soltisa", 14), Person.Position.Manager, 6000, 20, Employee.Education.Hight, 1000);
        controller.personData.add(man);
        BinarConverter conv = new BinarConverter();
        conv.serialise(controller.personData, "binaryuser.dat");
        result = conv.deserialise("binaryuser.dat",controller.personData);
        assertEquals("Not equals", result, controller.personData);
    }

    @Test
    public void testTextSerialization() throws Exception {
        ObservableList<Person> result = FXCollections.observableArrayList();
        Manager man = new Manager(1, new Birth(18, Birth.Month.DEC, 1973), "Dmitry", "Tsubulko", Person.Sex.Male, "TiMiQ@gmail.com", new Adress("Minsk", "Soltisa", 14), Person.Position.Manager, 6000, 20, Employee.Education.Hight, 1000);
        controller.personData.add(man);
        TextConverter conv = new TextConverter();
        conv.serialise(controller.personData, "textpers.csv");
        result = conv.deserialise("textpers.csv",controller.personData);
        assertEquals("Not equals", result, controller.personData);
    }
    //------------------------------------------------- PLUGINS--------------------------------
    @Test
    public void test32() throws Exception {
        ObservableList<Person> result = FXCollections.observableArrayList();
        controller.fillPersonData();
        plagTextConverter conv = new plagTextConverter();
        Class cryptoClass=Class.forName("plugins.CBase64");
        Method encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        conv.serialise(controller.personData, "C64.CBase64.txt",cryptoClass,encryptionMethod);
        String fileName = "C64.CBase64.txt";
        String f1 = Files.lines(Paths.get(fileName)).reduce("", String::concat);
         cryptoClass=Class.forName("plugins.JBase64");
         encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        conv.serialise(controller.personData, "J64.JBase64.txt",cryptoClass,encryptionMethod);
        fileName = "J64.JBase64.txt";
        String f2 = Files.lines(Paths.get(fileName)).reduce("", String::concat);
        assertEquals("Not equals", f1, f2);
    }

    @Test
    public void test64() throws Exception {
        ObservableList<Person> result = FXCollections.observableArrayList();
        controller.fillPersonData();
        plagTextConverter conv = new plagTextConverter();
        Class cryptoClass=Class.forName("plugins.CBase32");
        Method encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        conv.serialise(controller.personData, "C32.CBase32.txt",cryptoClass,encryptionMethod);
        String fileName = "C32.CBase32.txt";
        String f1 = Files.lines(Paths.get(fileName)).reduce("", String::concat);
        cryptoClass=Class.forName("plugins.JBase32");
        encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        conv.serialise(controller.personData, "J32.JBase32.txt",cryptoClass,encryptionMethod);
        fileName = "J32.JBase32.txt";
        String f2 = Files.lines(Paths.get(fileName)).reduce("", String::concat);
        assertEquals("Not equals", f1, f2);
    }

    @Test
    public void testPlug32Serialization() throws Exception {
        ObservableList<Person> result = FXCollections.observableArrayList();
        Manager man=new Manager(1, new Birth(18, Birth.Month.DEC, 1973), "Dmitry", "Tsubulko", Person.Sex.Male, "TiMiQ@gmail.com", new Adress("Minsk", "Soltisa", 14), Person.Position.Manager, 6000, 20, Employee.Education.Hight, 1000);
        controller.personData.add(man);
        plagJsonConverter conv = new plagJsonConverter();
        Class cryptoClass=Class.forName("plugins.CBase32");
        Method encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        conv.serialise(controller.personData, "C32.CBase32.txt",cryptoClass,encryptionMethod);
        result = conv.deserialise("C32.CBase32.txt",controller.personData);
        assertEquals("Not equals",result,controller.personData);
    }

    @Test
    public void testPlug64Serialization() throws Exception {
        ObservableList<Person> result = FXCollections.observableArrayList();
        Manager man=new Manager(1, new Birth(18, Birth.Month.DEC, 1973), "Dmitry", "Tsubulko", Person.Sex.Male, "TiMiQ@gmail.com", new Adress("Minsk", "Soltisa", 14), Person.Position.Manager, 6000, 20, Employee.Education.Hight, 1000);
        controller.personData.add(man);
        plagJsonConverter conv = new plagJsonConverter();
        Class cryptoClass=Class.forName("plugins.CBase64");
        Method encryptionMethod = cryptoClass.getDeclaredMethod("encode", byte[].class);
        conv.serialise(controller.personData, "C64.CBase64.txt",cryptoClass,encryptionMethod);
        result = conv.deserialise("C64.CBase64.txt",controller.personData);
        assertEquals("Not equals",result,controller.personData);
    }

}
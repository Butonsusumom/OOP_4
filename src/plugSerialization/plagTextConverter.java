package plugSerialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import tsubulko.entity.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class plagTextConverter implements plugSerialization {
    @Override
    public void serialise(ObservableList<Person> data, String name, Class cryptoClass, Method encryptionMethod) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        String NEW_LINE_SEPARATOR = "\n";
        String s="";

        for (Person person: data) {
            s+=person.write();
            s+=NEW_LINE_SEPARATOR;
        }
        System.out.println(s);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();


        String encryptedObject = (String) encryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), s.getBytes());

        FileOutputStream fileOutputStream = new FileOutputStream(new File(name));
        fileOutputStream.write(encryptedObject.getBytes());
        fileOutputStream.close();
    }

    @Override
    public ObservableList<Person> deserialise(String name,ObservableList<Person> now) throws IOException, ClassNotFoundException, Exception {
        try {
            ObservableList<Person> newList =  FXCollections.observableArrayList();
            String line = "";
            String COMMA_DELIMITER = ",";
            BufferedReader fileReader = new BufferedReader(new FileReader(name));
            String l;
            while ((l = fileReader.readLine()) != null) {
                line+=l;
            }

            String[] t = name.split("[.]");
            String str = "plugins."+t[t.length-2];
            Class cryptoClass =Class.forName(str);
            Method decryptionMethod = cryptoClass.getDeclaredMethod("decode", String.class);
            byte[] objectBytes = (byte[]) decryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), line);
            String[] tok = new String(objectBytes).split("\n");

           for (int i=0;i<tok.length;i++) {
                String[] tokens = tok[i].split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                    switch (tokens[11]) {
                        case ("Manager"):
                            newList.add(Manager.read(tokens));
                            break;
                        case ("Programmer"):
                            newList.add(Programmer.read(tokens));
                            break;
                        case ("Tester"):
                            newList.add(Tester.read(tokens));
                            break;
                        case ("Designer"):
                            newList.add(Designer.read(tokens));
                            break;
                        case ("Student"):
                            newList.add(Student.read(tokens));
                            break;
                    }
                    // System.out.println(pers);
                }
            }
            System.out.println(newList);
            fileReader.close();
            return newList;
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid binar format");

            alert.showAndWait();
            return now;
        }
    }

}

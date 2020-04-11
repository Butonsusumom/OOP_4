package plugSerialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import tsubulko.entity.Person;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

public class plagJsonConverter implements plugSerialization {

    @Override
    public void serialise(ObservableList<Person> data, String filePath, Class cryptoClass,Method encryptionMethod) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
        String jsonuser = objectMapper.writeValueAsString(data);

        String encryptedObject = (String) encryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), jsonuser.getBytes());

        FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
        fileOutputStream.write(encryptedObject.getBytes());
        fileOutputStream.close();
    }


    @Override
    public ObservableList<Person> deserialise (String name,ObservableList<Person> now) throws IOException, ClassNotFoundException, Exception {
        StringBuilder restoredObject = new StringBuilder();
        Scanner in = new Scanner(new File(name));
        while(in.hasNext()) {
            restoredObject.append(in.nextLine());
        }
        in.close();

        String[] tokens = name.split("[.]");
        String str = "plugins."+tokens[tokens.length-2];
        Class cryptoClass =Class.forName(str);
        Method decryptionMethod = cryptoClass.getDeclaredMethod("decode", String.class);
        byte[] objectBytes = (byte[]) decryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), restoredObject.toString());
        try{
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class,Person.class);
        String jsonuser=new String(objectBytes);
        ArrayList<Person> arrayListResult = mapper.readValue(jsonuser, type);

        ObservableList<Person> result = FXCollections.observableArrayList();
        result.setAll(arrayListResult);
        System.out.println(result);
        return result;
    }
        catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid json format");

        alert.showAndWait();
        return now;
    }
    }
}

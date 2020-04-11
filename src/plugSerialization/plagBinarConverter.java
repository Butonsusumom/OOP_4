package plugSerialization;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import tsubulko.entity.Person;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class plagBinarConverter implements plugSerialization {
    @Override
    public void serialise(ObservableList<Person> data, String filePath, Class cryptoClass, Method encryptionMethod) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       // ObjectOutputStream objectOutputStream = null;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(new ArrayList<>(data));

            byte[] objectBytes = byteArrayOutputStream.toByteArray();

            String encryptedObject = (String) encryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), objectBytes);

            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(encryptedObject.getBytes());
            fileOutputStream.close();
        }
        finally {
            try {
                byteArrayOutputStream.close();
            }
            catch (IOException ignored) { }
        }
    }

    @Override
    public ObservableList<Person> deserialise(String name,ObservableList<Person> now) throws IOException, ClassNotFoundException, Exception {
        try {
            StringBuilder objectString = new StringBuilder();
            String line;
            try {
                FileInputStream fileInputStream = new FileInputStream(name);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "cp1251"));
                while ((line = bufferedReader.readLine()) != null) {
                    objectString.append(line);
                }
                bufferedReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            String[] tokens = name.split("[.]");
            String str = "plugins."+tokens[tokens.length-2];
            Class cryptoClass =Class.forName(str);
            Method decryptionMethod = cryptoClass.getDeclaredMethod("decode", String.class);
            byte[] objectBytes = (byte[]) decryptionMethod.invoke(cryptoClass.getDeclaredConstructor().newInstance(), objectString.toString());

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBytes);
            ObjectInput objectInput = null;
            Object object = null;
            objectInput = new ObjectInputStream(byteArrayInputStream);
            object = objectInput.readObject();

            ObservableList<Person> result = FXCollections.observableArrayList();
            result.setAll((ArrayList<Person>) object);

            return result;
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

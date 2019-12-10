package gobbler.services;

import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class NewGobblerService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PictureRepository pictureRepository;

    public List<String> newGobbler(String gobblerName, String name, String password, String password2, String gobblerPath) throws IOException {
        List<String> errors = new ArrayList<>();

        List<String> gobblerNameErrors = checkGobblerName(gobblerName);
        List<String> nameErrors = checkName(name);
        List<String> passwordErrors = checkPasswords(password, password2);
        List<String> gobblerPathErrors = checkPath(gobblerPath);

        errors.addAll(gobblerNameErrors);
        errors.addAll(nameErrors);
        errors.addAll(passwordErrors);
        errors.addAll(gobblerPathErrors);

        if (errors.isEmpty()) {
            Gobbler gobbler = new Gobbler();
            gobbler.setGobblerName(gobblerName);
            gobbler.setName(name);
            gobbler.setPassword(passwordEncoder.encode(password));
            gobbler.setGobblerPath(gobblerPath.toLowerCase());

            Picture picture = new Picture();
            picture.setInfo("Default gobbler picture.");

            String workingDirectory = System.getProperty("user.dir");
            System.out.println(workingDirectory);

            File file = new File("./src/main/resources/public/defaultPic.png");

            byte[] fileContent = Files.readAllBytes(file.toPath());
            picture.setContent(fileContent);

            pictureRepository.save(picture);

            gobbler.addPicture(picture);
            gobbler.setProfilePicture(picture);

            gobblerRepository.save(gobbler);
        }

        return errors;
    }

    private String containsSpecialCharacters(String string) {
        if (string == null) {
            return "";
        }

        String specChars = "";

        for (int i = 0; i < string.length(); i++) {
            String character = string.substring(i, i + 1);
            if (character.matches("[^a-zA-Z0-9 ]")) {
                specChars = specChars + character;
            }
        }
        return specChars;
    }

    private List<String> checkGobblerName(String gobblerName) {
        List<String> errors = new ArrayList<>();

        if (gobblerName == null || gobblerName.trim().equals("")) {
            errors.add("Please enter a Gobbler name which contains between 5 and 15 characters.");
            return errors;
        }
        if (gobblerName.contains("  ")) {
            errors.add("Gobbler name must not contain two white spaces in a row.");
        }

        if (gobblerName.substring(0, 1).equals(" ")
                || gobblerName.substring(gobblerName.length() - 1, gobblerName.length()).equals(" ")) {
            errors.add("Gobbler name must not begin or end with a white space.");
        }
        if (gobblerName.length() < 5 || gobblerName.length() > 15) {
            errors.add("Gobbler name must be between 5 and 15 characters long.");
        }
        String specialCharacter = containsSpecialCharacters(gobblerName);
        if (!specialCharacter.equals("")) {
            errors.add("Gobbler name must not contain any special character like '" + specialCharacter + "'.");
        }
        if (gobblerRepository.findByGobblerName(gobblerName) != null) {
            errors.add("Gobbler name is already taken by a fellow Gobbler.");
        }

        return errors;
    }

    private List<String> checkName(String name) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().equals("")) {
            errors.add("Name must contain atleast 10 characters and not white space in the begging or the end.");
            return errors;
        }
        if (name.contains("  ")) {
            errors.add("Name must not contain two white spaces in a row.");
        }
        if (name.substring(0, 1).equals(" ")
                || name.substring(name.length() - 1, name.length()).equals(" ")) {
            errors.add("Name must not contain two white spaces in a row.");
        }
        if (name.length() < 10 || name.length() > 25) {
            errors.add("Name must be between 10 and 15 characters long.");
        }
        String specialCharacter = containsSpecialCharacters(name);
        if (!specialCharacter.equals("")) {
            errors.add("Name must not contain any special character like '" + specialCharacter + "'.");
        }
        return errors;
    }

    private List<String> checkPasswords(String password, String password2) {
        List<String> errors = new ArrayList<>();

        if (password == null || password2 == null) {
            errors.add("Please add your two matching passwords.");
            return errors;
        }
        if (!password.equals(password2)) {
            errors.add("Passwords did not match.");
        }
        if (password.length() < 8 || password.length() > 30) {
            errors.add("Password must be between 8 and 30 characters long");
        }
        return errors;
    }

    private List<String> checkPath(String gobblerPath) {
        List<String> errors = new ArrayList<>();

        if (gobblerPath == null || gobblerPath.trim().equals("")) {
            errors.add("Gobbler path must contain atleast 1 character and not special characters or white spaces.");
            return errors;
        }
        if (!gobblerPath.toString().equals(gobblerPath.toLowerCase().toString())) {
            errors.add("Gobbler path must be in lower case.");
        }
        if (gobblerPath.contains(" ")) {
            errors.add("Gobbler path must not contain any white space.");
        }
        if (gobblerPath.length() < 5 || gobblerPath.length() > 15) {
            errors.add("Gobbler path must be between 5 and 15 characters long.");
        }
        if (gobblerRepository.findByGobblerPath(gobblerPath) != null) {
            errors.add("Gobbler path is already taken by a fellow Gobbler.");
        }
        String specialCharacter = containsSpecialCharacters(gobblerPath);
        if (!specialCharacter.equals("")) {
            errors.add("Gobbler path must not contain any special characters like '" + specialCharacter + "'.");
        }
        return errors;
    }

}

package abn.recipe.core;

import org.apache.commons.text.RandomStringGenerator;

public class RandomStringUtil {

    public static String randomStringWithLength(int length) {
        return new RandomStringGenerator.Builder()
                .withinRange('0', 'z') // Includes numbers and letters
                .filteredBy(Character::isLetterOrDigit) // Removes special characters
                .get()
                .generate(length);
    }
}

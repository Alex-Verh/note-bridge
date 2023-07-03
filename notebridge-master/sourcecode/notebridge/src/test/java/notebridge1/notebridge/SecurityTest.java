package notebridge1.notebridge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecurityTest {

    @Test
    public void testValidatePassword(){
        Assertions.assertFalse(Security.validatePassword(""));
        Assertions.assertFalse(Security.validatePassword("12345678"));
        Assertions.assertFalse(Security.validatePassword("abcdefgh"));
        Assertions.assertFalse(Security.validatePassword("ABCDEFGH"));
        Assertions.assertFalse(Security.validatePassword("ABCDefgh"));
        Assertions.assertFalse(Security.validatePassword("ABCD1234"));
        Assertions.assertFalse(Security.validatePassword("abcd1234"));
        Assertions.assertFalse(Security.validatePassword("Aa5"));
        Assertions.assertFalse(Security.validatePassword("Password with 3 whitespaces"));

        Assertions.assertTrue(Security.validatePassword("Pass12"));
        Assertions.assertTrue(Security.validatePassword("12Pass"));
    }
    @Test
    public void testValidateEmail(){
        Assertions.assertFalse(Security.validateEmail(""));
        Assertions.assertFalse(Security.validateEmail("Just a random string"));
        Assertions.assertFalse(Security.validateEmail("my@email"));
        Assertions.assertFalse(Security.validateEmail("@email.com"));
        Assertions.assertFalse(Security.validateEmail("my@email.c"));
        Assertions.assertFalse(Security.validateEmail("my@email.c123"));
        Assertions.assertFalse(Security.validateEmail("my@email.com.c"));
        Assertions.assertFalse(Security.validateEmail("my@email.com.c123"));
        Assertions.assertFalse(Security.validateEmail("my space @email.c123"));

        Assertions.assertTrue(Security.validateEmail("My@email.com"));
        Assertions.assertTrue(Security.validateEmail("my123@email.com"));
        Assertions.assertTrue(Security.validateEmail("my123@gmail123.com"));
        Assertions.assertTrue(Security.validateEmail("my123@gmail123.eu.uk"));
    }

    @Test
    public void testValidateName(){
        Assertions.assertFalse(Security.validateName(".Name"));
        Assertions.assertFalse(Security.validateName("!Name"));

        Assertions.assertTrue(Security.validateName("First Last"));
        Assertions.assertTrue(Security.validateName("First van Last"));
        Assertions.assertTrue(Security.validateName("First Second Last"));
        Assertions.assertTrue(Security.validateName("F-S Last"));
        Assertions.assertTrue(Security.validateName("first 132last"));
    }

    @Test
    public void testRemoveTags(){
        Assertions.assertEquals(Security.removeTags("<> Heyo"), "<> Heyo");
        Assertions.assertEquals(Security.removeTags("<p> Heyo <p>"), "Heyo");
        Assertions.assertEquals(Security.removeTags("<p> Heyo with more words! <p>"), "Heyo with more words!");
        Assertions.assertEquals(Security.removeTags("<script>console.log()</script>"), "");
        Assertions.assertEquals(Security.removeTags("<img src=\"img_girl.jpg\">IMG"), "IMG");


    }
}

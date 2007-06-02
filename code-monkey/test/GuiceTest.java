import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * On: May 14, 2007 9:40:09 AM
 *
 * @author Dhanji R. Prasanna
 */
public class GuiceTest {
    @Inject private GuiceTest test;

    public GuiceTest() { }

    public static void main(String...args) {
        Guice.createInjector().getInstance(GuiceTest.class);
    }

    private static void print(GuiceTest instance) {
        if (instance.test != null) {
            System.out.println(instance.test);
            print(instance.test);
        }

        throw new AssertionError();
    }
}

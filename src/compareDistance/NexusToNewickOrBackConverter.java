package compareDistance;


/**
 * Created by Alexey Markin on 02/08/2017.
 *
 * to have a jar file
 */
public class NexusToNewickOrBackConverter {
    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }

        try {
            IOUtils.nexusToNewickOrBack(args[0], args[1]);
        } catch (Exception e) {
            System.out.println("Conversion failed. " + e.toString());
        }
    }
}

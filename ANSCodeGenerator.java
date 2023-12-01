import java.io.IOException;
import java.util.Scanner;
import java.lang.StringBuilder; // Marginally more efficient and convenient than constant string concatonation
import java.io.FileWriter;
public class ANSCodeGenerator {
    /* Prints out basic Java code for I2 and E3 messages from the given examples
     * Printouts will include the encode and decode methods from the respective class files provided
     * Printouts of different instances of the same type of message will only vary by the message text contents
     * Because those are the only differences between messages of the same type, pre-filled templates are
     * extremely efficient.
     */

    public static String generate (final String[] tokens) {
        final StringBuilder printOut = new StringBuilder();
        // dependencies
        printOut.append("import net.ddp2p.ASN1.*;");
        // instantiate
        printOut.append(String.format("public class %s extends ASNObjArrayable {\n", tokens[0]));
        // define the Application 0 byte ahead of time. The CC2 tag is unused in both messages, so I will omit it for brevity.
        // AP0 is defined the same for either. I2Message uses the constant Encoder.PC_PRIMITIVE, which =s 0, instead of just writing 0 like E3 does
        printOut.append("final static byte TAG_AP0 = Encoder.buildASN1byteType(Encoder.CLASS_APPLICATION,0,(byte)0);");
        // version/value is not instantiated in the example input ANS1. While the var is called different things in both classes
        // the int is used similarly. Version is a better fitting name, resulting in cleaner generated code with fewer arbitrary changes
        printOut.append("int version;");
        // the second to last token is always the message
        printOut.append(String.format("String message = %s", tokens[tokens.length -2]));
        // The encoder is identical for both message types, so it will be used verbatim in the generated code.
        // And Silaghi says that filling in these methods with the given code "Would not be incorrect." and gave no other asterisks
        // Although I should double check if it's even necessary since the example code generator makes empty methods.
        // The following code snippets in multiline strings are sourced from Marius Silaghi under the GNU Affero GPL
        printOut.append("""
                public Encoder getEncoder() {
                		Encoder e = new Encoder().initSequence();
                		e.addToSequence(
                			new Encoder(value)
                			 .setASN1Type(TAG_AP0));
                		e.addToSequence(new Encoder(message));
                		return e.setASN1TypeImplicit(I2Message.class);
                }
                """);
        // I2 and E3 differ in their Decoder methods because E3 explicitly uses the var Version to check
        // if it can handle the given message at its current version.
        // Including this in the I2Message class will not adversely affect performance and again leads to cleaner code
        printOut.append("""
                public E3Message decode(Decoder dec) throws ASN1DecoderFail {
                   Decoder d = dec.getContentExplicit();
                   version = d.getFirstObject(true).getInteger(TAG_AP0).intValue();
                   if (version > 1) {System.out.println("May need to upgrade!");}
                   message = d.getFirstObject(true).getString();
                   if (d.getTypeByte() != 0) throw new ASN1DecoderFail("Extra objects!");
                   return this;
                  }""");
        printOut.append("}"); // end the class
        return printOut.toString();
    }

    public static void main (final String[] args) throws IOException {
        final Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            String test = input.nextLine();
            final String[] tokens = test.split(" ");
            if (tokens[0].equals("I2Message") || tokens[0].equals("E3Message")) {
                final String result = generate(tokens);
                FileWriter output = new FileWriter(String.format("Generated%s.java", tokens[0]));
                System.out.println(result);
                output.write(result);
                output.close();

            } else {
                // fail fast I guess, but there's no possibility in the given input space for this.
                System.out.println("Error: Unacceptable type: " + test);
                System.exit(1);
            }


        }
    }

}
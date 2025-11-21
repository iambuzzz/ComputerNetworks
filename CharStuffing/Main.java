package CN.CharStuffing;

public class Main {
    // Character Stuffing (DLE STX ... DLE ETX)
    public static String charStuffing(String payload) {
        String DLE = "DLE";
        String STX = "STX";
        String ETX = "ETX";
        StringBuilder frame = new StringBuilder();

        frame.append(DLE).append(STX); // Start Flag

        // If payload contains "DLE", escape it with another "DLE"
        if (payload.contains(DLE)) {
            payload = payload.replace(DLE, DLE + DLE);
        }

        frame.append(payload);
        frame.append(DLE).append(ETX); // End Flag

        return frame.toString();
    }

    // Character Destuffing
    public static String charDestuffing(String frame) {
        String DLE = "DLE";
        String STX = "STX";
        String ETX = "ETX";

        // Remove DLE STX from start
        frame = frame.replaceFirst(DLE + STX, "");

        // Remove DLE ETX from end
        int endIndex = frame.lastIndexOf(DLE + ETX);
        if (endIndex != -1) {
            frame = frame.substring(0, endIndex);
        }

        // Remove stuffed DLE (DLE DLE → DLE)
        frame = frame.replace(DLE + DLE, DLE);

        return frame;
    }

    // Test example
    public static void main(String[] args) {
        String original = "ABCDLEXYZ";
        System.out.println("Original payload: " + original);

        String stuffed = charStuffing(original);
        System.out.println("After char stuffing: " + stuffed);

        String destuffed = charDestuffing(stuffed);
        System.out.println("After char destuffing: " + destuffed);
    }

}

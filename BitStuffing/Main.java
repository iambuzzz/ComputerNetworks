package CN.BitStuffing;

public class Main {
    // 1. Bit Stuffing (Insert 0 after five consecutive 1s)
    public static String bitStuffing(String data) {
        int count = 0;
        StringBuilder stuffed = new StringBuilder();

        for (char bit : data.toCharArray()) {
            if (bit == '1') {
                count++;
                stuffed.append('1');
            } else {
                count = 0;
                stuffed.append('0');
            }

            if (count == 5) {
                stuffed.append('0'); // Stuffing
                count = 0;
            }
        }

        return stuffed.toString();
    }

    // 2. Bit Destuffing (Remove 0 after five consecutive 1s)
    public static String bitDestuffing(String stuffedData) {
        int count = 0;
        StringBuilder destuffed = new StringBuilder();

        for (int i = 0; i < stuffedData.length(); i++) {
            char bit = stuffedData.charAt(i);

            if (bit == '1') {
                count++;
                destuffed.append('1');

                // If 5 consecutive 1s found, skip next bit (stuffed 0)
                if (count == 5) {
                    i++; // Skip the next bit
                    count = 0;
                }
            } else {
                count = 0;
                destuffed.append('0');
            }
        }

        return destuffed.toString();
    }

    // Test example
    public static void main(String[] args) {
        String original = "111111";
        System.out.println("Original data: " + original);

        String stuffed = bitStuffing(original);
        System.out.println("After stuffing: " + stuffed);

        String destuffed = bitDestuffing(stuffed);
        System.out.println("After destuffing: " + destuffed);
    }

}

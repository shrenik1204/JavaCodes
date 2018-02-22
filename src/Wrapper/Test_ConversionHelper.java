package Wrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class Test_ConversionHelper {


    public static void main (String[] args) {

        String iFilePath = "/Users/kishoresubramanian/Downloads/input-h202.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(iFilePath));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                String[] aSplitLine = line.split("!");
                if (aSplitLine.length >0){
                    ApplicationUtils.mSampleMasterQueue.addAll(Arrays.asList(aSplitLine).subList(1, aSplitLine.length));
                }
                counter++;
            }
            System.out.println("count = " + counter);

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }






    }
}

package Wrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Test_interpolate {

    private static LinkedList<String> mStringArray = new LinkedList<String>();

    public static void main(String[] args) throws IOException {

        String pyPath = "/Users/kishoresubramanian/Desktop/Demo Tests/sattva-07-07-15-58-19/algo-new1input-sattva-07-07-15-58-19.txt";
//        String pyFilepath = "/Users/kishoresubramanian/Sattva_Aravind/PythonCode/FileChanger_2_Index.py";
//        String[] pycommand = {"python", pyFilepath, pyPath};
//        Runtime rt = Runtime.getRuntime();
//        Process pr = rt.exec(pycommand);
////    	int exitval = proc.waitFor();
//        BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//        String line = "";
//        String line2 = null;
//        while ((line = bfr.readLine()) != null) {
//            // display each output line form python script
//            line2 = line;
//            System.out.println(line);
////            String line2 = System.getenv(line);
//            String iFilePath = line2;
            String iFilePath = pyPath;


            CubicInterpolate15 aCubic = new CubicInterpolate15();

            double[][] mInput = aCubic.convert(iFilePath);
            iFilePath = iFilePath.substring(0, iFilePath.length() - 4);
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath + ".csv"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mInput.length; i++) {
                    sb.append(mInput[i][0]);
                    sb.append(",");
                    sb.append(mInput[i][1]);
                    sb.append(",");
                    sb.append(mInput[i][2]);
                    sb.append(",");
                    sb.append(mInput[i][3]);
                    sb.append("\n");
                }
                br.write(sb.toString());
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // FILE CHANGER END
//		String iFilePath = "/Users/kishoresubramanian/Google Drive/AUF_Pune_2018/Bharati Hospital /B37/sattva-02-20-08-46-35/algo-new1input-sattva-02-20-08-46-35.txt";

//    		String iFilePath = "G:\\My Drive\\AUF_Pune_2018\\Bharati Hospital\\B32\\sattva-02-17-21-07-37\\new1input-sattva-02-17-21-07-37.txt";

//        4509, 4589, 9209, 9292, 11454, 11531, 12471, 12538
//        4512, 4594, 9216, 9301, 11465, 11544, 12486, 12555
//           3,    5,    7,    9,    11,    13,    15,    17
        }
    }
//}

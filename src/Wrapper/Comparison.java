package Wrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Comparison {
    public static void main(String[] args) throws Exception {
        /////////////////////// FHR Comparison /////////////////////
        File folderpath_fhr = new File("/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo/FHR");
        File[] listOfFiles_fhr = new File[0];
        listOfFiles_fhr = folderpath_fhr.listFiles();
        File folderpath_fhrold = new File("/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo test/FHR");
        File[] listOfFiles_fhrold = folderpath_fhrold.listFiles();


        /////////////////////// EXE Comparison /////////////////////
        File folderpath_exe = new File("/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo/EXE");
        File[] listOfFiles_exe = folderpath_exe.listFiles();
        File folderpath_exeold = new File("/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo test/EXE");
        File[] listOfFiles_exeold = folderpath_exeold.listFiles();

        /////////////////////// UA Comparison /////////////////////
        File folderpath_ua = new File("/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo/UA");
        File[] listOfFiles_ua = folderpath_ua.listFiles();
        File folderpath_uaold = new File("/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo test/UA");
        File[] listOfFiles_uaold = folderpath_uaold.listFiles();
        for (int i = 0; i < listOfFiles_fhr.length; i++) {

            if (folderpath_fhr.list()[i].compareTo(folderpath_fhrold.list()[i].toString()) == 1) {
                System.out.println("The Files are not the same");
//                break;
            }
        }
        if (listOfFiles_fhr.length != listOfFiles_fhrold.length) {
            System.out.println("There is a size mismatch of files in the folder");
        }
        FileWriter writer2 = new FileWriter("/Users/kishoresubramanian/Desktop/Sattva work/Java Test report/ConsolidatedList.csv");
        for (int i = 0; i < listOfFiles_fhr.length; i++) {
            ArrayList fhrarray = new ArrayList();
            ArrayList temparray = new ArrayList();
            ArrayList fhrarray_final = new ArrayList();
            ArrayList fhrarray_old = new ArrayList();
            ArrayList<String> exearray = new ArrayList();
            ArrayList<String> exearray_old = new ArrayList();
            ArrayList uaarray = new ArrayList();
            ArrayList uararray_old = new ArrayList();

            BufferedReader CSVFile1 = new BufferedReader(new FileReader(listOfFiles_fhr[i]));
            fhrarray = writetoarray(CSVFile1);
            CSVFile1.close();
//            BufferedReader CSVFiletemp = new BufferedReader(new FileReader(listOfFiles_fhr[i]));
//            temparray = writetoarray(CSVFiletemp);
//            CSVFiletemp.close();

            BufferedReader CSVFile2 = new BufferedReader(new FileReader(listOfFiles_fhrold[i]));
            fhrarray_old = writetoarray(CSVFile2);
            CSVFile2.close();

            BufferedReader CSVFile3 = new BufferedReader(new FileReader(listOfFiles_exe[i]));
            exearray = writetoarray(CSVFile3);
            CSVFile3.close();

            BufferedReader CSVFile4 = new BufferedReader(new FileReader(listOfFiles_exeold[i]));
            exearray_old = writetoarray(CSVFile4);
            CSVFile4.close();

            temparray.addAll(fhrarray);

            fhrarray.removeAll(fhrarray_old);
            fhrarray_old.removeAll(temparray);
            for (int j = 0; j < fhrarray.size(); j++) {
                fhrarray_final.add(fhrarray.get(j).toString().concat(",").concat(fhrarray_old.get(j).toString()));
            }



//            ArrayList<Integer> x = new ArrayList<>();
            int size = fhrarray.size();
            ArrayList<Integer> IterlocationList_fhr = null;
            ArrayList<Integer> resultList_exe = null;
            ArrayList<Integer> resultList_exeold = null;
            IterlocationList_fhr = getIntegerArray(fhrarray,true);
            Set<Integer> s = new LinkedHashSet<>(IterlocationList_fhr);
            IterlocationList_fhr.clear();
            IterlocationList_fhr.addAll(s);
            resultList_exe = getIntegerArray(exearray,false);
            ArrayList exereport = new ArrayList();
            resultList_exeold = getIntegerArray(exearray_old,false);
            ArrayList exereportold = new ArrayList();
            if (size != 0) {
                for (int loc : IterlocationList_fhr
                        ) {
                    for (int k = 0; k < resultList_exe.size(); k++) {
                        if(loc == resultList_exe.get(k)){
                            exereport.add(exearray.get(k));
                        }
                    }
                }

                for (int loc : IterlocationList_fhr
                        ) {
                    for (int k = 0; k < resultList_exeold.size(); k++) {
                        if(loc == resultList_exeold.get(k)){
                            exereportold.add(exearray_old.get(k));
                        }
                    }
                }

            }
            System.out.println("FHR Array size :" + size);
            FileWriter writer = new FileWriter("/Users/kishoresubramanian/Desktop/Sattva work/Java Test report/" + folderpath_fhr.list()[i]);

            fileWriter(writer, fhrarray_final.size(), fhrarray_final);

            writer.append("New Algo");
            writer.append('\n');
            writer.append("Iteration , Start Location, MA , QRSM Detection , QRSF Selection Type, Last Fetal QRS, No of QRSF Selected, No of FHR computed, Last RR mean Fetal \n");

            fileWriter(writer, exereport.size(), exereport);

            writer.append("Old Algo");
            writer.append('\n');
            writer.append("Iteration, Start Location, MA , QRSM Detection , QRSF Selection Type, Last Fetal QRS, No of QRSF Selected, No of FHR computed, Last RR mean Fetal \n");

            fileWriter(writer, exereportold.size(), exereportold);

            writer.flush();
            writer.close();

            writer2.append('\n');
            writer2.append(folderpath_fhr.list()[i]);
            writer2.append('\n');
            writer2.append("Iteration, Iteration Old, Start Loc, Start Loc Old, MA, MA Old, QRSM Detection, QRSM Detection Old, QRSF Selection Type, QRSF Selection Type Old, Last Fetal QRS, Last Fetal QRS Old, No of QRSF Selected, No of QRSF Selected Old, No of FHR computed, No of FHR computed Old, Last RR mean Fetal, Last RR mean Fetal Old \n");
            getnewExe(exereport, exereportold, writer2);




        }
        writer2.flush();
        writer2.close();

    }

    public static ArrayList writetoarray(BufferedReader CSVFile) throws Exception {
        ArrayList arr = new ArrayList();
        String dataRow1 = CSVFile.readLine();
        while (dataRow1 != null) {
            String[] dataArray1 = dataRow1.split("\n");
            for (String item1 : dataArray1) {
                arr.add(item1);
            }
            dataRow1 = CSVFile.readLine(); // Read next line of data.
        }
        return arr;
    }

    public static ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray, boolean div) {
        ArrayList<String> newstr = new ArrayList<>();
        if(!div){
            stringArray.remove(0);
        }
        ArrayList<Integer> loc = new ArrayList();
        ArrayList<Integer> fhr = new ArrayList();
        String[] s = null;

        for ( String str: stringArray) {
            try{
                s = str.split(",");
                if(div){
                    loc.add(Integer.parseInt(s[0])/10000);
                }
                else{
                    loc.add(Integer.parseInt(s[0]));
                }
                fhr.add(Integer.parseInt(s[1]));
            } catch (NumberFormatException nfe){
                System.out.println("Error in Conversion to Integer List");
            }
//            str = str.substring(0, str.indexOf(","));
//            newstr.add(str);
        }
//        ArrayList<Integer> result = new ArrayList<Integer>();
//        for(String stringValue : newstr) {
//            try {
//                //Convert String to Integer, and store it into integer array list.
//                if(div){
//                    result.add(Integer.parseInt(stringValue)/10000);
//                }
//                else{
//                    result.add(Integer.parseInt(stringValue));
//                }
//            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
//                Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
//                System.out.println("Error in Conversion to Integer List");
//            }
//        }
        return loc;
    }

    public static void getnewExe(ArrayList<String> stringArray1,ArrayList<String> stringArray2, FileWriter writer) {
        ArrayList<String> Iteration = new ArrayList();
        ArrayList<String> StartLoc = new ArrayList();
        ArrayList<String> MA = new ArrayList();
        ArrayList<String> QRSMDetect = new ArrayList();
        ArrayList<String> QRSFSelectType = new ArrayList();
        ArrayList<String> LastFetalQRS = new ArrayList();
        ArrayList<String> QRSFSelected_No = new ArrayList();
        ArrayList<String> FHRcomputed_No = new ArrayList();
        ArrayList<String> RRmeanFetal_Last = new ArrayList();

        String[] s  = {"0", "0", "0", "0", "0", "0", "0", "0", "0"};
        String[] r = {"0", "0", "0", "0", "0", "0", "0", "0", "0"};
        for ( String str: stringArray1) {
            String[] splitArray = str.split(",");

            for (int i = 0; i < splitArray.length; i++) {
                s[i] = splitArray[i];
            }

            Iteration.add(s[0]);
            StartLoc.add(s[1]);
            MA.add(s[2]);
            QRSMDetect.add(s[3]);
            QRSFSelectType.add(s[4]);
            LastFetalQRS.add(s[5]);
            QRSFSelected_No.add(s[6]);
            FHRcomputed_No.add(s[7]);
            RRmeanFetal_Last.add(s[8]);
        }

        ArrayList<String> Iteration_new = new ArrayList(Iteration);
        ArrayList<String> StartLoc_new = new ArrayList(StartLoc);
        ArrayList<String> MA_new = new ArrayList(MA);
        ArrayList<String> QRSMDetect_new = new ArrayList(QRSMDetect);
        ArrayList<String> QRSFSelectType_new = new ArrayList(QRSFSelectType);
        ArrayList<String> LastFetalQRS_new = new ArrayList(LastFetalQRS);
        ArrayList<String> QRSFSelected_No_new = new ArrayList(QRSFSelected_No);
        ArrayList<String> FHRcomputed_No_new = new ArrayList(FHRcomputed_No);
        ArrayList<String> RRmeanFetal_Last_new = new ArrayList(RRmeanFetal_Last);

        ArrayList<String> Final_Exe = new ArrayList<>();

        Iteration.clear();
        StartLoc.clear();
        MA.clear();
        QRSMDetect.clear();
        QRSFSelectType.clear();
        LastFetalQRS.clear();
        QRSFSelected_No.clear();
        FHRcomputed_No.clear();
        RRmeanFetal_Last.clear();

        for ( String str: stringArray2) {
            String[] splitArray = str.split(",");

            for (int i = 0; i < splitArray.length; i++) {
                r[i] = splitArray[i];
            }
            Iteration.add(r[0]);
            StartLoc.add(r[1]);
            MA.add(r[2]);
            QRSMDetect.add(r[3]);
            QRSFSelectType.add(r[4]);
            LastFetalQRS.add(r[5]);
            QRSFSelected_No.add(r[6]);
            FHRcomputed_No.add(r[7]);
            RRmeanFetal_Last.add(r[8]);
        }

        for (int i = 0; i < stringArray1.size(); i++) {
            Final_Exe.add(Iteration_new.get(i).concat(",").concat(Iteration.get(i)).concat(",").
                    concat(StartLoc_new.get(i)).concat(",").concat(StartLoc.get(i)).concat(",").
                    concat(MA_new.get(i)).concat(",").concat(MA.get(i)).concat(",").
                    concat(QRSMDetect_new.get(i)).concat(",").concat(QRSMDetect.get(i)).concat(",").
                    concat(QRSFSelectType_new.get(i)).concat(",").concat(QRSFSelectType.get(i)).concat(",").
                    concat(LastFetalQRS_new.get(i)).concat(",").concat(LastFetalQRS.get(i)).concat(",").
                    concat(QRSFSelected_No_new.get(i)).concat(",").concat(QRSFSelected_No.get(i)).concat(",").
                    concat(FHRcomputed_No_new.get(i)).concat(",").concat(FHRcomputed_No.get(i)).concat(",").
                    concat(RRmeanFetal_Last_new.get(i)).concat(",").concat(RRmeanFetal_Last.get(i)));
        }
        fileWriter(writer, Final_Exe.size(),Final_Exe);

    }

    public static void fileWriter(FileWriter writer, int size, ArrayList array){
        int j = 0;

        try {

            while (j < size) {
//                    size--;
                writer.append("" + array.get(j));
                writer.append('\n');
//                    writer.append("" + exearray.get(j));
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
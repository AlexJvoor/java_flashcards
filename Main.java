package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

class Flashcards {
    Scanner fileScan;
    Map<String, String> tnd = new LinkedHashMap<>();//terms n definitions
    Map<String, String> dnt = new LinkedHashMap<>();//definitions n terms
    List<String> log = new ArrayList<>();//log to be saved
    List<Integer> statArr = new ArrayList<>();//statistics of all current cards
    List<String> tmpSet;
    File file;
    Random rand = new Random(2142);
    String action = "null";
    String tmpTerm;
    String fileName;
    String tmpDef;
    String fileNameImport;
    String fileNameExport;
    StringBuilder tmpString;
    int count;
    int tmpHardest;
    int tmpRand;
    boolean exitFlag = false;

    private static void addLog(List<String> log, String str) {
        log.add(str);
        System.out.print(str);
    }

    void checkImEx(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-import":
                        fileNameImport = args[i + 1];
                        importCase();
                        break;
                    case "-export":
                        fileNameExport = args[i + 1];
                        exitFlag = true;
                        break;
                }
            }
        }
    }

    void filePrepImport(Scanner scan) {
        tmpSet = new ArrayList<>(tnd.keySet());
        addLog(log, "File name:\n");
        fileNameImport = scan.nextLine();
        log.add(fileNameImport + "\n");
        file = new File(fileNameImport);
    }

    void filePrepExport(Scanner scan) {
        tmpSet = new ArrayList<>(tnd.keySet());
        addLog(log, "File name:\n");
        fileNameExport = scan.nextLine();
        log.add(fileNameExport + "\n");
        file = new File(fileNameExport);
    }

    void addCase(Scanner scan) {
        addLog(log ,"The card:\n");
        tmpTerm = scan.nextLine();
        log.add(tmpTerm + "\n");
        if (tnd.containsKey(tmpTerm)) {
            addLog(log, "The card \"" + tmpTerm + "\" already exists\n");
            return;
        }
        addLog(log, "The definition of the card:\n");
        tmpDef = scan.nextLine();
        log.add(tmpDef + "\n");
        if (dnt.containsKey(tmpDef)) {
            addLog(log, "The definition \"" + tmpDef + "\" already exists\n");
            return;
        }
        tnd.put(tmpTerm, tmpDef);
        dnt.put(tmpDef, tmpTerm);
        statArr.add(0);
        addLog(log, "The pair (\"" + tmpTerm + ":" + tmpDef + "\") has been added.\n");
    }

    void removeCase(Scanner scan) {
        addLog(log, "The card:\n");
        tmpTerm = scan.nextLine();
        log.add(tmpTerm + "\n");
        if(tnd.containsKey(tmpTerm)) {
            tmpSet = new ArrayList<>(tnd.keySet());
            statArr.remove(tmpSet.indexOf(tmpTerm));
            dnt.remove(tnd.get(tmpTerm));
            tnd.remove(tmpTerm);
            addLog(log, "The card has been removed.\n");
        }
        else {
            addLog(log, "Can't remove \""+ tmpTerm +"\": there is no such card\n");
        }
    }

    void importCase() {
        file = new File(fileNameImport);
        try {
            count = 0;
            fileScan = new Scanner(file);
            while(fileScan.hasNext()) {
                tmpTerm = fileScan.nextLine();
                tmpDef = fileScan.nextLine();
                tmpHardest = fileScan.nextInt();
                fileScan.nextLine();
                if (tnd.containsKey(tmpTerm) || dnt.containsKey(tmpDef)) {
                    if (tnd.containsKey(tmpTerm)) {
                        statArr.remove(tmpSet.indexOf(tmpTerm));
                        dnt.remove(tnd.get(tmpTerm));
                        tnd.remove(tmpTerm);
                    } else {
                        statArr.remove(tmpSet.indexOf(tmpTerm));
                        tnd.remove(dnt.get(tmpDef));
                        dnt.remove(tmpDef);
                    }
                }
                statArr.add(tmpHardest);
                tnd.put(tmpTerm, tmpDef);
                dnt.put(tmpDef, tmpTerm);
                count++;
            }
            System.out.printf("%d cards have been loaded\n", count);
        }
        catch (FileNotFoundException e) {
            addLog(log, "File not found.\n");
            return;
        }
    }

    void exportCase() {
        file = new File(fileNameExport);
        try (PrintWriter pw = new PrintWriter(file)) {
            count = 0;
            for (Map.Entry<String, String> entry : tnd.entrySet()) {
                pw.println(entry.getKey());
                pw.println(entry.getValue());
                pw.println(statArr.get(tmpSet.indexOf(entry.getKey())));
                count++;
            }
            addLog(log, count + " cards have been saved.\n");
        }
        catch(IOException e) {
            addLog(log, "IOException\n");
            return;
        }
    }

    void askCase(Scanner scan) {
        tmpSet = new ArrayList<>(tnd.keySet());
        System.out.println("How many times to ask?");
        int i = scan.nextInt();
        scan.nextLine();
        while(i > 0){
            tmpRand = rand.nextInt(tmpSet.size());
            tmpTerm = tmpSet.get(tmpRand);
            System.out.printf("Print the definition of \"%s\"\n", tmpTerm);
            tmpDef = scan.nextLine();
            if (tmpDef.contentEquals(tnd.get(tmpTerm))) {
                System.out.println("Correct!");
            }
            else {
                System.out.printf("Wrong, the right answer is \"%s\"", tnd.get(tmpTerm));
                if (dnt.containsKey(tmpDef)) {
                    System.out.printf(", but your definition is correct for \"%s\".\n", dnt.get(tmpDef));
                }
                else {System.out.println(".");}
                statArr.set(tmpRand, (statArr.get(tmpRand) + 1));
            }
            i--;
        }
    }

    void logCase(Scanner scan) {
        addLog(log, "File name:\n");
        fileName = scan.nextLine();
        log.add(fileName);
        file = new File(fileName);
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String str : log) {
                pw.print(str);
            }
        }
        catch (IOException e) {
            addLog(log, "IOException\n");
            return;
        }
        addLog(log, "The log has been saved.\n");
    }

    void hardestCase() {
        tmpHardest = 0;
        count = 0;
        String prefix = "";
        tmpString = new StringBuilder("");
        for (Integer val : statArr) {
            if (val > tmpHardest) {
                tmpHardest = val;
            }
        }
        for (Integer val : statArr) {
            if (val == tmpHardest) {
                count++;
            }
        }
        if (tmpHardest > 0) {
            tmpSet = new ArrayList<>(tnd.keySet());
            if (count == 1) {
                tmpString.append(tmpSet.get(statArr.indexOf(tmpHardest)));
                addLog(log, ("The hardest card is \"" + tmpString + "\". You have " + tmpHardest + " errors answering it.\n"));
            }
            else {
                for (String term : tmpSet) {
                    if (statArr.get(tmpSet.indexOf(term)) == tmpHardest) {
                        tmpString.append(prefix);
                        prefix = ",";
                        tmpString.append(" \"" + term + "\"");
                    }
                }
                addLog(log, "The hardest cards are" + tmpString + ". You have " + tmpHardest + " errors answering them.\n");
            }
        }
        else {
            addLog(log, "There are no cards with errors.\n");
        }
    }
}

public class Main {

    private static void addLog(List<String> log, String str) {
        log.add(str);
        System.out.print(str);
    }

    public static void main(String[] args) {
        Flashcards all = new Flashcards();
        Scanner scan = new Scanner(System.in);
        all.checkImEx(args);

        while (!all.action.contentEquals("exit")) {
            addLog(all.log,"Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");
            all.action = scan.nextLine();
            all.log.add(all.action + "\n");
            switch (all.action) {
                case ("add"):
                    all.addCase(scan);
                    break;
                case ("remove"):
                    all.removeCase(scan);
                    break;
                case ("import"):
                    all.filePrepImport(scan);
                    all.importCase();
                    break;
                case ("export"):
                    all.filePrepExport(scan);
                    all.exportCase();
                    break;
                case ("ask"):
                    all.askCase(scan);
                    break;
                case ("exit"):
                    addLog(all.log, "Bye bye!\n");
                    if (all.exitFlag) {
                        all.exportCase();
                    }
                    break;
                case ("log"):
                    all.logCase(scan);
                    break;
                case("hardest card"):
                    all.hardestCase();
                    break;
                case("reset stats"):
                    for (int ctr = 0; ctr < all.statArr.size(); ctr++) {
                        all.statArr.set(ctr, 0);
                    }
                    addLog(all.log, "Card statistics has been reset\n");
                    break;
                default:
                    break;
            }
        }
    }
}

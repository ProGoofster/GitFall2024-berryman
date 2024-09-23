package org.example;

import java.io.*;
import java.util.LinkedList;
import java.util.Random;
import java.lang.Byte;
import java.util.Scanner;


/*
    The program should prompt
    the user to input the name of a file that contains the card names and their respective
    energy costs. It will then create a report that tallies the total energy cost and a energy
    cost histogram for the deck and output the report in a pdf file.

    Jake Berryman
 */
public class SlayTheSpire {
    private String fileName;
    private LinkedList<String[]> FileContents;
    private int ID;
    private int totalCost;
    private boolean voidReport;
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter deck file location: ");
        String fileName = scan.nextLine();
        try {
            SlayTheSpire a = new SlayTheSpire(fileName);
        } catch (Exception e) {
            System.out.println(fileName+ " does not exist, try again!");
        }

    }

    //constructor, takes in a file name,
    public SlayTheSpire(String fileName) {
        voidReport = false;
        this.fileName = fileName;
        FileContents = generateFileContent();
        ID = generateID();
        totalCost = getTotalCost();
        new HistogramGenerator(FileContents, ID, voidReport, totalCost);
    }

    //generates a unique id by converting each line to bytes, then longs and adding each long together
    public int generateID(){
        if (voidReport) return 0;
        long seed = 0;
        for(String[] i : FileContents){
            byte[] bytes = (i[0]+i[1]).getBytes();
            for(Byte j : bytes){
                seed = seed + j.longValue();
            }
        }
        Random r = new Random(seed);
        return 100000000 + r.nextInt(900000000);
    }

    public int getTotalCost(){
        if (voidReport) return 0;
        int cost = 0;
        for(String[] i : FileContents){
            cost += Integer.parseInt(i[1]);
        }
        return cost;
    }

    public LinkedList<String[]> generateFileContent() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            LinkedList<String[]> fileData= new LinkedList<>();
            String currentLine = br.readLine();

            if (currentLine == null || currentLine.isEmpty()){
                fileData.push(new String[]{"",""});
            }

            int invalid = 0;
            while (currentLine != null) {
                String[] arr = currentLine.split(":", 2);
                //toss entry if it isn't formatted right
                if (!(arr.length != 2 || arr[0].trim().isEmpty() || arr[1].trim().isEmpty()) && arr[1].matches("\\d+")) {
                    fileData.push(arr);
                } else invalid++;
                currentLine = br.readLine();
                if(fileData.size() > 1000 || invalid > 10){
                    voidReport = true;
                    return null;
                }
            }

            br.close();
            return fileData;
        }
        catch(Exception e) {
            System.out.println("File Read Error");
            return null;
        }
    }
}
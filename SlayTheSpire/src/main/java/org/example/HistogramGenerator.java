package org.example;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;


public class HistogramGenerator {
    private HashMap<String, Integer> PerCardTotalCost;
    private int totalEnergyCost;

    public HistogramGenerator(LinkedList<String[]> FileContents, int ID, boolean voidReport, int totalCost){
        if(voidReport){
            String fileName = "SpireDeck_" + ID + "(VOID).pdf";
            voidReport(fileName);
        } else {
            totalEnergyCost = totalCost;
            PerCardTotalCost = getPerCardTotalCost(FileContents);
            String fileName = "SpireDeck_" + ID + ".pdf";

            JFreeChart chart = createHistogram(PerCardTotalCost);
            writeAsPDF(chart, fileName, 1280, 720);
        }
    }

    public static void voidReport(String pdfName){
        try {
            Document document = new Document(new PdfDocument(new PdfWriter(pdfName)));

            document.add(new Paragraph("VOID").setFontSize(200));

            document.close();

            System.out.println("VOID added");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //generates a 720p pdf, places the chart at the top and total cost below
    public void writeAsPDF(JFreeChart chart, String pdfName, int width, int height) {
        try {;
            Document document = new Document(new PdfDocument(new PdfWriter(pdfName)));

            //turns chart into image, encodes it as a png, then creates image data and adds it to the pdf
            ImageData data = ImageDataFactory.create(ChartUtils.encodeAsPNG(chart.createBufferedImage(width, height)));
            document.add(new Image(data));
            document.add(new Paragraph("total cost: " + totalEnergyCost));

            document.close();

            System.out.println("Results generated to file: " + pdfName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //returns the chart, takes in the the hashmap corresponding to costs
    private static JFreeChart createHistogram(HashMap<String, Integer> cardCount) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String key : cardCount.keySet()) {
            dataset.addValue(cardCount.get(key), "Card", key);
        }

        return ChartFactory.createBarChart(
                "Energy Cost Histogram",
                "Cards",
                "Card Energy Cost",
                dataset
        );
    }

    //generates a hash map where the card name is the key and the value is the total amount of energy they take together
    public HashMap<String, Integer> getPerCardTotalCost(LinkedList<String[]> FileContents){
        HashMap<String, Integer> countMap = new HashMap<>();

        for (String[] str : FileContents) {
            //key is the name, adds cost every time it appears again
            countMap.put(str[0], countMap.getOrDefault(str[0], 0) + Integer.parseInt(str[1]));
        }

        return countMap;
    }
}

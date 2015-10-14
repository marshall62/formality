package formality.Util;

import formality.Util.model.StudentSkillMatrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Collections;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Dec 11, 2006
 * Time: 9:16:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataGenerator {

    String[] diffLevels_= {"easier", "medium", "harder"};
    Random randomGen_;
      private final static String tab_ = "\t";
   private final static String endline_ = "\r\n";

   //generate N number of questions with P percent correct responses for one skill
   public String getTestOneData(int N, double P){
       StringBuffer out = new StringBuffer();
       int qID;
       double nextDouble = 0.0;
       int nextAns=0;
       String modID = "22";
       String userID = "52";
       String skill = StudentSkillMatrix.skillNames_[0];
       randomGen_ = new Random();
       for(int i=0;i<N;i++){
           nextDouble = randomGen_.nextDouble();
           if(nextDouble<=P)
               nextAns=1;
           else
           nextAns=0;
           out.append(i).append(tab_).append(modID).append(tab_);
           out.append(userID).append(tab_).append(nextAns).append(tab_);
           out.append(skill).append(tab_).append(diffLevels_[1]).append(endline_);
       }
       return out.toString();
   }

       //generate N number of questions 3 skills one G with P percent correct responses for each skill
   public String getTestTwoData(int N){
       StringBuffer out = new StringBuffer();
       int qID;
       double nextDouble = 0.0;
       int nextAns=0;
       String modID = "22";
       String userID = "52";
       String skill1 = StudentSkillMatrix.skillNames_[0];
       String skill2 = StudentSkillMatrix.skillNames_[1];
       String skill3 = StudentSkillMatrix.skillNames_[2];
       randomGen_ = new Random();
       ArrayList lines = new ArrayList();
       for(int i=0;i<N;i++){
           nextDouble = randomGen_.nextDouble();
           if(nextDouble<=.3)
               nextAns=1;
           else
           nextAns=0;
           lines.add(i+tab_+modID+tab_+userID+tab_+nextAns+tab_+skill1+tab_+diffLevels_[1]+endline_);
       }
       for(int i=0;i<N;i++){
           nextDouble = randomGen_.nextDouble();
           if(nextDouble<=.2)
               nextAns=1;
           else
           nextAns=0;
           lines.add((i+N)+tab_+modID+tab_+userID+tab_+nextAns+tab_+skill2+tab_+diffLevels_[1]+endline_);
          // out.append(i+N).append(tab_).append(modID).append(tab_);
          // out.append(userID).append(tab_).append(nextAns).append(tab_);
          // out.append(skill2).append(tab_).append(diffLevels_[1]).append(endline_);
       }
       for(int i=0;i<N;i++){
           nextDouble = randomGen_.nextDouble();
           if(nextDouble<=.9)
               nextAns=1;
           else
           nextAns=0;
           lines.add((i+N+N)+tab_+modID+tab_+userID+tab_+nextAns+tab_+skill3+tab_+diffLevels_[1]+endline_);
          // out.append(i+N+N).append(tab_).append(modID).append(tab_);
           //out.append(userID).append(tab_).append(nextAns).append(tab_);
           //out.append(skill3).append(tab_).append(diffLevels_[1]).append(endline_);
       }
       Collections.shuffle(lines);
       return getLines(out, lines);
   }

   private String getLines(StringBuffer out, ArrayList lines){
       for(int i=0;i<lines.size();i++){
          out.append((String)lines.get(i));
       }
       return out.toString();
   }
       //assume data is a list of lists
   public void writeData(String fileName, ArrayList data) throws Exception {
       File outFile=new File(fileName);
       FileOutputStream outFileStream=new FileOutputStream(outFile, true);//not append=false
       PrintWriter outStream=new PrintWriter(outFileStream);
       Iterator dataiter = data.iterator();
       while(dataiter.hasNext()){
           ArrayList line = (ArrayList)dataiter.next();
           Iterator lineiter = line.iterator();
           while(lineiter.hasNext()){
             String d  = (String)lineiter.next();
             outStream.print(d);
             outStream.print('\t');
           }
          outStream.print('\r');
          outStream.print('\n');
       }
       outStream.close();
    }
    public void writeReport(String report, String fileName) throws Exception {
       File outFile=new File(fileName);
       FileOutputStream outFileStream=new FileOutputStream(outFile, true);//not append=false
       PrintWriter outStream=new PrintWriter(outFileStream);
       outStream.print(report);
       outStream.close();
    }

     public static void main(String[] args) {
         DataGenerator d = new DataGenerator();
          try{
          String outFile = args[0];
          int numQuestions = Integer.parseInt(args[1]);
          double percentCorr  = Double.parseDouble(args[2]);
          //d.writeReport(d.getTestOneData(numQuestions, percentCorr), outFile);
          d.writeReport(d.getTestTwoData(numQuestions), outFile);
         } catch(Exception ex){
            System.out.println("ERROR  "+ex.getMessage());
         }
      }
}

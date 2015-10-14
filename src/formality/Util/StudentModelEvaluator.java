package formality.Util;

import formality.parser.StringEncoder;
import formality.Util.model.StudentSkillMatrix;
import formality.Util.model.StudentTwoSkillMatrix;
import formality.Util.model.StudentDiscSkillMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Dec 9, 2006
 * Time: 3:40:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentModelEvaluator {
    String[] qidsi={"39","68","69","75","80","81","85","86","87","89","91","96","100","138","139","140","141"};
    Random R;
    //the list will be a list of lists
   public void readDataFile(String fileName, ArrayList data) throws Exception
     {
         if ( fileName == null || fileName.equals( "" ) )
           System.err.print( "Invalid File Name" );
         else {
           String inputLine;
           FileReader fileReader;
           File inFile=new File(fileName);
            fileReader=new FileReader(inFile);
           BufferedReader bufReader =new BufferedReader(fileReader);
           try{ while(true){
               inputLine = bufReader.readLine();
               if(inputLine != null){
                 processLine(data, inputLine);
               }
               else break;
             }
           }
                catch ( EOFException eofex ) { bufReader.close(); }
            bufReader.close();
           }
    }

     protected void processLine(ArrayList data, String inputLine) throws Exception
    {
      ArrayList line = new ArrayList();
      StringTokenizer tokens =new StringTokenizer(inputLine);
      Vector lineVec = new Vector();
            while (tokens.hasMoreTokens())
            {
              String word=tokens.nextToken();
              line.add(word);
            }
      data.add(line);
    }


    public Connection getConnection()throws Exception {
          Connection conn=null;
          Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
          String dbpwd="w{:Tgd4V8je6";
           //COMPILE THIS LINE FOR ONION: conn= DriverManager.getConnection("jdbc:odbc:4malityDB", "4mality_login", StringEncoder.decode(dbpwd));
           //The develeopment DB is 4malityDev:
           // conn= DriverManager.getConnection("jdbc:odbc:4mality_TomatoDev", "4mality_login", StringEncoder.decode(dbpwd));
           //The user DB is 4mality:
           conn= DriverManager.getConnection("jdbc:odbc:4mality", "4mality_login", StringEncoder.decode(dbpwd));
         return conn;
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
/******************************************************************************************************************
 * ******************************************************************************************************************/
   String sModelQuery = "SELECT f4N1,f4N2,f4N3,f4N4,f4N5,f4N6,f4N7,f4N8,f4N9,f4N10,f4N11,f4N12,\n" +
        "f4N13,f4N14,f4N15,f4N16,f4N17,f4N18,f4P1,f4P2,f4P3,f4P4,f4P5,f4P6,\n" +
        "f4G1,f4G2,f4G3,f4G4,f4G5,f4G6,f4G7,f4G8,f4G9,f4M1,f4M2,f4M3,f4M4,\n" +
        "f4M5,f4D1,f4D2,f4D3,f4D4,f4D5,f4D6 from StudentFrameworkData \n" +
        "where UserID=";

   public TreeMap getStudentModel(Connection conn, String sID) throws Exception {
    TreeMap data = new TreeMap();
    Statement stmt=conn.createStatement();
      ResultSet res;
      res=stmt.executeQuery(sModelQuery+sID);
      while(res.next()){
          for(int i=1;i<19;i++){
             data.put("4.N."+i, new Double(res.getDouble("f4N"+i)));
          }
          for(int i=1;i<7;i++){
             data.put("4.P."+i, new Double(res.getString("f4P"+i)));
          }
          for(int i=1;i<10;i++){
             data.put("4.G."+i, new Double(res.getString("f4G"+i)));
          }
          for(int i=1;i<5;i++){
             data.put("4.M."+i, new Double(res.getString("f4M"+i)));
          }
          for(int i=1;i<7;i++){
             data.put("4.D."+i, new Double(res.getString("f4D"+i)));
          }
      }
      res.close();
      stmt.close();
     return data;
  }

    public void printModel() throws Exception {
        TreeMap map = getStudentModel(getConnection(), "319");

        System.out.print(map.toString());
    }

    //group all student data into separate ArrayLists of arraylists
     public ArrayList separateStudentData(ArrayList inputFileData) throws Exception
     {
        String curStudentID="";
        ArrayList curStudentData = null;
        ArrayList allStudentData = new ArrayList();
        Iterator fileiter = inputFileData.iterator();
         while(fileiter.hasNext()){
           ArrayList line = (ArrayList)fileiter.next();
           String sID= (String)line.get(2);
           String corr = (String)line.get(3);
           String skill = (String)line.get(4);
           if(curStudentID.equals(sID)){
               curStudentData.add(line);
           }
           else{
              allStudentData.add(curStudentData);
              curStudentData = new ArrayList();
              curStudentData.add(line);
           }
       }
      return allStudentData;
     }
/**********************************************************************************************************/
//pred reports

public String getPredResults(ArrayList inputFileData, TreeMap studentModels) throws Exception{
     StringBuffer predRpt = new StringBuffer();
   double ACC = 0.0, MAE=0.0, MSE=0.0, correct = 0.0, incorrect=0.0,
           curResp = 0.0, predictedResp=0.0, p = 0.0;
   StringBuffer res = new StringBuffer();
   String curStudentID="";
   StudentSkillMatrix curModel = null;
   Iterator fileiter = inputFileData.iterator();
   int ct =0;
    while(fileiter.hasNext()){
        ArrayList line = (ArrayList)fileiter.next();
        ct++;
        String sID= "";
        try{
        sID= (String)line.get(2);
        }catch (Exception ex){
           System.out.println("ERROR  "+ex.getMessage()+" ct= "+ct);
            break;
        }
        String corr = (String)line.get(3);
        String skill = (String)line.get(4);
        if(corr.equals("0"))
           curResp=0.0;
        else
           curResp=1.0;
        if(!curStudentID.equals(sID)){
           //curModel = getStudentModel(getConnection(), sID);
           curModel = (StudentSkillMatrix)studentModels.get(sID);
           curStudentID=sID;
        }
        p=curModel.getSkillVal(skill);
        predRpt.append(sID).append('\t').append(skill).append('\t').append(p).append("\r\n");
        if(p>=0.5)
            predictedResp=1.0;
        else
            predictedResp=0.0;
        if(curResp==predictedResp)
            correct++;
        else
            incorrect++;
        MAE = MAE+Math.abs(curResp-p);
        MSE = MSE + Math.pow((curResp - p), 2);
    }
   ACC = correct/(correct+incorrect);
   MAE = MAE/(correct+incorrect);
   MSE = MSE/(correct+incorrect);
   res.append("S Model").append("\r\n");
   res.append("Correct = ").append(correct).append("\r\n");
   res.append("Incorrect = ").append(incorrect).append("\r\n");
   res.append("Accuracy = ").append(ACC).append("\r\n");
   res.append("Mean Absolute Error = ").append(MAE).append("\r\n");
   res.append("Mean Squared Error = ").append(MSE).append("\r\n");
   writeReport(predRpt.toString(), "G:/courses/ML689/project/results/predictions.txt");
  return res.toString();
 }


/**********************************************************************************************************/
//accuracy reports
   public String calculateRandomResults(ArrayList inputFileData) throws Exception{
           R = new Random();
      double ACC = 0.0, MAE=0.0, MSE=0.0, correct = 0.0, incorrect=0.0,
              curResp = 0.0, predictedResp=0.0, p = 0.0;
      StringBuffer res = new StringBuffer();
      String curStudentID="";
     // StudentSkillMatrix curModel = null;
      Iterator fileiter = inputFileData.iterator();
      int ct =0;
       while(fileiter.hasNext()){
           ArrayList line = (ArrayList)fileiter.next();
           ct++;
           String sID= "";
           try{
           sID= (String)line.get(2);
           }catch (Exception ex){
              System.out.println("ERROR  "+ex.getMessage()+" ct= "+ct);
               break;
           }
           String corr = (String)line.get(3);
           String skill = (String)line.get(4);
           if(corr.equals("0"))
              curResp=0.0;
           else
              curResp=1.0;
           if(!curStudentID.equals(sID)){
              //curModel = getStudentModel(getConnection(), sID);
              //curModel = (StudentSkillMatrix)studentModels.get(sID);
              curStudentID=sID;
           }
           p=R.nextDouble();
           if(p>=0.5)
               predictedResp=1.0;
           else
               predictedResp=0.0;
           if(curResp==predictedResp)
               correct++;
           else
               incorrect++;
           MAE = MAE+Math.abs(curResp-p);
           MSE = MSE + Math.pow((curResp - p), 2);
       }
      ACC = correct/(correct+incorrect);
      MAE = MAE/(correct+incorrect);
      MSE = MSE/(correct+incorrect);
      res.append("S Model").append("\r\n");
      res.append("Correct = ").append(correct).append("\r\n");
      res.append("Incorrect = ").append(incorrect).append("\r\n");
      res.append("Accuracy = ").append(ACC).append("\r\n");
      res.append("Mean Absolute Error = ").append(MAE).append("\r\n");
      res.append("Mean Squared Error = ").append(MSE).append("\r\n");
      return res.toString();
    }

    public String calculateResults(ArrayList inputFileData, TreeMap studentModels) throws Exception{
        StringBuffer predRpt = new StringBuffer();
        predRpt.append("sID").append('\t').append("skill").append('\t').append("prediction").append("\r\n");
      double ACC = 0.0, MAE=0.0, MSE=0.0, correct = 0.0, incorrect=0.0,
              curResp = 0.0, predictedResp=0.0, p = 0.0;
      StringBuffer res = new StringBuffer();
      String curStudentID="";
      StudentSkillMatrix curModel = null;
      Iterator fileiter = inputFileData.iterator();
      int ct =0;
       while(fileiter.hasNext()){
           ArrayList line = (ArrayList)fileiter.next();
           ct++;
           String sID= "";
           try{
           sID= (String)line.get(2);
           }catch (Exception ex){
              System.out.println("ERROR  "+ex.getMessage()+" ct= "+ct);
               break;
           }
           String corr = (String)line.get(3);
           String skill = (String)line.get(4);
           if(corr.equals("0"))
              curResp=0.0;
           else
              curResp=1.0;
           if(!curStudentID.equals(sID)){
              //curModel = getStudentModel(getConnection(), sID);
              curModel = (StudentSkillMatrix)studentModels.get(sID);
              curStudentID=sID;
           }
           p=curModel.getSkillVal(skill);
           predRpt.append(sID).append('\t').append(skill).append('\t').append(p).append("\r\n");
           if(p>=0.5)
               predictedResp=1.0;
           else
               predictedResp=0.0;
           if(curResp==predictedResp)
               correct++;
           else
               incorrect++;
           MAE = MAE+Math.abs(curResp-p);
           MSE = MSE + Math.pow((curResp - p), 2);
       }
      ACC = correct/(correct+incorrect);
      MAE = MAE/(correct+incorrect);
      MSE = MSE/(correct+incorrect);
      res.append("S Model").append("\r\n");
      res.append("Correct = ").append(correct).append("\r\n");
      res.append("Incorrect = ").append(incorrect).append("\r\n");
      res.append("Accuracy = ").append(ACC).append("\r\n");
      res.append("Mean Absolute Error = ").append(MAE).append("\r\n");
      res.append("Mean Squared Error = ").append(MSE).append("\r\n");
      writeReport(predRpt.toString(), "G:/courses/ML689/project/results/realdata/S_Rpredictions.txt");
      return res.toString();
    }

    public String calculateModelCM2Results(ArrayList inputFileData, TreeMap studentModels) throws Exception{
        StringBuffer predRpt = new StringBuffer();
        predRpt.append("sID").append('\t').append("skill-1").append('\t');
         predRpt.append("skill-2").append('\t').append("skill-3").append('\t').append("skill-G").append("\r\n");
      double ACC = 0.0, MAE=0.0, MSE=0.0, correct = 0.0, incorrect=0.0,
              curResp = 0.0, predictedResp=0.0, p = 0.0;
        double s1=0.0,s2=0.0,s3=0.0,g=0.0;
      StringBuffer res = new StringBuffer();
      String curStudentID="";
      StudentTwoSkillMatrix curModel = null;
      Iterator fileiter = inputFileData.iterator();
      int ct =0;
       while(fileiter.hasNext()){
           ArrayList line = (ArrayList)fileiter.next();
           ct++;
           String sID= "";
           try{
           sID= (String)line.get(2);
           }catch (Exception ex){
              System.out.println("ERROR  "+ex.getMessage()+" ct= "+ct);
               break;
           }
           String corr = (String)line.get(3);
           String skill = (String)line.get(4);
           if(corr.equals("0"))
              curResp=0.0;
           else
              curResp=1.0;
           if(!curStudentID.equals(sID)){
              //curModel = getStudentModel(getConnection(), sID);
              curModel = (StudentTwoSkillMatrix)studentModels.get(sID);
              curStudentID=sID;
           }
           //P(A|G,S)=min{P(A|G), P(A|S)}
           p=Math.min(curModel.getSkillVal(skill), curModel.getSkillVal("G"));
           if(p>=0.5)
               predictedResp=1.0;
           else
               predictedResp=0.0;
           if(curResp==predictedResp)
               correct++;
           else
               incorrect++;
           MAE = MAE+Math.abs(curResp-p);
           MSE = MSE + Math.pow((curResp - p), 2);
       }
      ACC = correct/(correct+incorrect);
      MAE = MAE/(correct+incorrect);
      MSE = MSE/(correct+incorrect);
      res.append("S,G Model").append("\r\n");
      res.append("Correct = ").append(correct).append("\r\n");
      res.append("Incorrect = ").append(incorrect).append("\r\n");
      res.append("Accuracy = ").append(ACC).append("\r\n");
      res.append("Mean Absolute Error = ").append(MAE).append("\r\n");
      res.append("Mean Squared Error = ").append(MSE).append("\r\n");

        curModel = (StudentTwoSkillMatrix)studentModels.get("317");  //317 g1 //320 g2
      s1=curModel.getSkillVal(StudentSkillMatrix.skillNames_[0]);
      s2=curModel.getSkillVal(StudentSkillMatrix.skillNames_[1]);
        s3=curModel.getSkillVal(StudentSkillMatrix.skillNames_[2]);
      g=curModel.getSkillVal("G");
      predRpt.append("52").append('\t').append(s1).append('\t');
         predRpt.append(s2).append('\t').append(s3).append('\t').append(g).append("\r\n");
      writeReport(predRpt.toString(), "G:/courses/ML689/project/results/realdata/SG_Rpredictions.txt");
      return res.toString();
    }

    public String calculateModelCDResults(ArrayList inputFileData, TreeMap studentModels) throws Exception{
        StringBuffer predRpt = new StringBuffer();

      double ACC = 0.0, MAE=0.0, MSE=0.0, correct = 0.0, incorrect=0.0,
              curResp = 0.0, predictedResp=0.0, p = 0.0, ps=0.0;
        double avgPASone=0.0, avgPASzero=0.0, avgS=0.0, pAct=0.0;
        int pAS1ct=0, pAS0ct=0;

      StringBuffer res = new StringBuffer();
      String curStudentID="";
      String qID= null;
      StudentDiscSkillMatrix curModel = null;
      Iterator fileiter = inputFileData.iterator();
      int ct =0;
       while(fileiter.hasNext()){
           ArrayList line = (ArrayList)fileiter.next();
           ct++;
           String sID= "";
           qID= (String)line.get(0);
           try{
           sID= (String)line.get(2);
           }catch (Exception ex){
              System.out.println("ERROR  "+ex.getMessage()+" ct= "+ct);
               break;
           }
           String corr = (String)line.get(3);
           String skill = (String)line.get(4);
           if(corr.equals("0"))
              curResp=0.0;
           else
              curResp=1.0;
           if(!curStudentID.equals(sID)){
              //curModel = getStudentModel(getConnection(), sID);
              curModel = (StudentDiscSkillMatrix)studentModels.get(sID);
              curStudentID=sID;
           }
           //P(S)
           ps=curModel.getSkillVal(skill);
           if(ps>=0.5){
               //P(A=1|S=1)
               p=curModel.getQS1Val(qID);
               if(p>=0.5)
                 predictedResp=1.0;
               else {
                   predictedResp=0.0;

               }
           }
           else  {
               //P(A=1|S=0)
               p=curModel.getQS0Val(qID);
               if(p>=0.5){
                 predictedResp=1.0;

               }
               else
                  predictedResp=0.0;
           }


           if(curResp==predictedResp)
               correct++;
           else
               incorrect++;
           MAE = MAE+Math.abs(curResp-p);
           MSE = MSE + Math.pow((curResp - p), 2);
       }
      ACC = correct/(correct+incorrect);
      MAE = MAE/(correct+incorrect);
      MSE = MSE/(correct+incorrect);
      res.append("Discreet Model").append("\r\n");
      res.append("Correct = ").append(correct).append("\r\n");
      res.append("Incorrect = ").append(incorrect).append("\r\n");
      res.append("Accuracy = ").append(ACC).append("\r\n");
      res.append("Mean Absolute Error = ").append(MAE).append("\r\n");
      res.append("Mean Squared Error = ").append(MSE).append("\r\n");






      return res.toString();
    }
    public void getD(TreeMap studentModels) throws Exception{
         StringBuffer predRpt = new StringBuffer();
        String sk ="4.N.10";
         double psk=0.0, paonesone=0.0,paoneszero=0.0;
         double avgPASone=0.0, avgPASzero=0.0, avgS=0.0, pAct=0.0;
        int pAS1ct=0, pAS0ct=0;
    String qIDstring="";

int[][] times = new int[2][qidsi.length];
       Iterator miter = studentModels.keySet().iterator();
       while(miter.hasNext()){
           String sID = (String)miter.next();
        StudentDiscSkillMatrix  curModel = (StudentDiscSkillMatrix)studentModels.get(sID);

           psk=curModel.getSkillVal(sk);
           avgS+=psk;

           for(int i=0;i<qidsi.length;i++){
               String qID=qidsi[i];
             try{
                 if(!qIDstring.contains(qID))
                    qIDstring+=qID+" ";
               paonesone = curModel.getQS1Val(qID);
               paoneszero =curModel.getQS0Val(qID);
               if(psk>=0.5){
                 //P(A=1|S=1)
                 if(paonesone<0.5)
                    pAS1ct++;
                   times[0][getSkillIndex(qID)]++;
                }
                else  {
                  //P(A=1|S=0)
                  if(paoneszero>=0.5)
                        pAS0ct++;
                        times[1][getSkillIndex(qID)]++;
                }
                avgPASone+=curModel.getQS1Val(qID);
               avgPASzero+=curModel.getQS0Val(qID);
                  pAct++;
             }catch(Exception e){}
       }


            }
 if(pAct<1)
 pAct=1;
       //  predRpt.append("qIDs ").append(qIDstring).append("\r\n");
       // predRpt.append("average P(A=1|S=1) ").append(avgPASone/pAct).append("\r\n");
      //  predRpt.append("average P(A=1|S=0) ").append(avgPASzero/pAct).append("\r\n");
      //  predRpt.append("average P(S=1) ").append(avgS/pAct).append("\r\n");
       //  predRpt.append(" ").append(avgS/pAct).append("\r\n");
       //  predRpt.append("times P(A=1|S=1) < .5 when S=1 ").append(pAS1ct).append("\r\n");
       //   predRpt.append("times P(A=1|S=0) > .5 when S=0 ").append(pAS0ct).append("\r\n");
       //   predRpt.append("count for 4.N.10 ").append(pAct).append("\r\n");

    for(int i=0;i<qidsi.length;i++)
        predRpt.append("qID ").append(qidsi[i]).append("\t");
        predRpt.append("\r\n");
      for(int i=0;i<qidsi.length;i++)
        predRpt.append("a1s1 ").append(times[0][i]).append("\t");
      predRpt.append("\r\n");
       for(int i=0;i<qidsi.length;i++)
        predRpt.append("a1s0 ").append(times[1][i]).append("\t");
        predRpt.append("\r\n");


       writeReport(predRpt.toString(), "G:/courses/ML689/project/results/realdata/DDIFFpredictions.txt");

    }

     public int getSkillIndex(String name){
     int index = -1;
     for(int i=0;i<qidsi.length;i++){
        if(name.equals(qidsi[i]))
              index=i;
       }
     return index;
   }
    public void createRandomEvaluation(String trainFile,
                                      String testFile, String outFile)throws Exception{
       // ArrayList traindata = new ArrayList();
       // readDataFile(trainFile, traindata);
       // formality.Util.model.BetaStudentModel module = new formality.Util.model.BetaStudentModel();

       // module.processData(traindata);
       // String sid="350";
        // StudentSkillMatrix ssm = (StudentSkillMatrix)module.getAllStudentModels().get(sid);
       // String sm = ssm.debugPrint();
       //  writeReport(sm, "G:/courses/ML689/project/results/StudentSkillMatrix"+sid+".txt");
        ArrayList testdata = new ArrayList();
        readDataFile(testFile, testdata);
        String res = calculateRandomResults(testdata);
        res=res+"test file: "+testFile+"\r\n";
        writeReport(res, outFile);
    }

    public void createModelEvaluation(String trainFile,
                                      String testFile, String outFile)throws Exception{
        ArrayList traindata = new ArrayList();
        readDataFile(trainFile, traindata);
        formality.Util.model.BetaStudentModel m = new formality.Util.model.BetaStudentModel();

        m.processData(traindata);
       // String sid="350";
        // StudentSkillMatrix ssm = (StudentSkillMatrix)module.getAllStudentModels().get(sid);
       // String sm = ssm.debugPrint();
       //  writeReport(sm, "G:/courses/ML689/project/results/StudentSkillMatrix"+sid+".txt");
        ArrayList testdata = new ArrayList();
        readDataFile(testFile, testdata);
        String res = calculateResults(testdata, m.getAllStudentModels());
        res=res+"train file: "+trainFile+"\r\n"+"test file: "+testFile+"\r\n";
        writeReport(res, outFile);
    }
     //two skills S,G
        public void createModelCM2Evaluation(String trainFile,
                                      String testFile, String outFile)throws Exception{
        ArrayList traindata = new ArrayList();
        readDataFile(trainFile, traindata);
        formality.Util.model.BetaTwoStudentModel m = new formality.Util.model.BetaTwoStudentModel();

        m.processData(traindata);
      // String sid="350";
        // StudentTwoSkillMatrix ssm = (StudentTwoSkillMatrix)module.getAllStudentModels().get(sid);
       // String sm = ssm.debugPrint();
       //  writeReport(sm, "G:/courses/ML689/project/results/StudentTwoSkillMatrix"+sid+".txt");
        ArrayList testdata = new ArrayList();
        readDataFile(testFile, testdata);
        String res = calculateModelCM2Results(testdata, m.getAllStudentModels());
        res=res+"train file: "+trainFile+"\r\n"+"test file: "+testFile+"\r\n";
        writeReport(res, outFile);
    }

    public void createModelCDEvaluation(String trainFile,
                                      String testFile, String outFile)throws Exception{
        ArrayList traindata = new ArrayList();
        readDataFile(trainFile, traindata);
        formality.Util.model.BetaDiscSkillStudentModel m = new formality.Util.model.BetaDiscSkillStudentModel();

        m.processData(traindata);
       // String sid="350";
        // StudentDiscSkillMatrix ssm = (StudentDiscSkillMatrix)module.getAllStudentModels().get(sid);
       // String sm = ssm.debugPrint();
       //  writeReport(sm, "G:/courses/ML689/project/results/StudentDiscSkillMatrix"+sid+".txt");
        ArrayList testdata = new ArrayList();
        readDataFile(testFile, testdata);
        getD(m.getAllStudentModels());
       // String res = calculateModelCDResults(testdata, module.getAllStudentModels());
       // res=res+"train file: "+trainFile+"\r\n"+"test file: "+testFile+"\r\n";
        //writeReport(res, outFile);
    }
   /*********************************************************************************************************
    * ********************************************************************************************************/
  public void createPredConvergence(String trainFile, String outFile)throws Exception{
        StringBuffer res = new StringBuffer();
        ArrayList traindata = new ArrayList();
       ArrayList curtraindata;
       formality.Util.model.BetaStudentModel m;
        readDataFile(trainFile, traindata);
       res.append(1).append('\t').append(0.5).append("\r\n");
       for(int i=1; i<=traindata.size();i++){
          curtraindata = getTrainingSet(i, traindata);
          m = new formality.Util.model.BetaStudentModel();
          m.processData(curtraindata);
          StudentSkillMatrix ssm = (StudentSkillMatrix)m.getAllStudentModels().get("319"); //317 g1 //320 g2
          double p = ssm.getSkillVal("4.N.10");
          res.append(i+2).append('\t').append(p).append("\r\n");
       }
       writeReport(res.toString(), outFile);
    }

     public void createPred2Convergence(String trainFile, String outFile)throws Exception{
        StringBuffer res = new StringBuffer();
        ArrayList traindata = new ArrayList();
       ArrayList curtraindata;
       formality.Util.model.BetaTwoStudentModel m;
        readDataFile(trainFile, traindata);
       res.append(1).append('\t').append(0.5).append('\t').append(0.5).append('\t');
       res.append(0.5).append('\t').append(0.5).append("\r\n");
       for(int i=1; i<=traindata.size();i++){
          curtraindata = getTrainingSet(i, traindata);
          m = new formality.Util.model.BetaTwoStudentModel();
          m.processData(curtraindata);
          StudentTwoSkillMatrix ssm = (StudentTwoSkillMatrix)m.getAllStudentModels().get("319");
          double p1 = ssm.getSkillVal("4.N.1");
          double p2 = ssm.getSkillVal("4.N.3");
          double p3 = ssm.getSkillVal("4.N.10");
          double p4 = ssm.getSkillVal("G");
          res.append(i+2).append('\t').append(p1).append('\t').append(p2).append('\t');
          res.append(p3).append('\t').append(p4).append('\t').append("\r\n");
       }
       writeReport(res.toString(), outFile);
    }

  private ArrayList getTrainingSet(int num, ArrayList traindata){
      ArrayList curdata = new ArrayList();
      for(int i=0; i<num;i++){
          curdata.add(traindata.get(i));
     }
    return curdata;
  }
    /**
    * @param args
    */
     public static void main(String[] args) {
         StudentModelEvaluator r = new StudentModelEvaluator();
          try{
           String respath="G:/courses/ML689/project/results/realdata/";
           String datapath="G:/courses/ML689/project/4mality_data/";
           String trainFile = datapath+args[0];
           String testFile = datapath+args[1];
           String outFile = respath+args[2];
          //r.createRandomEvaluation(trainFile, testFile, outFile);
        // r.createModelEvaluation(trainFile, testFile, outFile);
         //r.createModelCM2Evaluation(trainFile, testFile, outFile);
          r.createModelCDEvaluation(trainFile, testFile, outFile);
          //   r.createPred2Convergence(trainFile, outFile);
        // r.createPred2Convergence(trainFile, outFile);
         } catch(Exception ex){
            System.out.println("ERROR  "+ex.getMessage());
         }
      }
  }


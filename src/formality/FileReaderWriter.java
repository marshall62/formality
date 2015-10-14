package formality;

import formality.Util.DataTuple;

import java.util.Vector;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FileReaderWriter {

  int eventCt_;
  protected static final String theDelimiters_=".,?\":;1234567890 \n\t\r";
  protected static final String header_="Title";


  public FileReaderWriter(){
    eventCt_ = 0;
  }
     /**
      * tokenize the line and return a vector
   *  @param inputLine a String
   * @return Vector
   * @throws Exception
   */
   protected Vector processLine(String inputLine) throws Exception
    {
      StringTokenizer tokens =new StringTokenizer(inputLine);
      Vector lineVec = new Vector();
            while (tokens.hasMoreTokens())
            {
              String word=tokens.nextToken();
              lineVec.addElement(word);
            }
      return lineVec;
    }

    //these files are lines of First Middle Last
     protected DataTuple processRosterLine(String cID, String inputLine) throws Exception
    {
      StringTokenizer tokens =new StringTokenizer(inputLine);
      int ct=1;
      DataTuple dt=new DataTuple();
      StringBuffer login = new StringBuffer();
            while (tokens.hasMoreTokens())
            {
              String word=tokens.nextToken();
              if(ct==1){
                String fInit=word.substring(0,1);
                login.append(fInit.toLowerCase());
                dt.setSecond(word.trim());
              }
              if(ct==3)
                  login.append(word.toLowerCase());
                  dt.setThird(word.trim());
              ct++;
            }
      login.append(cID);
      dt.setFirst(login.toString());
      return dt;
    }

     //expect fname, space, lname, tab, login
   protected DataTuple processNamesAndLoginRosterLine(String cID, boolean prependCid,
                                                    String inputLine) throws Exception
    {
      StringTokenizer tokens =new StringTokenizer(inputLine);
      DataTuple dt=new DataTuple();
      int index=1;
      while (tokens.hasMoreTokens()){
          String token = tokens.nextToken().trim();
          if(index==1)
            dt.setSecond(token);
          else if(index==2)
            dt.setThird(token);
          else if(index==3)
            if(prependCid)
              dt.setFirst(cID+token);
            else
              dt.setFirst(token);
          else
              throw new Exception("error in FileReaderWriter.processNamesAndLoginRosterLine: index out of bounds "+index
              +" input "+inputLine);
          index++;
      }
      return dt;
    }
        //expect only a login- append the cID to the front
     protected DataTuple processLoginOnlyRosterLine(String cID,
                                                    String inputLine) throws Exception
    {
      StringTokenizer tokens =new StringTokenizer(inputLine);
      DataTuple dt=new DataTuple();
      while (tokens.hasMoreTokens()){
          String login = tokens.nextToken();
          login = cID+login.trim();
          dt.setFirst(login.toString());
          dt.setSecond(" ");
          dt.setThird(" ");
      }
      return dt;
    }
   /**  given a file name, will open the file (create it if it doesn't already exist),
*    write the vector of strings to file with a PrintWriter, will overwrite any previous output.
*/

public void writeReport(String outputFileName, Vector report) throws Exception
{
     File outFile=new File(outputFileName);
     FileOutputStream outFileStream=new FileOutputStream(outFile, true);//not append=false
     PrintWriter outStream=new PrintWriter(outFileStream);
     //write the vector
     for(int i=0;i<report.size();i++)
        outStream.println((String)report.get(i));
     outStream.close();
}


   /**  given a file name, will open the file (create it if it doesn't already exist),
*    write the vector of strings to file with a PrintWriter, will overwrite any previous output.
*/

public void writeHtmlFile(String outputFileName, StringBuffer page) throws Exception
{
     File outFile=new File(outputFileName);
     FileOutputStream outFileStream=new FileOutputStream(outFile);
     PrintWriter outStream=new PrintWriter(outFileStream);
     outStream.print(page.toString());
     outStream.close();
}


//////////////// BINARY  /////////////////////////////////////////////////////////////////////////
   /////////////////////////////////////////////////////////////////////////////////////////////

 //creates, opens, writes an objects to seq file as binary data
 public void writeObjectToBinaryFile(String fileName, Object obj)
 {
    ObjectOutputStream output=null;
    File theFile=null;
       if ( fileName == null || fileName.equals( "" ) )
         {  System.err.println("error: writeObjectToBinaryFile invalid filename");
            System.exit(1);}
          // Open the file
          try {theFile=new File(fileName);
               output = new ObjectOutputStream(new FileOutputStream(theFile) );}
          catch ( IOException e ) {
                   System.err.println("error: writeObjectToBinaryFile could not open file "+ fileName);
                   System.exit(1); }
         ////////////////////write objects to file//////////////////////////
                try {  output.writeObject(obj);
                       output.flush();}
                 catch( IOException ex ) {
                   System.err.println("error writing to file  writeObjToBinaryFile "+ fileName);
                   System.exit(1);}
         ///////////////////////////////close////////////////////////////////
            try {
                output.close();
            }
            catch( IOException ex ) {
                   System.err.println("error: writeOgjToBinaryFile could not close file "+ fileName);
                   System.exit(1);}
   }

 //opens,reads object from a seq file
 public Object readObjectFromBinaryFile(String fileName) throws Exception
 {
      Object record =null;
      File theFile=null;
      ObjectInputStream input=null;
        if ( fileName == null || fileName.equals( "" ) ){
         System.err.println("error:readObjectFromBinaryFile invalid filename");
         System.exit(1);}
                 // Open the file
                 try {theFile=new File(fileName);
                      input = new ObjectInputStream( new FileInputStream(theFile) ); }
                  catch ( IOException e ) {
                  System.err.println("error: readObjectFromBinaryFile could not open file "+ fileName);
                 System.exit(1);}
         ////////////////////////////read object///////////////////////////////
              try{ record = input.readObject();}
              catch ( EOFException eofex ) {input.close();}
               catch ( ClassNotFoundException cnfex ) {
                 System.err.println("error:readObjectFromBinaryFile unable to create object");
                 System.exit(1);
               }
               catch ( IOException ioex ) {
                         System.err.println("error:readObjectFromBinaryFile during read from file "+ fileName);
                 System.exit(1);
               }
         input.close();
         return record;
 }


//creates, opens, writes a vector of objects to seq file as binary data
public void writeVecToBinaryFile(String fileName, Vector dataObjects)
{
   ObjectOutputStream output=null;
   File theFile=null;
      if ( fileName == null || fileName.equals( "" ) )
        {
                 System.err.println("error: writeVecToBinaryFile invalid filename");
        System.exit(1);
        }
         // Open the file
         try {
        theFile=new File(fileName);
                  output = new ObjectOutputStream(new FileOutputStream(theFile) );
         }
         catch ( IOException e ) {
                  System.err.println("error: writeVecToBinaryFile could not open file "+ fileName);
                  System.exit(1);
         }

        ////////////////////write objects to file//////////////////////////
               try {
        for(int i=0;i<dataObjects.size();i++)
        {
            //  Dimension dim=(Dimension)dataObjects.get(i);
                 Object record=dataObjects.get(i);
                      output.writeObject(record);
                      output.flush();
                  }
               }
                catch( IOException ex ) {
                  System.err.println("error writing to file  writeVecToBinaryFile "+ fileName);
                  System.exit(1);
          }

        ///////////////////////////////close////////////////////////////////
           try {
               output.close();
           }
           catch( IOException ex ) {
                  System.err.println("error: writeVecToBinaryFile could not close file "+ fileName);
                  System.exit(1);
          }

  }

//opens,reads objects from a seq file into a vector
public void readVecFromBinaryFile(String fileName, Vector outputVec) throws Exception
{
           File theFile=null;
        ObjectInputStream input=null;


            if ( fileName == null || fileName.equals( "" ) )
        {
        System.err.println("error:readVecFromBinaryFile invalid filename");
        System.exit(1);
        }

                // Open the file
                try {
               theFile=new File(fileName);
                         input = new ObjectInputStream( new FileInputStream(theFile) );

                 }
                 catch ( IOException e ) {
                           System.err.println("error: readVecFromBinaryFile could not open file "+ fileName);
                System.exit(1);
                 }

        ////////////////////////////read objects///////////////////////////////

     // BankAccountRecord record;
        //Dimension dim;
      // input the values from the file
    try{
        while(true){


        Object  record = input.readObject();
        if(record==null)
                return;
         outputVec.addElement(record);
        }

      }
             catch ( EOFException eofex ) {input.close();}


                 catch ( ClassNotFoundException cnfex ) {
                System.err.println("error:readVecFromBinaryFile unable to create object");
                System.exit(1);
              }
              catch ( IOException ioex ) {
                        System.err.println("error:readVecFromBinaryFile during read from file "+ fileName);
                System.exit(1);
              }

        input.close();
}

  // adds each row of the file to the array
   public void readFileToArray(String inputFile, ArrayList<String> data) throws Exception {
      if (inputFile == null || inputFile.equals( "" ) )
          throw new Exception( "Invalid File Name: "+inputFile );
      else {
          String inputLine;
          FileReader fileReader;
          File inFile=new File(inputFile);
          fileReader=new FileReader(inFile);
          BufferedReader bufReader =new BufferedReader(fileReader);
          try{
             while(true){
              inputLine = bufReader.readLine();
              if(inputLine != null){
                data.add(inputLine);
              }
              else break;
             }
           }
          catch ( EOFException eofex ) { bufReader.close(); }
                 bufReader.close();
          }
     }
   }


package formality.parser;

/**
 * Created by IntelliJ IDEA.
 * User: gordon
 * Date: Aug 16, 2005
 * Time: 11:37:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class StringEncoder {

    final static String usage_ = "StringEncoder source pad\n";

  public StringEncoder() {
  }

  public static String encode(String src, String pad)
  {
    int minc = (int)'!'; // 33
    int maxc = (int)'~'; // 126
    int range = maxc - minc + 1;
    StringBuffer tmp = new StringBuffer(src.length() + 32);
    while(pad.length() < src.length())
    {
      pad += pad;
    }
    for(int i=0; i<src.length(); ++i)
    {
      int srcC = (int)src.charAt(i);
      int padC = (int)pad.charAt(i);
      int c = (srcC - minc) + (padC - minc);
      if(c > range)
        c -= range;
      c += minc;
      tmp.append((char)c);
    }
    return tmp.toString();
  }

  public static void main(String[] args) {
		// get each value
		if(args.length < 2)
		   System.out.println(usage_);
    String action = args[0];
		String src = args[1];
		String pad = args[2];
    StringEncoder encoder = new StringEncoder();
    if(action.equalsIgnoreCase("encode"))
    {
      System.out.println("encoded:");
      String encoded = encoder.encode(src, pad);
      System.out.println(encoded);
      System.out.println("decoded:");
      String decoded = encoder.decode(encoded, pad);
      System.out.println(decoded);
    }
    else
    {
      System.out.println("decoded:");
      String decoded = encoder.decode(src, pad);
      System.out.println(decoded);
      System.out.println("encoded:");
      String encoded = encoder.encode(decoded, pad);
      System.out.println(encoded);
    }
  }
    public static String decode(String src)
     {
       String pad="whendoweeat";
       return decode(src, pad);
    }

    public static String decode(String src, String pad)
     {
       int minc = (int)'!'; // 33
       int maxc = (int)'~'; // 126
       int range = maxc - minc + 1;
       StringBuffer tmp = new StringBuffer(src.length() + 32);
       while(pad.length() < src.length())
       {
         pad += pad;
       }
       for(int i=0; i<src.length(); ++i)
       {
         int srcC = (int)src.charAt(i);
         srcC -= minc;
         int padC = (int)pad.charAt(i);
         padC -= minc;
         int c = srcC - padC;
         if(c < 0)
           c += range;
         c += minc;
         tmp.append((char)c);
       }
       return tmp.toString();
     }

}

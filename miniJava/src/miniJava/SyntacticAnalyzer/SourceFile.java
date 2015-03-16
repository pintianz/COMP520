package miniJava.SyntacticAnalyzer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SourceFile {

  static final char eolUnix = '\n';
  static final char eolWindows = '\r';
  static final char eot = '\u0000';

  InputStream sourceFile;
  java.io.BufferedInputStream source;
  int currentLine;

  public SourceFile(InputStream inputStream) {
	  source = new BufferedInputStream(inputStream);
	  currentLine = 1;
  }

  char getSource() {
    try {
      int c = source.read();
      if (c == -1) {
        c = eot;
      } else if (c == eolUnix || c == eolWindows) {
          currentLine++;
      }
      return (char) c;
    }
    catch (java.io.IOException s) {
      return eot;
    }
  }
  
  int getSourceInt() {
	    try {
	      int c = source.read();
	      if (c == eolUnix) {
	          currentLine++;
	      }  else if(c == eolWindows){
	    	  if(sourcePeek()==eolUnix) source.read();
	          currentLine++;
	      } 
	      return c;
	    }
	    catch (java.io.IOException s) {
	      return -1;
	    }
	  }
  
  char sourcePeek() {
	  source.mark(5);
		char c;
		try {
			c = (char)source.read();
			source.reset();
			return c;
		} catch (IOException e) {
			return '~';
		}
	}
  
  String sourcePeekStr(int len) {
	  source.mark(len+5);
		char c;
		String s = "";
		try {
			for(int i=0; i< len; i++){
				c = (char)source.read();
				s = s+c;
			}
			source.reset();
			return s;
		} catch (IOException e) {
			return "";
		}
	}

  int getCurrentLine() {
    return currentLine;
  }
}

package miniJava.SyntacticAnalyzer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SourceFile {

  static final char eol = '\n';
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
      } else if (c == eol) {
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

  int getCurrentLine() {
    return currentLine;
  }
}

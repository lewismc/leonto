/**
 * 
 */
package uk.ac.gcu.ebe.legalontoconstruct;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.writer.JSONWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.dom4j.Branch;

/**
 * <p>The main method in {@link ConstructJob} simply accepts one 'file' input 
 * parameter, from which we extract triples.</p>
 * <p>If the class is invoked without any parameters it will display a
 * useage message.</p>
 * @author lewismc
 */
public class ConstructJob {

  public static void main(String [] args) throws Exception{
    String filePath = null;

    if(args.length < 2) {
      System.err.println("Usage: ConstructJob -file </path/to/file>");
    }
    for(int i = 0; i < args.length; i++) {
      if("-file".equals(args[i])){
        try {
          filePath = args[++i];
        } catch (Exception e) {
          e.printStackTrace();
        }
      } runner(filePath);
    }
  }

  public static void runner(String fileURIString) throws IOException, TripleHandlerException, URISyntaxException {

    Any23 runner = new Any23();
    DocumentSource source = runner.createDocumentSource("file:" + fileURIString);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TripleHandler handler = new JSONWriter(baos);
    final ExtractionParameters extractionParameters = ExtractionParameters.newDefault();
    extractionParameters.setFlag("any23.extraction.head.meta", true);
    try {
      runner.extract(extractionParameters, source, handler);
    } catch (ExtractionException e) {
      e.printStackTrace();
    } finally {
      handler.close();
    }
    String out = baos.toString("UTF-8");
    try {
      BufferedWriter bf = new BufferedWriter(new FileWriter("construct.txt"));
      System.out.println("Wrote file out.");
      bf.write(out);
      bf.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

}

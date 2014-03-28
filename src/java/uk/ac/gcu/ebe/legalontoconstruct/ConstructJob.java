/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * <p>The main method in {@link ConstructJob} accepts two input 
 * parameters; namely '-file' and '/path/to/file'.</p>
 * <p>If the class is invoked without any parameters it will display a
 * usage message.</p>
 * <p>The extracted triples are written as a text file using a standard
 * {@link java.io.FileWriter.FileWriter}. This file takes the form
 * 'construct.txt' and is written to the top level directory of this
 * project.</p>
 * <p>Example files can be found in the <code>/resources</code> directory
 * of the top level directory of this project.</p>
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
      System.out.println("=== Starting Leonto extraction ===\n");
      runner.extract(extractionParameters, source, handler);
    } catch (ExtractionException e) {
      e.printStackTrace();
    } finally {
      handler.close();
    }
    String out = baos.toString("UTF-8");
    try {
      BufferedWriter bf = new BufferedWriter(new FileWriter("construct.txt"));
      bf.write(out);
      bf.close();
      System.out.println("=== Finished Leonto extraction ===");
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

}

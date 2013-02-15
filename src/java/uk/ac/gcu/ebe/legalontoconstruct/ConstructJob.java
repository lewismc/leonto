/**
 * 
 */
package uk.ac.gcu.ebe.legalontoconstruct;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.http.HTTPClient;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main method in {@link ConstructJob} simply accepts one 'url' input 
 * parameter, from which we extract triples.
 * @author lewismc
 */
public class ConstructJob implements Tool {
	
	private static final Logger log = LoggerFactory.getLogger(ConstructJob.class);

	
	public static void main(String [] args) throws Exception{
		final int res = ToolRunner.run(new ConstructJob(), args);
		System.exit(res);
	}
	private Configuration conf;
	/**
	 * @param args
	 */
	public int run(String[] args) throws Exception{
		String url = null;
		String usage = "Usage: ConstructJob -url <url>";
		
		if(args.length < 1) {
			System.err.println(usage);
			return -1;
		}
		for(int i = 0; i < args.length; i++) {
			if("-url".equals(args[i])){
				getConf().set("seed.url", args[i++]);
			}
			url = args[i];
		}
		return runner(url);
	}
	
	public int runner(String url) throws IOException, TripleHandlerException, URISyntaxException {
		String u = url;
		log.info("ConstructJob: Starting...");
		Any23 runner = new Any23();
		runner.setHTTPUserAgent("<==legalOntoConstruct==>");
		HTTPClient httpClient = runner.getHTTPClient();
		DocumentSource source = null;
	  source = new HTTPDocumentSource(httpClient, u);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// TODO make the writer switch configurable 
		TripleHandler handler = new NTriplesWriter(baos);
		try {
		log.info("ConstructJob: Extracting from: '" + u + "'");
		runner.extract(source, handler);
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
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		log.info("ConstructorJob: Finished!");
		return 0;
	}
	public Configuration getConf() {
	  return conf;
  }
	public void setConf(Configuration conf) {
	  this.conf = conf;
	  
  }

}

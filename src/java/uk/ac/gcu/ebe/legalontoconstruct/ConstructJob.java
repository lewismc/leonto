/**
 * 
 */
package uk.ac.gcu.ebe.legalontoconstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

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
 * @author lewismc
 *
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
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TripleHandler handler = new NTriplesWriter(out);
		try {
		log.info("ConstructJob: Extracting from: '" + u + "'");
		runner.extract(source, handler);
		} catch (ExtractionException e) {
	    e.printStackTrace();
    } finally {
		handler.close();
		}
		String n3 = out.toString("UTF-8");
		return 0;
	}
	public Configuration getConf() {
	  return conf;
  }
	public void setConf(Configuration conf) {
	  this.conf = conf;
	  
  }

}

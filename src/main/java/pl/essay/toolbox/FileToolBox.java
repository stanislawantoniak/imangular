package pl.essay.toolbox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

public class FileToolBox {

	public final static void saveStringAsFile(String s, String fileName){
		FileOutputStream is = null;
		OutputStreamWriter osw = null;
		Writer w = null;
		try {
			File saveFile = new File(fileName);
			is = new FileOutputStream(saveFile);
			osw = new OutputStreamWriter(is, "UTF-8");    
			w = new BufferedWriter(osw);
			w.write(s);
			w.close();
			osw.close();
			is.close();
		} catch (IOException e) {

		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public final static String readFileAsString(String filename ) {

		FileInputStream inputStream = null;

		String output = "";

		try {
			inputStream = new FileInputStream(filename);
			output = IOUtils.toString(inputStream,"UTF-8");
		} catch (FileNotFoundException e) {
			System.out.println("File does not  exist " + filename );
		} catch (IOException e) {
			System.out.println("Problem reading file " + filename );
		} finally {
			if (inputStream!=null)
				try {
					inputStream.close();
				} catch (IOException e) {
					System.out.println("Problem reading file " + filename );
				}
		}

		return output;
	}

	public final static String listFilesInCatalog(String baseCat){
		String list = "";
		File catalog = new File(baseCat);
		if (catalog.exists() && catalog.isDirectory()){
			System.out.println("catalog exists:: "+baseCat);
			String files[] = catalog.list();
			for(String file: files)
				list += file+"\n";
		}
		return list;
	}
}

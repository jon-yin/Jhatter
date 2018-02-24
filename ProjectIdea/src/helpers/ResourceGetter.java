package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.CCommand;

/**
 * Handles getting resources from the resource folder such as reading file data
 * or holding useful global constants.
 * 
 * @author Jonathan Yin
 *
 */
public class ResourceGetter {

	public final static String translations = "/Commands.txt";
	public final static String help = "/Help.txt";

	public static Map<String, CCommand> getTrans() throws IOException {
		//Path cwd = Paths.get("");
		//System.out.println(cwd.toAbsolutePath());
		InputStream fileText = ResourceGetter.class.getResourceAsStream(translations);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileText));
		List<String> readTranslations = new ArrayList<>();
		String curLine = null;
		while ((curLine = reader.readLine()) != null)
		{
			readTranslations.add(curLine);
		}
		CCommand[] commands = CCommand.values();
		Map<String, CCommand> map = new HashMap<>();
		for (int i = 0; i < readTranslations.size(); i++) {
			map.put(readTranslations.get(i), commands[i + 1]);
		}
		return map;
	}

	public static List<String> getHelpText() throws IOException
	{
		InputStream fileText = ResourceGetter.class.getResourceAsStream(help);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileText));
		List<String> helpLines = new ArrayList<>();
		String curLine = null;
		while ((curLine = reader.readLine()) != null)
		{
			helpLines.add(curLine);
		}
		return helpLines;

	}

}

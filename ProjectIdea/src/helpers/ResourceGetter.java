package helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public final static String translations = "resources/Commands.txt";
	public final static String help = "resources/Help.txt";

	public static Map<String, CCommand> getTrans() throws IOException {

		File f = new File(translations);
		Path path = Paths.get(f.toURI());
		List<String> translations = Files.readAllLines(path);
		CCommand[] commands = CCommand.values();
		Map<String, CCommand> map = new HashMap<>();
		for (int i = 0; i < translations.size(); i++) {
			map.put(translations.get(i), commands[i + 1]);
		}
		return map;
	}

	public static List<String> getHelpText() throws IOException
	{
		File f = new File(help);
		Path path = Paths.get(f.toURI());
		return Files.readAllLines(path);
	}

}

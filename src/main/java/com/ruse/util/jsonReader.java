package com.ruse.util;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class jsonReader {
	
	private static int count = 0;
	
	//file.getParentFile().setWritable(true);
	
	
	public static void start() {
		
		try(Stream<Path> paths = Files.walk(Paths.get("./data/saves/characters/difficulties/"))) {
			System.out.println("Player ID,Playtime,Difficulty,Username");
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					//System.out.println(filePath.toString().replaceAll(" ", "\\ "));
					handleJson(filePath);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void handleJson(Path path) {
		
		File file = path.toFile();
		
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);

			//this  alows for easy export to .csv
			if (reader.has("total-play-time-ms") && reader.get("total-play-time-ms").getAsLong() >= 3600000 && reader.has("difficulty")) {
				count++;
				System.out.println(count + ","+
				Misc.getTimeDurationBreakdown(reader.get("total-play-time-ms").getAsLong())+
				","+Misc.capitalizeString(reader.get("difficulty").getAsString())+","+reader.get("username").getAsString());
			}
			
			
			
			
			
			/*String difficulty = builder.fromJson(reader.get("difficulty"), String.class);
			int id = reader.get("total-play-time-msd").getAsInt();
			System.out.println("difficulty = "+difficulty+", playtime = "+id);
			difficulty = null;
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

}

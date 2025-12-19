package server;

import java.util.ArrayList;
import java.util.Arrays;

public class RequestHandler {

	private ArrayList<String> parseRequest(String request) {
		return new ArrayList<>(Arrays.asList(request.trim().split("&")));
	}
	
	public String processRequest(String request) {
		ArrayList<String> params = parseRequest(request);

		switch (params.get(0)) {
			case "000" -> {
				return "Pong";
                }

			case "001" -> {
				return "001&"+params.get(1);
			}

			case "069" ->{
				return "069&Nice";
			}
			default -> throw new AssertionError();
		}


	}
}

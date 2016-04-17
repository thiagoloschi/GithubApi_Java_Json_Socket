package com.github.thiagoloschi;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class GithubApi {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String mensagem = "", user="";
		String[] flrws;

		System.out.println("Please inform the Github's username: ");
		user = scanner.nextLine();

		String address = "https://api.github.com/users/" + user;

		scanner.close();

		user = connect(address);

		if (user==null)
			return;
		else{

			System.out.println("\nHere is all the data found: " + user+"\n");

			JSONObject json = parser(user);
			System.out.println("User: " + json.get("login") + "\nFull Name: " + json.get("name") + "\nFrom: " + json.get("location") + 
					"\nMember Since: "+ json.get("created_at").toString().substring(0, 4) +"\n");  
			if( Integer.parseInt(json.get("public_repos").toString())>0) {

				mensagem = connect(json.get("repos_url").toString()); 
				//Here you can use JSONArray to parse an array of Json objects, in this case, I chose to create and adapt my own array of JSON objects using split() and replace();
				mensagem = mensagem.replace("[", "").replace("]","");
				mensagem = mensagem.replace("},{", "}-x-{");
				flrws = mensagem.split("-x-");

				System.out.println(flrws.length + " Public Repositories:");

				JSONObject repo;

				for (int i = 0; i < flrws.length; i++) {
					repo = parser(flrws[i]);
					System.out.println("\t\t"+repo.get("name") + " (" + repo.get("language")+")");
				}
			}else
				System.out.println("No public repositories.");

			if( Integer.parseInt(json.get("followers").toString())>0) {

				System.out.println("\n" + json.get("followers")  + " followers:");
				mensagem = connect(json.get("followers_url").toString());

				//formating message
				mensagem = mensagem.replace("[", "").replace("]","");
				mensagem = mensagem.replace("},{", "}-x-{");
				flrws = mensagem.split("-x-");

				for (int i = 0; i < flrws.length; i++) {
					json = parser(flrws[i]);
					user = connect(json.get("url").toString());
					json = parser(user);

					System.out.println("\t\t" + json.get("login") + "\n\t\t\tFull Name: " + json.get("name") + "\n\t\t\tFrom: " + json.get("location") + 
							"\n\t\t\tMember Since: "+ json.get("created_at").toString().substring(0, 4) +"\n\t\t\t" + json.get("public_repos") + " repositories\n");
				}

			}else
				System.out.println("No followers.");
		}
	}
	public static String connect(String address){
		URL url;
		String mensagem = "";
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.254", 8080));
		try {
			url = new URL(address);
			URLConnection con;
			BufferedReader input;
			try {
				con = url.openConnection(); //con = url.openConnection(proxy);
				input = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8")
						);
				String line;
				StringBuilder source = new StringBuilder();
				while((line = input.readLine()) != null)
					source.append(line);
				input.close();

				mensagem = source.toString();



			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//e.printStackTrace();
				System.err.println("\n\nUser not found.");
				return null;
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.err.println("\n\nToo many requests from this IP. Wait and try again later.");
				return null;
			}


		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();			
			return null;

		}
		return mensagem;
	}

	public static JSONObject parser(String mensagem){
		JSONParser parser = new JSONParser();
		JSONObject json = null;

		try {
			json = (JSONObject) parser.parse(mensagem);			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public static JSONArray parseArray(String data){
		JSONParser parser = new JSONParser();
		JSONArray jsons = null;

		try {
			jsons = (JSONArray) parser.parse(data);			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsons;
	}

}

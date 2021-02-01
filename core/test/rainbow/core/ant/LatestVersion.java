package rainbow.core.ant;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LatestVersion {

	public static void main(String[] args) throws IOException, InterruptedException {
		long time = System.currentTimeMillis();
		HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						"https://github.com/jinghui70/rainbow/releases/download/v5.0.50.20210113095455/dev.zip"))
				.GET().build();

		Path file = Paths.get("dev.zip");
		client.send(request, HttpResponse.BodyHandlers.ofFile(file));
		System.out.println(System.currentTimeMillis() - time);
//		Path file = Paths.get("rainbow.version");
//		String old = "";
//		if (Files.exists(file))
//			old = Files.readString(file);
//		System.out.println(old);
//		HttpClient client = HttpClient.newHttpClient();
//		HttpRequest request = HttpRequest.newBuilder()
//				.setHeader("Authorization", "Bearer 0fc6533cdbcb854cf3eb41c431c74d1589adf15a")
//				.uri(URI.create("https://api.github.com/graphql"))
//				.POST(BodyPublishers.ofString(
//						"{\"query\":\"{repository(owner:\\\"jinghui70\\\", name:\\\"rainbow\\\"){releases(last:1){nodes{tagName}}}}\"}"))
//				.build();
//
//		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//		String result = Utils.substringAfter(response.body(), "tagName\":");
//		if (result.isEmpty())
//			throw new RuntimeException(response.body());
//		result = Utils.substringBetween(result, "\"");
//		Files.writeString(file, result);
	}
}

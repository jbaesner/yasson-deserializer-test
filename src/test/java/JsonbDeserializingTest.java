import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.spi.JsonbProvider;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import test.FlexibleMimeTypeJsonbDeserializer;
import test.NonParsingMimeTypeJsonbDeserializer;
import test.StaticMimeTypeJsonbDeserializer;

public class JsonbDeserializingTest {

	@Test
	public void testFlexibleMimeTypeJsonbDeserializer() throws Exception {
		System.out.println("parse with FlexibleMimeTypeJsonbDeserializer");
		JsonbConfig config = new JsonbConfig().withDeserializers(new FlexibleMimeTypeJsonbDeserializer()).withNullValues(true);
		Jsonb jsonb = JsonbProvider.provider().create().withConfig(config).build();
		parseAllFiles(jsonb);
	}

	@Test
	public void testNonParsingMimeTypeJsonbDeserializer() throws Exception {
		System.out.println("parse with NonParsingMimeTypeJsonbDeserializer");
		JsonbConfig config = new JsonbConfig().withDeserializers(new NonParsingMimeTypeJsonbDeserializer()).withNullValues(true);
		Jsonb jsonb = JsonbProvider.provider().create().withConfig(config).build();
		parseAllFiles(jsonb);
	}

	@Test
	@Ignore
	public void testStaticMimeTypeJsonbDeserializer() throws Exception {
		System.out.println("parse with StaticMimeTypeJsonbDeserializer");
		JsonbConfig config = new JsonbConfig().withDeserializers(new StaticMimeTypeJsonbDeserializer()).withNullValues(true);
		Jsonb jsonb = JsonbProvider.provider().create().withConfig(config).build();
		parseAllFiles(jsonb);
	}
	
	public void parseAllFiles(Jsonb jsonb) throws Exception {
		TestObject to = parseJson(jsonb, "mimeTypes.json", TestObject.class);
		TestObjectContainer toc = parseJson(jsonb, "mimeTypesInContainer.json", TestObjectContainer.class);
		TestObjectContainerContainer tocc = parseJson(jsonb, "mimeTypesInContainerInContainer.json", TestObjectContainerContainer.class);
		
		//first attribute is always parsed
		Assert.assertNotNull(to.getString1());
		Assert.assertNotNull(toc.getContainerAttribute1());
		Assert.assertNotNull(tocc.getContainerContainerAttribute1());
		
		//last attribute is not parsed in case the issue occurs
		Assert.assertNotNull(tocc.getContainerContainerAttribute2());
		Assert.assertNotNull(toc.getContainerAttribute2());
		Assert.assertNotNull(to.getString2());
				
	}
	
	
	private <T> T parseJson(Jsonb jsonb, String filename, Class<T> expectedType) throws Exception {
		Path filePath = Paths.get("src/test/resources/").resolve(filename);
		try (InputStream is = Files.newInputStream(filePath)){
			T testObject = jsonb.fromJson(is, expectedType);
			System.out.println("parsed from " + filename + ": " + testObject);
			return testObject;
		}
		
	}
}

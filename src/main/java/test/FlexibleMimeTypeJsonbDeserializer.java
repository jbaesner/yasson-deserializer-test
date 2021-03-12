package test;

import java.lang.reflect.Type;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.json.JsonObject;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * this Deserializer sall be able to read plain mimeType-string AND/OR mimeType-object-structure
 * 
 *
 */
public class FlexibleMimeTypeJsonbDeserializer implements JsonbDeserializer<MimeType> {

	public MimeType deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		try {
			
			// KEY_NAME
			if (!parser.hasNext()) {
				throw new JsonbException("invalid!");
			}
			JsonParser.Event event = parser.next();
			// VALUE_STRING or START_OBJECT
			if (!parser.hasNext()) {
				throw new JsonbException("invalid!");
			}
			event = parser.next();
			if (event == JsonParser.Event.VALUE_STRING) {
				return new MimeType(parser.getString());
			} else if (event == JsonParser.Event.START_OBJECT) {
				JsonObject jsonObject = parser.getObject();
				String primary = jsonObject.getString("primary");
				String sub = jsonObject.getString("sub");
				return new MimeType(primary, sub);
			} else {
				throw new JsonbException("invalid!");
			}
		} catch (MimeTypeParseException e) {
			throw new JsonbException("fail", e);
		}
	}

}

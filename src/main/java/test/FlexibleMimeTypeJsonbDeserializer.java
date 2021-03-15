package test;

import java.lang.reflect.Type;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.json.JsonObject;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.eclipse.yasson.internal.UserDeserializerParser;

/**
 * this Deserializer shall be able to read plain mimeType-string AND/OR mimeType-object-structure
 * 
 */
public class FlexibleMimeTypeJsonbDeserializer implements JsonbDeserializer<MimeType> {

	public MimeType deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		try {

			if (parser instanceof UserDeserializerParser) {

				// depending on the last Event we know how to parse the MimeType correctly...
				Event lastEvent = ((UserDeserializerParser) parser).getCurrentLevel().getLastEvent();

				switch (lastEvent) {

					case START_OBJECT:
						JsonObject jsonObject = parser.getObject();
						String primary = jsonObject.getString("primary");
						String sub = jsonObject.getString("sub");
						return new MimeType(primary, sub);

					case VALUE_STRING:
						return new MimeType(parser.getString());

					default:
						throw new JsonbException("Invalid event '" + lastEvent + "'");
				}
			} else {
				throw new JsonbException("Invalid parser type '" + parser.getClass().getName() + "', must be '"
						+ UserDeserializerParser.class.getName() + "'");
			}
		} catch (MimeTypeParseException e) {
			throw new JsonbException("fail", e);
		}
	}
}

package th.mfu.service.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Turns a LocalDate into "29-07-2026" in the JSON.
 *
 * This class used to sit next to the entities. It lives beside the DTOs now,
 * because deciding how a date LOOKS is a presentation concern, not a database
 * concern - the answer to the question on the "Question" slide.
 */
public class LocalDateSerializer extends StdSerializer<LocalDate> {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public LocalDateSerializer() {
        this(null);
    }

    public LocalDateSerializer(Class<LocalDate> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(localDate.format(FORMATTER));
    }
}

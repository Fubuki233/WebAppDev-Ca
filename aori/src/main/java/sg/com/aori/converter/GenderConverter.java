package sg.com.aori.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sg.com.aori.model.Customer.Gender;

/**
 * Converter for Gender enum to handle database values with hyphens
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.0
 */

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.name().replace("_", "-");
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String enumName = dbData.replace("-", "_");
        try {
            return Gender.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

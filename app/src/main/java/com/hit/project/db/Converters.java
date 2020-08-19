package com.hit.project.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.project.model.DogData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.room.TypeConverter;

public class Converters
{
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        if(value == null)
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(value, String[].class));
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if(list == null)
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(list);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static DogData.DogSize fromName(String value) {
        return value == null ? null : DogData.DogSize.valueOf(value);
    }

    @TypeConverter
    public static String fromDogSize(DogData.DogSize value) {
        return value == null ? null : value.name();
    }
}
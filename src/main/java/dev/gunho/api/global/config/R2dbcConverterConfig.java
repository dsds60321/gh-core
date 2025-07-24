package dev.gunho.api.global.config;

import dev.gunho.api.bingous.v1.model.enums.DeviceType;
import dev.gunho.api.bingous.v1.model.enums.Gender;
import dev.gunho.api.bingous.v1.model.enums.UserStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.lang.NonNull;

import java.util.Arrays;

@Configuration
public class R2dbcConverterConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        return new R2dbcCustomConversions(
                CustomConversions.StoreConversions.NONE,
                Arrays.asList(
                        new StringToGenderConverter(),
                        new GenderToStringConverter(),
                        new StringToUserStatusConverter(),
                        new UserStatusToStringConverter(),
                        new StringToDeviceTypeConverter(),
                        new DeviceTypeToStringConverter()
                )
        );
    }

    @ReadingConverter
    public static class StringToGenderConverter implements Converter<String, Gender> {
        @Override
        public Gender convert(@NonNull String source) {
            return Gender.fromValue(source);
        }
    }

    @WritingConverter
    public static class GenderToStringConverter implements Converter<Gender, String> {
        @Override
        public String convert(@NonNull Gender source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    public static class StringToUserStatusConverter implements Converter<String, UserStatus> {
        @Override
        public UserStatus convert(@NonNull String source) {
            return UserStatus.fromValue(source);
        }
    }

    @WritingConverter
    public static class UserStatusToStringConverter implements Converter<UserStatus, String> {
        @Override
        public String convert(@NonNull UserStatus source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    public static class StringToDeviceTypeConverter implements Converter<String, DeviceType> {
        @Override
        public DeviceType convert(@NonNull String source) {
            return DeviceType.fromValue(source);
        }
    }

    @WritingConverter
    public static class DeviceTypeToStringConverter implements Converter<DeviceType, String> {
        @Override
        public String convert(@NonNull DeviceType source) {
            return source.getValue();
        }
    }
}

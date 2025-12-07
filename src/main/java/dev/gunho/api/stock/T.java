package dev.gunho.api.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class T {

    private static final Path DEFAULT_INPUT_JSON_PATH = Paths.get("stocks.json");
    private static final Path DEFAULT_OUTPUT_SQL_PATH = Paths.get("stock_meta_insert.sql");
    private static final int BATCH_SIZE = 500;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        Path input = args.length > 0 ? Paths.get(args[0]) : DEFAULT_INPUT_JSON_PATH;
        Path output = args.length > 1 ? Paths.get(args[1]) : DEFAULT_OUTPUT_SQL_PATH;

        if (!Files.exists(input)) {
            System.err.println("JSON 파일이 존재하지 않습니다: " + input.toAbsolutePath());
            return;
        }

        try (InputStream in = Files.newInputStream(input);
             JsonParser parser = OBJECT_MAPPER.getFactory().createParser(in);
             BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

            writer.write("-- auto generated INSERT for stock_meta\n");
            writer.write("-- source: " + input.toAbsolutePath() + "\n\n");

            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IllegalStateException("JSON 루트는 객체여야 합니다.");
            }

            List<String> values = new ArrayList<>(BATCH_SIZE);
            long totalCount = 0;

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();

                if (!"data".equals(fieldName)) {
                    parser.skipChildren();
                    continue;
                }

                if (parser.currentToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("data 필드는 배열이어야 합니다.");
                }

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    StockRecord record = OBJECT_MAPPER.readValue(parser, StockRecord.class);
                    if (!record.hasEssentialFields()) {
                        continue;
                    }

                    String symbolSql = toSqlString(record.getSymbol());
                    String nameSql = toSqlString(record.getName());
                    String exchangeSql = toSqlStringOrNull(record.getExchange());
                    String currencySql = toSqlString(defaultIfBlank(record.getCurrency(), "USD"));
                    String countrySql = toSqlString(defaultIfBlank(record.getCountry(), "USA"));
                    String activeYnSql = toSqlString(record.resolveActiveYn());

                    String valueRow = "(" +
                            symbolSql + ", " +
                            nameSql + ", " +
                            (record.isEtf() ? 1 : 0) + ", " +
                            exchangeSql + ", " +
                            currencySql + ", " +
                            countrySql + ", " +
                            activeYnSql +
                            ")";

                    values.add(valueRow);
                    totalCount++;

                    if (values.size() >= BATCH_SIZE) {
                        writeBatchInsert(writer, values);
                        values.clear();
                    }
                }
            }

            if (!values.isEmpty()) {
                writeBatchInsert(writer, values);
            }

            System.out.printf(Locale.ROOT,
                    "총 %,d건에 대한 INSERT SQL 생성 완료: %s%n",
                    totalCount,
                    output.toAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeBatchInsert(BufferedWriter writer, List<String> values) throws IOException {
        writer.write("INSERT IGNORE INTO stock_meta ");
        writer.write("(symbol, name, is_etf, exchange, currency, country, active_yn)\n");
        writer.write("VALUES\n");
        writer.write(String.join(",\n", values));
        writer.write(";\n\n");
    }

    private static String toSqlString(String s) {
        if (s == null) {
            return "NULL";
        }
        String escaped = s.replace("'", "''").trim();
        return "'" + escaped + "'";
    }

    private static String toSqlStringOrNull(String s) {
        if (isBlank(s)) {
            return "NULL";
        }
        return toSqlString(s);
    }

    private static String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class StockRecord {
        private String symbol;
        private String name;
        private String exchange;
        private String currency;
        private String country;
        private String type;
        @JsonProperty("is_etf")
        private Boolean explicitEtf;
        @JsonProperty("active_yn")
        private String activeYn;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getExplicitEtf() {
            return explicitEtf;
        }

        public void setExplicitEtf(Boolean explicitEtf) {
            this.explicitEtf = explicitEtf;
        }

        public String getActiveYn() {
            return activeYn;
        }

        public void setActiveYn(String activeYn) {
            this.activeYn = activeYn;
        }

        boolean hasEssentialFields() {
            return !isBlank(symbol) && !isBlank(name);
        }

        boolean isEtf() {
            if (explicitEtf != null) {
                return explicitEtf;
            }
            if (type != null && type.toLowerCase(Locale.ROOT).contains("etf")) {
                return true;
            }
            return name != null && name.toLowerCase(Locale.ROOT).contains("etf");
        }

        String resolveActiveYn() {
            if (isBlank(activeYn)) {
                return "Y";
            }
            return "Y".equalsIgnoreCase(activeYn.trim()) ? "Y" : "N";
        }
    }
}

package com.powerlife145.autocryptotrader.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpbitTickerResponse {
    private String market;

    @JsonProperty("trade_date")
    private String tradeDate; // yyyymmdd

    @JsonProperty("trade_time")
    private String tradeTime; // HHmmss

    @JsonProperty("trade_price")
    private Double tradePrice;

    @JsonProperty("opening_price")
    private Double openingPrice;

    @JsonProperty("high_price")
    private Double highPrice;

    @JsonProperty("low_price")
    private Double lowPrice;

    @JsonProperty("acc_trade_price_24h")
    private Double accTradePrice24h;

    private Long timestamp;
}

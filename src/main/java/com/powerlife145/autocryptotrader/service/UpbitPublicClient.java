package com.powerlife145.autocryptotrader.service;


import com.powerlife145.autocryptotrader.dto.UpbitTickerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpbitPublicClient {

    private final WebClient upbitWebClient;

    public Mono<UpbitTickerResponse> getTickerMono(String market) {
        return upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/ticker")
                        .queryParam("markets", market)
                        .build())
                // .retrieve() 대신 .exchangeToMono() 사용
                .exchangeToMono(response -> {
                    // 1. HTTP 상태 코드를 확인하여 성공/실패를 명시적으로 분기
                    if (response.statusCode().is2xxSuccessful()) {
                        // 2. 성공 경로: 응답 바디를 원하는 타입으로 변환하여 반환
                        return response.bodyToMono(UpbitTickerResponse[].class);
                    } else {
                        // 3. 에러 경로: 에러 응답 바디를 읽어 커스텀 에러 Mono를 생성하여 반환
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("No Error Body") // 에러 바디가 비어있을 경우를 대비
                                .flatMap(errorBody -> Mono.error(
                                        new IllegalStateException("Upbit API Error - Status: " + response.statusCode() + ", Body: " + errorBody)
                                ));
                    }
                })
                .flatMap(arr -> {
                    // 응답 배열이 비어있는 경우에 대한 처리
                    if (arr == null || arr.length == 0) {
                        return Mono.error(new IllegalStateException("Ticker response array from Upbit is empty."));
                    }
                    // 성공적으로 첫 번째 요소를 반환
                    return Mono.just(arr[0]);
                })
                // 최종적으로 발생할 수 있는 모든 예외를 포괄적으로 처리
                .onErrorResume(e -> {
                    // 우리가 직접 생성한 예외는 그대로 전달
                    if (e instanceof IllegalStateException) {
                        return Mono.error(e);
                    }
                    // 그 외 네트워크 연결 실패 등 다른 종류의 예외를 처리
                    return Mono.error(new IllegalStateException("API call failed for market: " + market, e));
                });
    }

    /** 동기형(테스트·컨트롤러 편의용). 실서비스에선 비동기/논블로킹 사용 권장 */
    public UpbitTickerResponse getTicker(String market) {
        return getTickerMono(market).block();
    }

    /** 여러 마켓 동시 조회 확장 */
    public Mono<List<UpbitTickerResponse>> getTickers(List<String> markets) {
        String joined = String.join(",", markets);
        return upbitWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/ticker")
                        .queryParam("markets", joined)
                        .build())
                .retrieve()
                .bodyToMono(UpbitTickerResponse[].class)
                .map(List::of);
    }
}

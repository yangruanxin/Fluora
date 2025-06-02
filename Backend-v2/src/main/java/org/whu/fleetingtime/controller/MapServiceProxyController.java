package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.whu.fleetingtime.exception.BizException;

import java.net.SocketTimeoutException;


@RestController
@RequestMapping("/api/map")
public class MapServiceProxyController {
    @Value("${baidu.map.ak}")
    private String baiduAk;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MapServiceProxyController.class);

    private static final String BAIDU_REVERSE_GEOCODE_URL = "https://api.map.baidu.com/reverse_geocoding/v3/";

    @GetMapping("/reverse-geocode")
    public String reverseGeocode(
            HttpServletRequest request,
            @RequestParam("lat") String latitude,
            @RequestParam("lng") String longitude
    ) {
        // request url
        String userId = (String) request.getAttribute("userId");
        logger.info("【全球逆地理编码请求转发】用户：{}, 纬度：{}, 经度：{}", userId, latitude, longitude);
        String url = BAIDU_REVERSE_GEOCODE_URL +
                "?ak=" + baiduAk +
                "&extensions_poi=1" +
                "&entire_poi=1" +
                "sort_strategy=distance" +
                "&output=json" +
                "&coordtype=bd09ll&coordtype=wgs84ll" +
                "&location=" + latitude + "," + longitude;
        try {
            String result = restTemplate.getForObject(url, String.class);
            logger.info("【全球逆地理编码请求返回】" + (result != null ? result.substring(0, 50) : "null"));
        } catch (ResourceAccessException e) {
            // 捕获超时或网络异常
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new BizException("请求超时");
            } else if (e.getCause() instanceof ConnectTimeoutException) {
                throw new BizException("请求超时");
            }
        } catch (Exception e) {
            // 其他异常
            throw new BizException("请求代理错误");
        }
        return restTemplate.getForObject(url, String.class);
    }
}
